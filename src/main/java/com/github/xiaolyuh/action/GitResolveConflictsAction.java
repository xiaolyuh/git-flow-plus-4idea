package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 提测
 *
 * @author yuhao.wang3
 */
public class GitResolveConflictsAction extends git4idea.actions.GitResolveConflictsAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        event.getPresentation().setText(I18n.getContent(I18nKey.GIT_RESOLVE_CONFLICTS_ACTION$TEXT));
    }
}



