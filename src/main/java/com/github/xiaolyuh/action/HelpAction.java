package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

/**
 * 帮助
 *
 * @author yuhao.wang3
 */
public class HelpAction extends AnAction {
    public HelpAction() {
        super("帮助", "帮助", IconLoader.getIcon("/icons/help.svg", AbstractNewBranchAction.class));
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        event.getPresentation().setText(I18n.getContent(I18nKey.HELP_ACTION$TEXT));
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        BrowserUtil.browse("https://github.com/xiaolyuh/mrtf-git-flow-4idea");
    }
}
