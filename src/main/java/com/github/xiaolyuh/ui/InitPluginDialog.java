package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.action.options.InitOptions;
import com.github.xiaolyuh.action.options.LocalConfig;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.i18n.LanguageEnum;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.GitBranchUtil;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.CollectionComboBoxModel;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * 初始化插件弹框
 *
 * @author yuhao.wang3
 */
public class InitPluginDialog extends DialogWrapper {
    private JPanel contentPane;

    private JComboBox<String> masterBranchComboBox;
    private JComboBox<String> releaseBranchComboBox;
    private JComboBox<String> testBranchComboBox;
    private JTextField featurePrefixTextField;
    private JTextField hotfixPrefixTextField;
    private JTextField tagPrefixTextField;
    private JTextField dingtalkTokenTextField;
    private JComboBox<String> languageComboBox;
    private JLabel specialBranchConfigLabel;
    private JLabel mastBranchLabel;
    private JLabel releaseBranchLabel;
    private JLabel testBranchLabel;
    private JLabel branchOptionsConfig;
    private JLabel branchPrefixConfigLabel;
    private JLabel featureBranchPrefixLabel;
    private JLabel hotfixBranchPrefixLabel;
    private JLabel tagNamePrefixLabel;
    private JTextField helloTokenTextField;
    private JTextField recipientTextField;
    private JLabel patternTextLabel;
    private JTextField patternTextField;
    private JLabel patternExplainLabel;
    private JTextArea patternExplainArea;

    public InitPluginDialog(Project project) {
        super(project);
        setTitle(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TITLE));

        initDialog(project);

        init();
        languageComboBox.addItemListener(e -> {
            I18n.loadLanguageProperties(LanguageEnum.getByLanguage((String) languageComboBox.getSelectedItem()));
            languageSwitch();
        });
    }

    private void languageSwitch() {
        setTitle(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TITLE));

        specialBranchConfigLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$SPECIAL_BRANCH_CONFIG_LABEL));
        mastBranchLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$MAST_BRANCH_LABEL));
        releaseBranchLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$RELEASE_BRANCH_LABEL));
        testBranchLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TEST_BRANCH_LABEL));
        branchOptionsConfig.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$BRANCH_OPTIONS_CONFIG));
        branchPrefixConfigLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$BRANCH_PREFIX_CONFIG_LABEL));
        featureBranchPrefixLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$FEATURE_BRANCH_PREFIX_LABEL));
        hotfixBranchPrefixLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$HOTFIX_BRANCH_PREFIX_LABEL));
        tagNamePrefixLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TAG_NAME_PREFIX_LABEL));
        patternTextLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$PATTERN_TEXT_FIELD));
        patternExplainLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$PATTERN_EXPLAIN_TEXT_LABEL));
    }


    public InitOptions getOptions() {
        InitOptions options = new InitOptions();

        options.setMasterBranch((String) masterBranchComboBox.getSelectedItem());
        options.setReleaseBranch((String) releaseBranchComboBox.getSelectedItem());
        options.setTestBranch((String) testBranchComboBox.getSelectedItem());
        options.setFeaturePrefix(featurePrefixTextField.getText());
        options.setHotfixPrefix(hotfixPrefixTextField.getText());
        options.setTagPrefix(tagPrefixTextField.getText());
        options.setDingtalkToken(dingtalkTokenTextField.getText());
        options.setHelloToken(helloTokenTextField.getText());
        options.setPattern(patternTextField.getText());
        options.setPatternExplain(patternExplainArea.getText());

        LocalConfig localConfig = LocalConfig.getInstance();
        localConfig.language = LanguageEnum.getByLanguage((String) languageComboBox.getSelectedItem());
        localConfig.recipient = recipientTextField.getText();


        return options;
    }

    /**
     * 初始化弹框
     */
    private void initDialog(Project project) {
        Optional<InitOptions> options = ConfigUtil.getConfig(project);
        LocalConfig localConfig = LocalConfig.getInstance();
        List<String> remoteBranches = GitBranchUtil.getRemoteBranches(project);
        List<String> languages = LanguageEnum.getAllLanguage();

        if (options.isPresent()) {
            List<String> masterBranchList = Lists.newArrayList(options.get().getMasterBranch());
            List<String> releaseBranchList = Lists.newArrayList(options.get().getReleaseBranch());
            List<String> testBranchList = Lists.newArrayList(options.get().getTestBranch());
            masterBranchList.addAll(remoteBranches);
            releaseBranchList.addAll(remoteBranches);
            testBranchList.addAll(remoteBranches);
            masterBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches, options.get().getMasterBranch()));
            releaseBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches, options.get().getReleaseBranch()));
            testBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches, options.get().getTestBranch()));

            featurePrefixTextField.setText(options.get().getFeaturePrefix());
            hotfixPrefixTextField.setText(options.get().getHotfixPrefix());
            tagPrefixTextField.setText(options.get().getTagPrefix());
            dingtalkTokenTextField.setText(options.get().getDingtalkToken());
            helloTokenTextField.setText(options.get().getHelloToken());
            patternTextField.setText(options.get().getPattern());
            patternExplainArea.setText(options.get().getPatternExplain());

            languageComboBox.setModel(new CollectionComboBoxModel<>(languages, localConfig.language.getLanguage()));
            recipientTextField.setText(localConfig.recipient);

            languageSwitch();

            return;
        }

        InitOptions initOptions = new InitOptions();
        masterBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches));
        releaseBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches));
        testBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches));
        languageComboBox.setModel(new CollectionComboBoxModel<>(languages));
        patternTextField.setText(initOptions.getPattern());
        patternExplainArea.setText(initOptions.getPatternExplain());

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
    protected ValidationInfo doValidate() {
        if (Objects.equals(masterBranchComboBox.getSelectedItem(), releaseBranchComboBox.getSelectedItem())) {

            return new ValidationInfo(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$RELEASE_LIKE_MASTER), releaseBranchComboBox);
        }
        if (Objects.equals(masterBranchComboBox.getSelectedItem(), testBranchComboBox.getSelectedItem())) {
            return new ValidationInfo(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TEST_LIKE_MASTER), testBranchComboBox);
        }
        if (releaseBranchComboBox.getSelectedItem().equals(testBranchComboBox.getSelectedItem())) {
            return new ValidationInfo(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TEST_LIKE_RELEASE), testBranchComboBox);
        }
        if (StringUtil.isEmptyOrSpaces(featurePrefixTextField.getText())) {
            return new ValidationInfo(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$FEATURE_PREFIX_REQUIRED), featurePrefixTextField);
        }
        if (StringUtil.isEmptyOrSpaces(hotfixPrefixTextField.getText())) {
            return new ValidationInfo(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$HOTFIX_PREFIX_REQUIRED), hotfixPrefixTextField);
        }

        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
