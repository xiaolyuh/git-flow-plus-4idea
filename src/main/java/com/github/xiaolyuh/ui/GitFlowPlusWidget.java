package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.action.DeleteBranchAction;
import com.github.xiaolyuh.action.FinishReleaseAction;
import com.github.xiaolyuh.action.GitResolveConflictsAction;
import com.github.xiaolyuh.action.HelpAction;
import com.github.xiaolyuh.action.InitPluginAction;
import com.github.xiaolyuh.action.MergeRequestAction;
import com.github.xiaolyuh.action.NewFeatureAction;
import com.github.xiaolyuh.action.NewHotFixAction;
import com.github.xiaolyuh.action.RebuildActionGroup;
import com.github.xiaolyuh.action.RebuildReleaseAction;
import com.github.xiaolyuh.action.RebuildTestAction;
import com.github.xiaolyuh.action.StartReleaseAction;
import com.github.xiaolyuh.action.StartTestAction;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.openapi.wm.impl.status.TextPanel;
import com.intellij.ui.ClickListener;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.popup.PopupFactoryImpl;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

/**
 * mrtf git flow 状态栏小部件
 *
 * @author yuhao.wang3
 */
public class GitFlowPlusWidget extends EditorBasedWidget implements StatusBarWidget.Multiframe, CustomStatusBarWidget {
    private final TextPanel.WithIconAndArrows myComponent;
    DefaultActionGroup popupGroup;
    Project project;


    public GitFlowPlusWidget(@NotNull Project project) {
        super(project);
        this.project = project;

        initPopupGroup(project);
        myComponent = new TextPanel.WithIconAndArrows() {
        };

        new ClickListener() {
            @Override
            public boolean onClick(@NotNull MouseEvent e, int clickCount) {
                update();
                showPopup(e);
                return true;
            }
        }.installOn(myComponent);
        myComponent.setBorder(WidgetBorder.WIDE);
    }

    private void showPopup(MouseEvent e) {
        ListPopup popup = new PopupFactoryImpl.ActionGroupPopup("GitFlowPlus", popupGroup, DataManager.getInstance().getDataContext(myComponent),
                false, false, true, true, null, -1, null, null);

        if (popup != null) {
            Dimension dimension = popup.getContent().getPreferredSize();
            Point at = new Point(0, -dimension.height);
            popup.show(new RelativePoint(e.getComponent(), at));
            Disposer.register(this, popup); // destroy popup on unexpected project close
        }
    }

    public void update() {
        myComponent.setVisible(true);
        myComponent.setToolTipText("GitFlowPlus");
        myComponent.setText("GitFlowPlus");
        myComponent.invalidate();
        if (myStatusBar != null) {
            myStatusBar.updateWidget(ID());
        }
    }

    private void initPopupGroup(Project project) {
        //No advanced features in the status-bar widget
        popupGroup = new DefaultActionGroup();
        popupGroup.add(new InitPluginAction());
        popupGroup.add(new Separator());

        popupGroup.add(new NewFeatureAction());
        popupGroup.add(new NewHotFixAction());
        popupGroup.add(new Separator());

        DefaultActionGroup rebuildPopupGroup = new RebuildActionGroup("重建分支", true);
        rebuildPopupGroup.add(new RebuildTestAction());
        rebuildPopupGroup.add(new RebuildReleaseAction());
        popupGroup.add(rebuildPopupGroup);
        popupGroup.add(new Separator());

        GitResolveConflictsAction conflictsAction = new GitResolveConflictsAction();
        conflictsAction.getTemplatePresentation().setText(I18n.getContent(I18nKey.GIT_RESOLVE_CONFLICTS_ACTION$TEXT));
        popupGroup.add(conflictsAction);
        popupGroup.add(new Separator());

        MergeRequestAction mergeRequestAction = new MergeRequestAction();
        popupGroup.add(mergeRequestAction);
        popupGroup.add(new Separator());

        StartTestAction action = new StartTestAction();
        action.registerCustomShortcutSet(new CustomShortcutSet(KeyStroke.getKeyStroke("ctrl shift T")), myComponent);
        popupGroup.add(action);
        popupGroup.add(new Separator());

        popupGroup.add(new StartReleaseAction());
        popupGroup.add(new FinishReleaseAction());
        popupGroup.add(new Separator());

        popupGroup.add(new DeleteBranchAction());
        popupGroup.add(new Separator());

        popupGroup.add(new HelpAction());
    }


    @Override
    public JComponent getComponent() {
        return myComponent;
    }

    @Override
    public StatusBarWidget copy() {
        return new GitFlowPlusWidget(project);
    }

    @NotNull
    @Override
    public String ID() {
        return GitFlowPlusWidget.class.getName();
    }
}
