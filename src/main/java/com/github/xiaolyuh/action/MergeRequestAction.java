package com.github.xiaolyuh.action;

import com.github.xiaolyuh.action.options.MergeRequestOptions;
import com.github.xiaolyuh.git.GitFlowPlus;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.notify.NotifyUtil;
import com.github.xiaolyuh.notify.ThirdPartyService;
import com.github.xiaolyuh.ui.MergeRequestDialog;
import com.github.xiaolyuh.utils.CollectionUtils;
import com.github.xiaolyuh.utils.GitBranchUtil;
import com.github.xiaolyuh.valve.merge.Valve;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * merge request
 *
 * @author yuhao.wang3
 */
public class MergeRequestAction extends AbstractMergeAction {
    protected GitFlowPlus gitFlowPlus = GitFlowPlus.getInstance();
    protected ThirdPartyService thirdPartyService = ThirdPartyService.getInstance();

    public MergeRequestAction() {
        super("Merge Request", "发起 code review", IconLoader.getIcon("/icons/mergeToTest.svg", AbstractNewBranchAction.class));
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setEnabled(true);
        event.getPresentation().setText(I18n.getContent(I18nKey.MERGE_REQUEST_ACTION$TEXT));
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        assert project != null;
        final String currentBranch = gitFlowPlus.getCurrentBranch(project);
        final GitRepository repository = GitBranchUtil.getCurrentRepository(project);
        if (Objects.isNull(repository)) {
            return;
        }

        // 校验是否有文件没有提交
        if (gitFlowPlus.isExistChangeFile(project)) {
            return;
        }

        // 获取最后一次提交信息
        GitCommandResult result = gitFlowPlus.getLocalLastCommit(repository, currentBranch);
        String[] msgs = result.getOutputAsJoinedString().split("-body:");
        MergeRequestDialog mergeRequestDialog = new MergeRequestDialog(project, msgs.length >= 1 ? msgs[0] : "", msgs.length >= 2 ? msgs[1] : "");
        mergeRequestDialog.show();
        if (!mergeRequestDialog.isOK()) {
            return;
        }

        new Task.Backgroundable(project, "Merge Request", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                NotifyUtil.notifyGitCommand(event.getProject(), "===================================================================================");

                // 新建临时分支
                String tempBranchName = currentBranch + "_mr";
                GitCommandResult result = gitFlowPlus.deleteBranch(repository, currentBranch, tempBranchName);
                result = gitFlowPlus.newNewBranchByLocalBranch(repository, currentBranch, tempBranchName);
                if (!result.success()) {
                    NotifyUtil.notifyError(project, "Error", result.getErrorOutputAsJoinedString());
                    return;
                }

                MergeRequestOptions mergeRequestOptions = mergeRequestDialog.getMergeRequestOptions();

                // 将目标分支合并到当前future分支
                String targetBranch = mergeRequestOptions.getTargetBranch();
                result = gitFlowPlus.mergeBranch(repository, targetBranch, tempBranchName);
                if (!result.success()) {
                    NotifyUtil.notifyError(project, "Error", result.getErrorOutputAsJoinedString());
                    return;
                }

                // 发起merge request
                result = gitFlowPlus.mergeRequest(repository, tempBranchName, targetBranch, mergeRequestOptions);
                if (!result.success()) {
                    NotifyUtil.notifyError(project, "Error", result.getErrorOutputAsJoinedString());
                }
                NotifyUtil.notifySuccess(project, "Success", result.getErrorOutputAsHtmlString());

                // 删除本地临时分支
                gitFlowPlus.deleteLocalBranch(repository, currentBranch, tempBranchName);

                // 消息通知
                if (CollectionUtils.isNotEmpty(result.getErrorOutput()) && result.getErrorOutput().size() > 3) {
                    String address = result.getErrorOutput().get(2);
                    address = address.split("   ")[1];
                    BrowserUtil.browse(address);

                    thirdPartyService.mergeRequestNotify(repository, mergeRequestOptions, address);
                }

                // 刷新
                repository.update();
                assert myProject != null;
                myProject.getMessageBus().syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repository);
                VirtualFileManager.getInstance().asyncRefresh(null);
            }
        }.queue();
    }

    @Override
    protected String getTargetBranch(Project project) {
        return null;
    }

    @Override
    protected String getDialogTitle(Project project) {
        return null;
    }

    @Override
    protected String getTaskTitle(Project project) {
        return null;
    }

    @Override
    protected List<Valve> getValves() {
        return null;
    }


}



