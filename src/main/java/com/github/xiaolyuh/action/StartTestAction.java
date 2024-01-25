package com.github.xiaolyuh.action;

import com.github.xiaolyuh.git.GitFlowPlus;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.valve.merge.ChangeFileValve;
import com.github.xiaolyuh.valve.merge.MergeValve;
import com.github.xiaolyuh.valve.merge.Valve;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 提测
 *
 * @author yuhao.wang3
 */
public class StartTestAction extends AbstractMergeAction {

    public StartTestAction() {
        super("提测", "将当前开发分支合并到test分支", IconLoader.getIcon("/icons/mergeToTest.svg", AbstractNewBranchAction.class));
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setText(I18n.getContent(I18nKey.START_TEST_ACTION$TEXT));
    }

    @Override
    protected String getTargetBranch(Project project) {
        return ConfigUtil.getConfig(project).get().getTestBranch();
    }

    @Override
    protected String getDialogTitle(Project project) {
        return I18n.getContent(I18nKey.START_TEST_ACTION$DIALOG_TITLE);
    }

    @Override
    protected String getTaskTitle(Project project) {
        return String.format(I18n.getContent(I18nKey.MERGE_BRANCH_TASK_TITLE), GitFlowPlus.getInstance().getCurrentBranch(project), getTargetBranch(project));
    }

    @Override
    protected List<Valve> getValves() {
        List<Valve> valves = new ArrayList<>();
        valves.add(ChangeFileValve.getInstance());
        valves.add(MergeValve.getInstance());
        return valves;
    }
}



