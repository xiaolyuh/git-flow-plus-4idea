package com.github.xiaolyuh.action;

import com.github.xiaolyuh.validator.GitNewBranchNameValidator;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import git4idea.GitUtil;

/**
 * 新建修复分支
 *
 * @author yuhao.wang3
 */
public class NewHotFixAction extends AbstractNewBranchAction {

    public NewHotFixAction() {
        super("新建修复分支","新建开发分支，并推送到远程仓库", null);
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setText(I18n.getContent(I18nKey.NEW_HOT_FIX_ACTION$TEXT));
    }

    @Override
    public String getPrefix(Project project) {
        return ConfigUtil.getConfig(project).get().getHotfixPrefix();
    }

    @Override
    public String getInputString(Project project) {
        return Messages.showInputDialog(project, I18n.getContent(I18nKey.NEW_HOT_FIX_ACTION$DIALOG_MESSAGE),
                I18n.getContent(I18nKey.NEW_HOT_FIX_ACTION$DIALOG_TITLE), null, "",
                GitNewBranchNameValidator.newInstance(GitUtil.getRepositoryManager(project).getRepositories(), getPrefix(project)));
    }

    @Override
    public String getTitle(String branchName) {
        return I18n.getContent(I18nKey.NEW_HOT_FIX_ACTION$TITLE) + ": " + branchName;
    }


    @Override
    public boolean isDeleteBranch() {
        return false;
    }
}
