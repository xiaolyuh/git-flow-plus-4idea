package com.github.xiaolyuh.git;

import com.alibaba.fastjson.JSON;
import com.github.xiaolyuh.Constants;
import com.github.xiaolyuh.action.options.InitOptions;
import com.github.xiaolyuh.action.options.MergeRequestOptions;
import com.github.xiaolyuh.action.options.TagOptions;
import com.github.xiaolyuh.action.vo.BranchVo;
import com.github.xiaolyuh.action.vo.CommitMessageVo;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.utils.CollectionUtils;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.GitBranchUtil;
import com.github.xiaolyuh.utils.StringUtils;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.vcsUtil.VcsUtil;
import git4idea.GitUtil;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitSimpleEventDetector;
import git4idea.i18n.GitBundle;
import git4idea.merge.GitMergeCommittingConflictResolver;
import git4idea.merge.GitMerger;
import git4idea.repo.GitRepository;
import git4idea.util.GitFileUtils;
import git4idea.util.GitUIUtil;
import git4idea.util.StringScanner;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yuhao.wang3
 * @since 2020/3/23 9:53
 */
public class GitFlowPlusImpl implements GitFlowPlus {
    private Git git = Git.getInstance();

    @Override
    public void addConfigToGit(GitRepository repository) {
        try {
            String filePath = repository.getProject().getBasePath() + File.separator + Constants.CONFIG_FILE_NAME;
            FilePath path = VcsUtil.getFilePath(filePath);
            GitFileUtils.addPaths(repository.getProject(), repository.getRoot(), Lists.newArrayList(path));
        } catch (VcsException e) {

        }
    }

    @Override
    public GitCommandResult newNewBranchBaseRemoteMaster(@NotNull GitRepository repository, @Nullable String master, @NotNull String newBranchName) {
        git.fetchNewBranchByRemoteMaster(repository, master, newBranchName);
        git.checkout(repository, newBranchName);

        // 推送分支
        return git.push(repository, newBranchName, true);
    }

    @Override
    public GitCommandResult newNewBranchByLocalBranch(@NotNull GitRepository repository, @Nullable String localBranchName, @NotNull String newBranchName) {
        git.checkout(repository, localBranchName);
        git.branch(repository, newBranchName);
        return git.checkout(repository, newBranchName);
    }

    @Override
    public void deleteBranch(@NotNull GitRepository repository,
                             @Nullable String branchName,
                             @Nullable boolean isDeleteLocalBranch) {

        if (isDeleteLocalBranch) {
            git.deleteLocalBranch(repository, branchName);
        }
        git.deleteRemoteBranch(repository, branchName);
    }

    @Override
    public GitCommandResult deleteBranch(@NotNull GitRepository repository,
                                         @Nullable String checkoutBranchName,
                                         @Nullable String branchName) {

        git.checkout(repository, checkoutBranchName);
        git.deleteRemoteBranch(repository, branchName);
        return git.deleteLocalBranch(repository, branchName);
    }

    @Override
    public GitCommandResult deleteLocalBranch(@NotNull GitRepository repository,
                                              @Nullable String checkoutBranchName,
                                              @Nullable String branchName) {

        git.checkout(repository, checkoutBranchName);
        return git.deleteLocalBranch(repository, branchName);
    }

    @Override
    public String getCurrentBranch(@NotNull Project project) {
        GitRepository repository = GitBranchUtil.getCurrentRepository(project);
        return repository.getCurrentBranch().getName();
    }

    @Override
    public String getRemoteLastCommit(@NotNull GitRepository repository, @Nullable String remoteBranchName) {
        git.fetch(repository);
        GitCommandResult result = git.showRemoteLastCommit(repository, remoteBranchName);
        GitCommandResult lastReleaseTimeResult = git.getLastReleaseTime(repository);
        String msg = result.getOutputAsJoinedString();
        msg = msg.replaceFirst("Author:", "\r\n  Author: ");
        msg = msg.replaceFirst("-Message:", ";\r\n  Message: ");

        String lastReleaseTime = lastReleaseTimeResult.getOutputAsJoinedString();
        if (StringUtils.isNotBlank(lastReleaseTime)) {
            lastReleaseTime = lastReleaseTime.substring(lastReleaseTime.indexOf("@{") + 2, lastReleaseTime.indexOf(" +"));
            msg = msg + "\r\n  Date: " + lastReleaseTime;
        }

        return msg;
    }

