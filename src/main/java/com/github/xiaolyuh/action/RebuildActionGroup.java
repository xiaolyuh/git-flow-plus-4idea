package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

/**
 * @author yuhao.wang3
 * @since 2020/4/8 18:27
 */
public class RebuildActionGroup extends DefaultActionGroup {
    public RebuildActionGroup() {
        super("重建分支", true);
    }

    public RebuildActionGroup(String shortName, boolean popup) {
        super(shortName, popup);
    }

    @Override
    public void update(AnActionEvent event) {
        super.update(event);
        event.getPresentation().setText(I18n.getContent(I18nKey.REBUILD_ACTION_GROUP$TEXT));
    }
}
