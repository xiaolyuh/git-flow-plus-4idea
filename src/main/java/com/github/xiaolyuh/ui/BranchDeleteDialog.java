package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.action.options.DeleteBranchOptions;
import com.github.xiaolyuh.action.options.InitOptions;
import com.github.xiaolyuh.action.vo.BranchVo;
import com.github.xiaolyuh.git.GitFlowPlus;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import git4idea.repo.GitRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.*;
import org.jetbrains.annotations.Nullable;

/**
 * @author yuhao.wang3
 * @since 2020/3/27 12:15
 */
public class BranchDeleteDialog extends DialogWrapper {
    private JPanel tagPanel;
    private JComboBox deleteModel;
    private JTextField deleteBeforeDate;
    private JCheckBox isDeleteLocalBranchBox;
    private JTable branchTable;
    private JButton searchButton;
    private JTextField branchNameField;

    private DeleteBranchOptions deleteBranchOptions = new DeleteBranchOptions();

    private Project project;

    GitFlowPlus gitFlowPlus = GitFlowPlus.getInstance();

    public BranchDeleteDialog(@Nullable GitRepository repository) {
        super(Objects.requireNonNull(repository).getProject());
        this.project = repository.getProject();
        setTitle("Branch Delete");
        init();

        refreshDeleteBranchList(repository);
        searchButton.addActionListener(actionEvent -> refreshDeleteBranchList(repository));
    }

    public void refreshDeleteBranchList(GitRepository repository) {
        InitOptions initOptions = ConfigUtil.getConfig(repository.getProject()).get();
        // 日期过滤
        List<BranchVo> allBranches = getBranchListFilterDate(repository);

        if (StringUtils.isNotBlank(branchNameField.getText())) {
            // 分支名称过滤
            allBranches = allBranches.stream().filter(branchVo -> branchVo.getBranch().contains(branchNameField.getText())).collect(Collectors.toList());
        }

        String selectedItem = (String) deleteModel.getSelectedItem();
        switch (Objects.requireNonNull(selectedItem)) {

            case "删除已上线分支":
                List<String> mergedBranches = gitFlowPlus.getMergedBranchList(repository);
                allBranches = allBranches.stream().filter(branchVo -> mergedBranches.contains(branchVo.getBranch())).collect(Collectors.toList());
                renderingBranchTable(allBranches);
                break;
            case "删除全部开发分支":
                allBranches = allBranches.stream()
                        .filter(branchVo -> branchVo.getBranch().contains(initOptions.getFeaturePrefix()) || branchVo.getBranch().contains(initOptions.getHotfixPrefix()))
                        .collect(Collectors.toList());
                renderingBranchTable(allBranches);
                break;
            default:
                // 删除全部分支
                renderingBranchTable(allBranches);
        }
    }

    private List<BranchVo> getBranchListFilterDate(GitRepository repository) {
        String dataNum = deleteBeforeDate.getText();
        boolean isMatch = Pattern.matches("^[1-9]\\d*$", dataNum);
        if (!isMatch) {
            dataNum = "3";
        }
        // 执行查询操作
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String data = LocalDateTime.now().minusDays(Integer.parseInt(dataNum)).format(df);
        List<BranchVo> allBranches = gitFlowPlus.getBranchList(repository);
        return allBranches.stream().filter(branchVo -> data.compareTo(branchVo.getLastCommitDate()) > 0).collect(Collectors.toList());
    }

    private void renderingBranchTable(List<BranchVo> branches) {
        this.deleteBranchOptions.setBranches(branches);
        for (int i = 0; i < branches.size(); i++) {
            branches.get(i).setId((branches.size() - i) + "");
        }

        final Object[] columnNames = {"序号", "最后一次提交时间", "分支名称", "创建人"};
        Object[][] rowData = new Object[branches.size()][4];
        for (int i = 0; i < branches.size(); i++) {
            BranchVo branchVo = branches.get(i);
            rowData[i][0] = branchVo.getId();
            rowData[i][1] = branchVo.getLastCommitDate();
            rowData[i][2] = branchVo.getBranch();
            rowData[i][3] = branchVo.getCreateUser();
        }

        TableModel dataModel = new DefaultTableModel(rowData, columnNames);
        branchTable.setModel(dataModel);
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


    public DeleteBranchOptions getDeleteBranchOptions() {
        deleteBranchOptions.setDeleteLocalBranch(isDeleteLocalBranchBox.isSelected());
        return deleteBranchOptions;
    }
}