    @Override
    public GitCommandResult getLocalLastCommit(@NotNull GitRepository repository, @Nullable String branchName) {
        git.fetch(repository);
        return git.showLocalLastCommit(repository, branchName);
    }

    @Override
    public GitCommandResult mergeBranchAndPush(GitRepository repository, String currentBranch, String targetBranch,
                                               TagOptions tagOptions) {
        String releaseBranch = ReadAction.compute(() -> ConfigUtil.getConfig(repository.getProject()).get().getReleaseBranch());
        // 判断目标分支是否存在
        GitCommandResult result = checkTargetBranchIsExist(repository, targetBranch);
        if (Objects.nonNull(result) && !result.success()) {
            return result;
        }

        // 发布完成拉取release最新代码
        if (Objects.nonNull(tagOptions)) {
            result = checkoutTargetBranchAndPull(repository, releaseBranch);
            if (!result.success()) {
                return result;
            }
        }

        // 切换到目标分支, pull最新代码
        result = checkoutTargetBranchAndPull(repository, targetBranch);
        if (!result.success()) {
            return result;
        }

        // 合并代码
        GitSimpleEventDetector mergeConflict = new GitSimpleEventDetector(GitSimpleEventDetector.Event.MERGE_CONFLICT);
        String sourceBranch = Objects.nonNull(tagOptions) ? releaseBranch : currentBranch;

        result = git.merge(repository, sourceBranch, targetBranch, mergeConflict);

        boolean allConflictsResolved = true;
        if (mergeConflict.hasHappened()) {
            // 解决冲突
            allConflictsResolved = new MyMergeConflictResolver(repository, currentBranch, targetBranch).merge();
        }

        if (!result.success() && !allConflictsResolved) {
            return result;
        }

        // 发布完成打tag
        if (Objects.nonNull(tagOptions)) {
            result = git.createNewTag(repository, tagOptions.getTagName(), tagOptions.getMessage());
            if (!result.success()) {
                return result;
            }
        }

        // push代码
        result = git.push(repository, targetBranch, false);
        if (!result.success()) {
            return result;
        }

        // 切换到当前分支
        return git.checkout(repository, currentBranch);
    }

    @Override
    public GitCommandResult mergeBranch(GitRepository repository, String sourceBranch, String targetBranch) {
        // 判断目标分支是否存在
        GitCommandResult result = checkTargetBranchIsExist(repository, sourceBranch);
        if (Objects.nonNull(result) && !result.success()) {
            return result;
        }

        // 源分支拉取最新代码
        result = checkoutTargetBranchAndPull(repository, sourceBranch);
        if (!result.success()) {
            return result;
        }
        git.checkout(repository, targetBranch);

        // 合并代码
        GitSimpleEventDetector mergeConflict = new GitSimpleEventDetector(GitSimpleEventDetector.Event.MERGE_CONFLICT);

        result = git.merge(repository, sourceBranch, targetBranch, mergeConflict);

        boolean allConflictsResolved = true;
        if (mergeConflict.hasHappened()) {
            // 解决冲突
            allConflictsResolved = new MyMergeConflictResolver(repository, sourceBranch, targetBranch).merge();
        }

        if (!result.success() && !allConflictsResolved) {
            return result;
        }

        // 切换到当前分支
        return git.checkout(repository, sourceBranch);
    }

    @Override
    public boolean lock(GitRepository repository, String currentBranch) {
        GitCommandResult result = git.push(repository, currentBranch, Constants.LOCK_BRANCH_NAME, false);

        if (result.success() && isNewBranch(result)) {
            return true;
        }

        return false;
    }

    @Override
    public GitCommandResult unlock(GitRepository repository) {
        return git.deleteRemoteBranch(repository, Constants.LOCK_BRANCH_NAME);
    }

    @Override
    public boolean isLock(Project project) {

        return GitBranchUtil.getRemoteBranches(project).contains(Constants.LOCK_BRANCH_NAME);
    }

    @Override
    public boolean isLock(GitRepository repository) {
        git.fetch(repository);
        repository.update();
        return isLock(repository.getProject());
    }

