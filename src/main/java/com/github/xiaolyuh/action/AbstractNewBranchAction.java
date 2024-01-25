package com.github.xiaolyuh.action;

import com.github.xiaolyuh.git.GitFlowPlus;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.notify.NotifyUtil;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.GitBranchUtil;
import com.github.xiaolyuh.utils.StringUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import java.util.Objects;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 新建分支
 *
 * @author yuhao.wang3
 */
public abstract class AbstractNewBranchAction extends AnAction {
    GitFlowPlus gitFlowPlus = GitFlowPlus.getInstance();

    public AbstractNewBranchAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        Project project = event.getProject();
        event.getPresentation().setEnabled(GitBranchUtil.isGitProject(project) && ConfigUtil.isInit(project));
        setEnabledAndText(event);
    }

    /**
     * 设置是否启用和Text
     *
     * @param event
     */
    protected abstract void setEnabledAndText(AnActionEvent event);

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        String featurePrefix = getPrefix(project);
        // 获取输入框内容
        String inputString = getInputString(project);
        if (StringUtils.isBlank(inputString)) {
            return;
        }

        // 获取开发分支完整名称
        final String newBranchName = featurePrefix + inputString;
        final GitRepository repository = GitBranchUtil.getCurrentRepository(project);
        if (Objects.isNull(repository)) {
            return;
        }

        new Task.Backgroundable(project, getTitle(newBranchName), false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                final String master = ConfigUtil.getConfig(project).get().getMasterBranch();

                if (gitFlowPlus.isExistChangeFile(project)) {
                    return;
                }

                NotifyUtil.notifyGitCommand(event.getProject(), "===================================================================================");
                if (isDeleteBranch()) {
                    // 删除分支
                    GitCommandResult result = gitFlowPlus.deleteBranch(repository, master, newBranchName);
                    if (result.success()) {
                        NotifyUtil.notifySuccess(myProject, "Success", String.format(I18n.getContent(I18nKey.DELETE_BRANCH_SUCCESS), newBranchName));
                    } else {
                        NotifyUtil.notifyError(myProject, "Error", I18n.getContent(I18nKey.DELETE_BRANCH_ERROR) + "：" + result.getErrorOutputAsJoinedString());
                    }
                }

                // 新建分支
                GitCommandResult result = gitFlowPlus.newNewBranchBaseRemoteMaster(repository, master, newBranchName);
                if (result.success()) {
                    NotifyUtil.notifySuccess(myProject, "Success", String.format(I18n.getContent(I18nKey.NEW_BRANCH_SUCCESS), master, newBranchName));
                } else {
                    NotifyUtil.notifyError(myProject, "Error", result.getErrorOutputAsJoinedString());
                }

                // 刷新
                repository.update();
                VirtualFileManager.getInstance().asyncRefresh(null);
            }
        }.queue();

    }

    /**
     * 获取分支前缀
     *
     * @param project Project
     * @return String
     */
    abstract public String getPrefix(Project project);

    /**
     * 获取输入的分支名称
     *
     * @param project Project
     * @return String
     */
    abstract public String getInputString(Project project);

    /**
     * 获取标题
     *
     * @param branchName branchName
     * @return String
     */
    abstract public String getTitle(String branchName);

    /**
     * 是否先删除分支
     *
     * @return boolean
     */
    public boolean isDeleteBranch() {
        return true;
    }

}
