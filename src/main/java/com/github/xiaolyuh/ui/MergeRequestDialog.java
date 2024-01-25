package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.action.options.InitOptions;
import com.github.xiaolyuh.action.options.LocalConfig;
import com.github.xiaolyuh.action.options.MergeRequestOptions;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.StringUtils;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.CollectionComboBoxModel;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

/**
 * @author yuhao.wang3
 * @since 2020/3/27 12:15
 */
public class MergeRequestDialog extends DialogWrapper {
    private JPanel tagPanel;

    private JTextField titleTextField;
    private JTextArea messageTextArea;
    private JComboBox<String> targetBranchComboBox;
    private JCheckBox noticeCheckBox;
    private JTextField recipientTextField;

    public MergeRequestDialog(@Nullable Project project, String title, String message) {
        super(project);
        setTitle(I18n.getContent(I18nKey.MERGE_REQUEST_DIALOG$TITLE));
        init();

        assert project != null;
        Optional<InitOptions> options = ConfigUtil.getConfig(project);
        List<String> remoteBranches = Lists.newArrayList(options.get().getMasterBranch(), options.get().getReleaseBranch(), options.get().getTestBranch());
        targetBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches, options.get().getReleaseBranch()));

        titleTextField.setText(title);
        messageTextArea.setText(message);
        recipientTextField.setText(LocalConfig.getInstance().recipient);
    }

    public MergeRequestOptions getMergeRequestOptions() {
        MergeRequestOptions tagOptions = new MergeRequestOptions();
        tagOptions.setTargetBranch(StringUtils.trim(Objects.requireNonNull(targetBranchComboBox.getSelectedItem()).toString()));
        tagOptions.setTitle(StringUtils.trim(titleTextField.getText()));
        tagOptions.setMessage(StringUtils.trim(messageTextArea.getText()));
        tagOptions.setNotice(noticeCheckBox.isSelected());
        tagOptions.setRecipient(recipientTextField.getText());
        return tagOptions;
    }

    @javax.annotation.Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isBlank(titleTextField.getText())) {
            return new ValidationInfo(I18n.getContent(I18nKey.MERGE_REQUEST_DIALOG$TITLE_REQUIRED), titleTextField);
        }
        if (StringUtils.isBlank(messageTextArea.getText())) {
            return new ValidationInfo(I18n.getContent(I18nKey.MERGE_REQUEST_DIALOG$MESSAGE_REQUIRED), messageTextArea);
        }
        return null;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return tagPanel;
    }
}