    @Override
    public boolean isExistChangeFile(@NotNull Project project) {

        Collection<Change> changes = ChangeListManager.getInstance(project).getAllChanges();
        if (CollectionUtils.isNotEmpty(changes)) {
            StringBuffer builder = new StringBuffer();
            changes.parallelStream().forEach(change -> builder.append(change.toString() + "\r\n"));
            Messages.showMessageDialog(project, builder.toString(),
                    String.format(I18n.getContent(I18nKey.CHANGE_FILE_VALVE$FILE_NOT_SUBMITTED)), Messages.getWarningIcon());
            return true;
        }
        return false;
    }


    @Override
    public String getUserEmail(GitRepository repository) {
        try {
            GitCommandResult result = git.getUserEmail(repository);
            String output = result.getOutputOrThrow(1);
            int pos = output.indexOf('\u0000');
            if (result.getExitCode() != 0 || pos == -1) {
                return "";
            }
            return output.substring(0, pos);
        } catch (VcsException e) {
            return "";
        }
    }

    @Override
    public boolean isExistTag(GitRepository repository, String tagName) {
        Set<String> myExistingTags = new HashSet<>();

        GitCommandResult result = git.tagList(repository);
        if (!result.success()) {
            GitUIUtil.showOperationError(repository.getProject(), GitBundle.message("tag.getting.existing.tags"), result.getErrorOutputAsJoinedString());
            throw new ProcessCanceledException();
        }
        for (StringScanner s = new StringScanner(result.getOutputAsJoinedString()); s.hasMoreData(); ) {
            String line = s.line();
            if (line.length() == 0) {
                continue;
            }
            myExistingTags.add(line);
        }

        return myExistingTags.contains(tagName);
    }

    @Override
    public GitCommandResult mergeRequest(GitRepository repository, String sourceBranch, String targetBranch, MergeRequestOptions mergeRequestOptions) {
        return git.mergeRequest(repository, sourceBranch, targetBranch, mergeRequestOptions);
    }

    private GitCommandResult checkTargetBranchIsExist(GitRepository repository, String
            targetBranch) {
        // 判断本地是否存在分支
        if (!GitBranchUtil.getLocalBranches(repository.getProject()).contains(targetBranch)) {
            if (GitBranchUtil.getRemoteBranches(repository.getProject()).contains(targetBranch)) {
                return git.checkoutNewBranch(repository, targetBranch);
            } else {
                String master = ConfigUtil.getConfig(repository.getProject()).get().getMasterBranch();
                return newNewBranchBaseRemoteMaster(repository, master, targetBranch);
            }
        }

        return null;
    }


    private boolean isNewBranch(GitCommandResult result) {
        return result.getOutputAsJoinedString().contains("new branch") || result.getErrorOutputAsJoinedString().contains("new branch");
    }

    @Override
    public GitCommandResult checkoutTargetBranchAndPull(GitRepository repository, String targetBranch) {
        // 切换到目标分支
        git.checkout(repository, targetBranch);

        // pull最新代码
        return git.pull(repository, targetBranch);
    }

    @Override
    public List<BranchVo> getBranchList(GitRepository repository) {
        InitOptions initOptions = ConfigUtil.getConfig(repository.getProject()).get();
        GitCommandResult branchList = git.getAllBranchList(repository);
        List<String> output = branchList.getOutput();
        return output.stream().map(row -> row.replace("origin/", "")).filter(row -> {
            String[] msg = row.split("@@@");
            if (msg[2].equalsIgnoreCase(initOptions.getMasterBranch())) {
                return false;
            }
            if (msg[2].equalsIgnoreCase(initOptions.getReleaseBranch())) {
                return false;
            }
            if (msg[2].equalsIgnoreCase(initOptions.getTestBranch())) {
                return false;
            }
            if (msg[2].equalsIgnoreCase("HEAD")) {
                return false;
            }
            if (msg[2].endsWith("_mr")) {
                return false;
            }
            return true;
        }).map(row -> {
            String[] msg = row.split("@@@");
            BranchVo branchVo = new BranchVo();
            branchVo.setLastCommitDate(msg[0]);
            branchVo.setCreateUser(msg[1]);
            branchVo.setBranch(msg[2]);
            return branchVo;
        }).distinct().sorted((o1, o2) -> o2.getLastCommitDate().compareTo(o1.getLastCommitDate())).collect(Collectors.toList());
    }

