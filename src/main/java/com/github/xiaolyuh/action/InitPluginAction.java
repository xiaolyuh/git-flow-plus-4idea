package com.github.xiaolyuh.action;

import com.alibaba.fastjson.JSON;
import com.github.xiaolyuh.action.options.InitOptions;
import com.github.xiaolyuh.git.GitFlowPlus;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.notify.NotifyUtil;
import com.github.xiaolyuh.ui.InitPluginDialog;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.GitBranchUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * 初始化Action
 *
 * @author yuhao.wang3
 */
public class InitPluginAction extends AnAction {

    private GitFlowPlus gitFlowPlus = GitFlowPlus.getInstance();

    public InitPluginAction() {
        super("初始化配置", "初始化仓库配置，如果测试分支与发布分支不存在，将基于master新建", IconLoader.getIcon("/icons/config.svg", AbstractNewBranchAction.class));
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        event.getPresentation().setEnabledAndVisible(GitBranchUtil.isGitProject(event.getProject()));
        event.getPresentation().setText(ConfigUtil.isInit(event.getProject())
                ? I18n.getContent(I18nKey.INIT_PLUGIN_ACTION$TEXT_UPDATE) : I18n.getContent(I18nKey.INIT_PLUGIN_ACTION$TEXT_INIT));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = event.getProject();
        final GitRepository repository = GitBranchUtil.getCurrentRepository(project);
        if (Objects.isNull(repository)) {
            return;
        }

        InitPluginDialog initPluginDialog = new InitPluginDialog(project);
        initPluginDialog.show();

        if (initPluginDialog.isOK()) {
            final InitOptions initOptions = initPluginDialog.getOptions();

            new Task.Backgroundable(project, "Init GitFlowPlus Plugins", false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    NotifyUtil.notifyGitCommand(event.getProject(), "===================================================================================");

                    // 校验主干分支是否存在
                    List<String> remoteBranches = GitBranchUtil.getRemoteBranches(project);
                    if (!remoteBranches.contains(initOptions.getMasterBranch())) {
                        NotifyUtil.notifyError(myProject, "Error", String.format(I18n.getContent(I18nKey.INIT_PLUGIN_ACTION$NOT_EXIST_MASTER_INFO), initOptions.getMasterBranch()));
                        return;
                    }

                    // 校验主测试支是否存在，不存在就新建
                    if (!remoteBranches.contains(initOptions.getTestBranch())) {
                        GitCommandResult result = gitFlowPlus.newNewBranchBaseRemoteMaster(repository, initOptions.getMasterBranch(), initOptions.getTestBranch());
                        if (result.success()) {
                            NotifyUtil.notifySuccess(myProject, "Success", String.format(I18n.getContent(I18nKey.NEW_BRANCH_SUCCESS), initOptions.getMasterBranch(), initOptions.getTestBranch()));
                        } else {
                            NotifyUtil.notifyError(myProject, "Error", String.format(I18n.getContent(I18nKey.INIT_PLUGIN_ACTION$NOT_EXIST_MASTER_INFO), initOptions.getTestBranch()));
                            return;
                        }
                    }

                    // 校验主发布支是否存在，不存在就新建
                    if (!remoteBranches.contains(initOptions.getReleaseBranch())) {
                        // 新建分支发布分支
                        GitCommandResult result = gitFlowPlus.newNewBranchBaseRemoteMaster(repository, initOptions.getMasterBranch(), initOptions.getReleaseBranch());
                        if (result.success()) {
                            NotifyUtil.notifySuccess(myProject, "Success", String.format(I18n.getContent(I18nKey.NEW_BRANCH_SUCCESS), initOptions.getMasterBranch(), initOptions.getReleaseBranch()));
                        } else {
                            NotifyUtil.notifyError(myProject, "Error", String.format(I18n.getContent(I18nKey.INIT_PLUGIN_ACTION$NOT_EXIST_MASTER_INFO), initOptions.getReleaseBranch()));
                            return;
                        }
                    }

                    // 存储配置
                    String configJson = JSON.toJSONString(initOptions);
                    ConfigUtil.saveConfigToLocal(project, configJson);
                    ConfigUtil.saveConfigToFile(project, configJson);

                    // 将配置文件加入GIT管理
                    gitFlowPlus.addConfigToGit(repository);

                    NotifyUtil.notifySuccess(myProject, "Success", I18n.getContent(I18nKey.INIT_PLUGIN_ACTION$INIT_SUCCESS));

                    //update the widget
                    myProject.getMessageBus().syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repository);
                    repository.update();
                    VirtualFileManager.getInstance().asyncRefresh(null);
                }
            }.queue();
        }
    }

}
