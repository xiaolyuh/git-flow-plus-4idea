package com.github.xiaolyuh.valve.merge;

import com.github.xiaolyuh.Constants;
import com.github.xiaolyuh.action.options.TagOptions;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.notify.NotifyUtil;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;

/**
 * 加锁阀门
 *
 * @author yuhao.wang3
 * @since 2020/4/7 16:42
 */
public class LockValve extends Valve {
    private static LockValve lockValve = new LockValve();

    public static Valve getInstance() {
        return lockValve;
    }

    @Override
    public boolean invoke(Project project, GitRepository repository, String sourceBranch, String targetBranch, TagOptions tagOptions) {
        if (!gitFlowPlus.lock(repository, sourceBranch)) {
            String msg = gitFlowPlus.getRemoteLastCommit(repository, Constants.LOCK_BRANCH_NAME);
            NotifyUtil.notifyError(project, "Error", String.format(I18n.getContent(I18nKey.LOCK_VALVE$LOCKED), msg));
            return false;
        }
        return true;
    }
}
