package com.github.xiaolyuh.valve.merge;

import com.github.xiaolyuh.action.options.TagOptions;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.notify.NotifyUtil;
import com.intellij.openapi.project.Project;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;

/**
 * 解锁阀门
 *
 * @author yuhao.wang3
 * @since 2020/4/7 16:42
 */
public class UnLockValve extends Valve {
    private static UnLockValve valve = new UnLockValve();

    public static Valve getInstance() {
        return valve;
    }

    @Override
    public boolean invoke(Project project, GitRepository repository, String sourceBranch, String targetBranch, TagOptions tagOptions) {
        GitCommandResult result = gitFlowPlus.unlock(repository);

        if (result.success()) {
            NotifyUtil.notifySuccess(repository.getProject(), "Success", I18n.getContent(I18nKey.UN_LOCK_VALVE$UN_LOCKED_SUCCESS));
            return true;
        }

        NotifyUtil.notifyError(repository.getProject(), "Error", String.format(I18n.getContent(I18nKey.UN_LOCK_VALVE$UN_LOCKED_ERROR) + ":%s", result.getErrorOutputAsJoinedString()));
        return false;
    }
}
