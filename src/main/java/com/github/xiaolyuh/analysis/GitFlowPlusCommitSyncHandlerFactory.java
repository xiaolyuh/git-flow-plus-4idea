package com.github.xiaolyuh.analysis;

import com.github.xiaolyuh.action.options.LocalConfig;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.ui.NonFocusableCheckBox;
import java.awt.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 插件Commit同步
 *
 * @author wyh
 */
public class GitFlowPlusCommitSyncHandlerFactory extends CheckinHandlerFactory {
    @Override
    public @NotNull CheckinHandler createHandler(@NotNull CheckinProjectPanel panel, @NotNull CommitContext commitContext) {
        Project myProject = panel.getProject();
        return new CheckinHandler() {
            @Override
            public @Nullable RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
                NonFocusableCheckBox syncCheckBox = new NonFocusableCheckBox("GitFlowPlus Commit Sync");
                syncCheckBox.addActionListener(e -> {
                    final LocalConfig localConfig = LocalConfig.getInstance();
                    localConfig.commitSync = syncCheckBox.isSelected();
                });
                return new RefreshableOnComponent() {
                    private final LocalConfig localConfig = LocalConfig.getInstance();

                    @Deprecated
                    @Override
                    public void refresh() {
                    }

                    @Override
                    public void saveState() {
                        localConfig.commitSync = syncCheckBox.isSelected();
                    }

                    @Override
                    public void restoreState() {
                        syncCheckBox.setSelected(localConfig.commitSync);
                    }

                    @Override
                    public JComponent getComponent() {
                        JPanel panel = new JPanel(new BorderLayout());
                        panel.add(syncCheckBox);
                        boolean dumb = DumbService.isDumb(myProject);
                        syncCheckBox.setSelected(localConfig.commitSync);
                        syncCheckBox.setEnabled(!dumb);
                        syncCheckBox.setToolTipText(dumb ? "GitFlowPlus Commit Sync" : "");
                        return panel;
                    }
                };
            }

        };
    }
}
