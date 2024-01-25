package com.github.xiaolyuh.git;

import com.github.xiaolyuh.action.options.MergeRequestOptions;
import com.intellij.openapi.components.ServiceManager;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitLineHandlerListener;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yuhao.wang3
 * @since 2020/4/7 14:36
 */
public interface Git {
    /**
     * 获取实例
     *
     * @return GbmGit
     */
    @NotNull
    static Git getInstance() {
        return ServiceManager.getService(Git.class);
    }

    /**
     * 切换分支
     *
     * @param repository gitRepository
     * @param reference  要切换的分支
     * @return
     */
    @NotNull
    GitCommandResult checkout(@NotNull GitRepository repository,
                              @NotNull String reference);

    /**
     * 将远程分支拉到本地
     *
     * @param repository    gitRepository
     * @param newBranchName 新建分支名称
     * @return
     */
    GitCommandResult checkoutNewBranch(GitRepository repository, String newBranchName);


    /**
     * 基于远程master分支新建分支，分支不会和远程分支建立关联
     *
     * @param repository    gitRepository
     * @param master        master
     * @param newBranchName newBranchName
     * @return
     */
    GitCommandResult fetchNewBranchByRemoteMaster(GitRepository repository, String master, String newBranchName);

    /**
     * 已本地分支拉取一个新分支
     *
     * @param repository    repository
     * @param newBranchName 新建分支
     * @return GitCommandResult
     */
    GitCommandResult branch(@NotNull GitRepository repository, @NotNull String newBranchName);

    /**
     * 重建
     *
     * @param repository    gitRepository
     * @param oldBranch     原来分支名称
     * @param newBranchName 新分支名称
     * @return GitCommandResult
     */
    GitCommandResult renameBranch(@NotNull GitRepository repository,
                                  @Nullable String oldBranch,
                                  @NotNull String newBranchName);

    /**
     * push 本地分支到远程
     *
     * @param repository      gitRepository
     * @param localBranchName 本地分支名称
     * @param isNewBranch     是否是新建分支
     * @return
     */
    GitCommandResult push(GitRepository repository, String localBranchName, boolean isNewBranch);

    /**
     * push 本地分支到远程
     *
     * @param repository       gitRepository
     * @param localBranchName  分支名称
     * @param remoteBranchName 是否是新建分支
     * @param isNewBranch      是否是新建分支
     * @return
     */
    GitCommandResult push(GitRepository repository, String localBranchName, String remoteBranchName, boolean isNewBranch);

    /**
     * 删除远程分支 git push origin --delete dev
     *
     * @param repository gitRepository
     * @param branchName branchName
     * @return GitCommandResult
     */
    GitCommandResult deleteRemoteBranch(@NotNull GitRepository repository, @Nullable String branchName);

    /**
     * 删除本地分支 git branch -D dev
     *
     * @param repository gitRepository
     * @param branchName branchName
     * @return GitCommandResult
     */
    GitCommandResult deleteLocalBranch(@NotNull GitRepository repository, @Nullable String branchName);

    /**
     * 查看对应分支最后一次提交信息
     *
     * @param repository       gitRepository
     * @param remoteBranchName 分支名称
     * @return
     */
    GitCommandResult showRemoteLastCommit(@NotNull GitRepository repository, @Nullable String remoteBranchName);

    /**
     * 查看本地分支最后一次提交信息
     *
     * @param repository gitRepository
     * @param branchName 分支名称
     * @return
     */
    GitCommandResult showLocalLastCommit(@NotNull GitRepository repository, @Nullable String branchName);

    /**
     * 获取最后的发布时间
     *
     * @param repository
     * @return
     */
    GitCommandResult getLastReleaseTime(@NotNull GitRepository repository);

    /**
     * 创建tag
     *
     * @param repository gitRepository
     * @param tagName    tag名称
     * @param message    备注信息
     * @return
     */
    GitCommandResult createNewTag(@NotNull GitRepository repository, @Nullable String tagName, @Nullable String message);

    /**
     * 获取Tag列表
     *
     * @param repository gitRepository
     * @return
     */
    GitCommandResult tagList(@NotNull GitRepository repository);

    /**
     * git fetch origin
     *
     * @param repository gitRepository
     * @return
     */
    GitCommandResult fetch(@NotNull GitRepository repository);

    /**
     * pull代码
     *
     * @param repository gitRepository
     * @param branchName branchName
     * @return
     */
    GitCommandResult pull(GitRepository repository, @Nullable String branchName);

    /**
     * merge
     *
     * @param repository   gitRepository
     * @param sourceBranch 需要merge的分支
     * @param targetBranch 目标分支（合并到的分支）
     * @param listeners    GitLineHandlerListener
     * @return
     */
    GitCommandResult merge(@NotNull GitRepository repository, @NotNull String sourceBranch, @NotNull String targetBranch, @NotNull GitLineHandlerListener... listeners);


    /**
     * 获取当前git账户
     *
     * @param repository gitRepository
     * @return
     */
    GitCommandResult getUserEmail(GitRepository repository);

    /**
     * merge request
     *
     * @param repository          gitRepository
     * @param sourceBranch        需要merge的分支
     * @param targetBranch        目标分支（合并到的分支）
     * @param mergeRequestOptions merge request参数
     * @return
     */
    GitCommandResult mergeRequest(GitRepository repository, String sourceBranch, String targetBranch, MergeRequestOptions mergeRequestOptions);

    /**
     * 获取全部分支列表
     *
     * @param repository gitRepository
     * @return
     */
    GitCommandResult getAllBranchList(GitRepository repository);

    /**
     * 获取已经Merged的分支列表
     *
     * @param repository gitRepository
     * @return
     */
    GitCommandResult getMergedBranchList(GitRepository repository, String data);

    /**
     * 查询最近一次提交详情
     *
     * @param repository – gitRepository
     * @return
     */
    GitCommandResult showLocalLastCommitFull(GitRepository repository, String commitId);

    /**
     * @param repository – gitRepository
     * @param key        key
     * @return
     */
    GitCommandResult getConfig(GitRepository repository, String key);

    /**
     * @param repository – gitRepository
     * @param commitId   commitId
     * @return
     */
    GitCommandResult showFile(GitRepository repository, String commitId);

    /**
     * 查询提交Message信息
     *
     * @param repository – gitRepository
     * @param commitId   commitId
     * @return
     */
    GitCommandResult showCommitMessage(GitRepository repository, String commitId);
}