    @Override
    public List<String> getMergedBranchList(GitRepository repository) {
        InitOptions initOptions = ConfigUtil.getConfig(repository.getProject()).get();
        String masterBranch = initOptions.getMasterBranch();

        // 执行查询操作
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String data = LocalDateTime.now().minusYears(2).format(df);
        GitCommandResult branchList = git.getMergedBranchList(repository, data);
        List<String> output = branchList.getOutput();
        List<String> branchs = output.stream()
                .filter(StringUtils::isNotBlank)
                .filter(message -> message.startsWith("Merge branch"))
                .map(row -> {
                    // Merge branch 'feature/add_elk' into release-body:
                    String[] msg = row.replace("'", "").split(" ");
                    if (msg.length < 3) {
                        return null;
                    }
                    return msg[2].replace("_mr", "");
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return branchs;
    }

    @Override
    public CommitMessageVo getCommitMessage(GitRepository repository, String commitId) {
        GitCommandResult gitCommandResult = git.showLocalLastCommitFull(repository, commitId);
//        0 = "commit 392d550b61119a1c79f48c79e4283b6aa6b69ab5"
//        1 = "Author:     wangyuhao01@longfor.com <wangyuhao01@longfor.com>"
//        2 = "AuthorDate: 2023-02-17_18:00:23"
//        3 = "Commit:     wangyuhao01@longfor.com <wangyuhao01@longfor.com>"
//        4 = "CommitDate: 2023-02-17_18:00:23"
        CommitMessageVo commitMessageVo = new CommitMessageVo();
        String commit = gitCommandResult.getOutput().get(0).replace("commit ", "");
        String gitAuthor = gitCommandResult.getOutput().get(1);
        String author = gitAuthor.substring(gitAuthor.indexOf("Author:     ") + 12, gitAuthor.indexOf(" <"));
        String email = gitAuthor.substring(gitAuthor.indexOf(" <") + 2, gitAuthor.indexOf(">"));
        String commitDate = gitCommandResult.getOutput().get(4).replace("CommitDate: ", "").replace("_", " ");

        commitMessageVo.setCommitId(commit);
        commitMessageVo.setCommitDate(commitDate);
        commitMessageVo.setAuthor(author);
        commitMessageVo.setEmail(email);
        commitMessageVo.setFullMessage(JSON.toJSONString(gitCommandResult.getOutput()));

        final String currentBranch = repository.getCurrentBranch().getName();
        commitMessageVo.setBranch(currentBranch);

        gitCommandResult = git.getConfig(repository, "remote.origin.url");
        String gitUrl = gitCommandResult.getOutput().get(0);
        commitMessageVo.setProject(gitUrl.substring(gitUrl.lastIndexOf("/") + 1, gitUrl.lastIndexOf(".git")));

        gitCommandResult = git.showFile(repository, commitMessageVo.getCommitId());
        List<String> files = gitCommandResult.getOutput().stream().filter(row -> row.startsWith(":")).collect(Collectors.toList());
        commitMessageVo.setModifyFile(JSON.toJSONString(files));

        gitCommandResult = git.showCommitMessage(repository, commitMessageVo.getCommitId());
        String message = gitCommandResult.getOutput().stream().collect(Collectors.joining("\n"));
        String[] messageArry = message.split("-@message@-");
        commitMessageVo.setSummary(messageArry[0]);
        commitMessageVo.setDescription(messageArry[1]);
        commitMessageVo.setShortId(messageArry[2]);
        return commitMessageVo;
    }


    private class MyMergeConflictResolver extends GitMergeCommittingConflictResolver {
        String currentBranch;
        String targetBranch;

        MyMergeConflictResolver(GitRepository repository, String currentBranch, String targetBranch) {
            super(repository.getProject(), git4idea.commands.Git.getInstance(), new GitMerger(repository.getProject()),
                    GitUtil.getRootsFromRepositories(Lists.newArrayList(repository)), new Params(), true);
            this.currentBranch = currentBranch;
            this.targetBranch = targetBranch;
        }

        @Override
        protected void notifyUnresolvedRemain() {
            notifyWarning(I18n.getContent(I18nKey.MERGE_CONFLICT_TITLE), String.format(I18n.getContent(I18nKey.MERGE_CONFLICT_CONTENT), currentBranch, targetBranch));
        }
    }


}
