package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.valve.merge.ChangeFileValve;
import com.github.xiaolyuh.valve.merge.Valve;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布失败
 *
 * @author yuhao.wang3
 */
public class FailureReleaseAction extends AbstractMergeAction {

    public FailureReleaseAction() {
        super("发布失败", "解锁，这时新的开发分支可以合并到发布分支了", IconLoader.getIcon("/icons/failure.svg", AbstractNewBranchAction.class));
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setText(I18n.getContent(I18nKey.FAILURE_RELEASE_ACTION$TEXT));
//        if (event.getPresentation().isEnabled()) {
//            event.getPresentation().setEnabled(gitFlowPlus.isLock(event.getProject()));
//        }
    }

    @Override
    protected String getTargetBranch(Project project) {
        return ConfigUtil.getConfig(project).get().getMasterBranch();
    }

    @Override
    protected String getDialogTitle(Project project) {
        return I18n.getContent(I18nKey.FAILURE_RELEASE_ACTION$DIALOG_TITLE);
    }

    @Override
    protected String getDialogContent(Project project) {
        String release = ConfigUtil.getConfig(project).get().getReleaseBranch();
        return String.format(I18n.getContent(I18nKey.FAILURE_RELEASE_ACTION$DIALOG_CONTENT), release);
    }

    @Override
    protected String getTaskTitle(Project project) {
        String release = ConfigUtil.getConfig(project).get().getReleaseBranch();
        return String.format(I18n.getContent(I18nKey.FAILURE_RELEASE_ACTION$TASK_TITLE), release);
    }

    @Override
    protected List<Valve> getValves() {
        List<Valve> valves = new ArrayList<>();
        valves.add(ChangeFileValve.getInstance());
//        valves.add(UnLockCheckValve.getInstance());
//        valves.add(UnLockValve.getInstance());
        return valves;
    }
}
