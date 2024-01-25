package com.github.xiaolyuh.analysis;

import com.github.xiaolyuh.action.options.InitOptions;
import com.github.xiaolyuh.action.options.LocalConfig;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.ui.NonFocusableCheckBox;
import java.awt.*;
import java.util.regex.Pattern;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 插件Commit检查
 *
 * @author wyh
 */
public class GitFlowPlusCommitCheckinHandlerFactory extends CheckinHandlerFactory {
    @Override
    public @NotNull CheckinHandler createHandler(@NotNull CheckinProjectPanel panel, @NotNull CommitContext commitContext) {
        return new CheckinHandler() {
            private final Project myProject = panel.getProject();

            @Override
            public @Nullable RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
                NonFocusableCheckBox checkCheckBox = new NonFocusableCheckBox("GitFlowPlus Commit Check");
                checkCheckBox.addActionListener(e -> {
                    final LocalConfig localConfig = LocalConfig.getInstance();
                    localConfig.commitCheck = checkCheckBox.isSelected();
                });
                return new RefreshableOnComponent() {
                    private final LocalConfig localConfig = LocalConfig.getInstance();

                    @Deprecated
                    @Override
                    public void refresh() {
                    }

                    @Override
                    public void saveState() {
                        localConfig.commitCheck = checkCheckBox.isSelected();
                    }

                    @Override
                    public void restoreState() {
                        checkCheckBox.setSelected(localConfig.commitCheck);
                    }

                    @Override
                    public JComponent getComponent() {
                        JPanel panel = new JPanel(new BorderLayout());
                        panel.add(checkCheckBox);
                        boolean dumb = DumbService.isDumb(myProject);
                        checkCheckBox.setSelected(localConfig.commitCheck);
                        checkCheckBox.setEnabled(!dumb);
                        checkCheckBox.setToolTipText(dumb ? "GitFlowPlus Commit Check" : "");
                        return panel;
                    }
                };
            }

            @Override
            public ReturnResult beforeCheckin() {
                LocalConfig localConfig = LocalConfig.getInstance();
                if (localConfig.commitCheck) {
                    if (!ConfigUtil.getConfig(myProject).isPresent()) {
                        Messages.showMessageDialog(myProject, I18n.getContent(I18nKey.ESCALATION_COMMIT_ACTION$NOT_INIT),
                                "GitFlowPlus Commit Check Error", Messages.getWarningIcon());
                        return ReturnResult.CANCEL;
                    }
                    InitOptions initOptions = ConfigUtil.getConfig(myProject).get();
                    String commitMessage = panel.getCommitMessage();
                    String pattern = initOptions.getPattern();
                    String patternExplain = initOptions.getPatternExplain();
                    if (Pattern.matches(pattern, commitMessage)) {
                        return ReturnResult.COMMIT;
                    }

                    Messages.showOkCancelDialog(myProject, patternExplain,
                            I18n.getContent(I18nKey.COMMIT_MSG_DIALOG_TITLE), Messages.getOkButton(), Messages.getCancelButton(), Messages.getWarningIcon());
                    return ReturnResult.CANCEL;
                }
                return ReturnResult.COMMIT;
            }

            @Override
            public void checkinSuccessful() {

            }
        };
    }
}
