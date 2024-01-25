package com.github.xiaolyuh.valve.merge;

import com.github.xiaolyuh.action.options.TagOptions;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.notify.NotifyUtil;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;

import java.util.Objects;

/**
 * 分支合并阀门
 *
 * @author yuhao.wang3
 * @since 2020/4/7 16:42
 */
public class MergeValve extends Valve {
    private static MergeValve valve = new MergeValve();

    public static Valve getInstance() {
        return valve;
    }

    @Override
    public boolean invoke(Project project, GitRepository repository, String sourceBranch, String targetBranch, TagOptions tagOptions) {
        GitCommandResult result = gitFlowPlus.mergeBranchAndPush(repository, sourceBranch, targetBranch, tagOptions);
        if (result.success()) {
            String releaseBranch = ReadAction.compute(() -> ConfigUtil.getConfig(repository.getProject()).get().getReleaseBranch());
            String source = Objects.nonNull(tagOptions) ? releaseBranch : sourceBranch;
            NotifyUtil.notifySuccess(project, "Success", String.format(I18n.getContent(I18nKey.MERGE_VALVE$MERGE_SUCCESS), source, targetBranch));
            return true;
        }

        NotifyUtil.notifyError(project, "Error", result.getErrorOutputAsJoinedString());
        return false;
    }
}
