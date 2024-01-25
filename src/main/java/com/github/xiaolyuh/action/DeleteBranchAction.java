package com.github.xiaolyuh.action;

import com.github.xiaolyuh.action.options.DeleteBranchOptions;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.notify.NotifyUtil;
import com.github.xiaolyuh.ui.BranchDeleteDialog;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.GitBranchUtil;
import com.github.xiaolyuh.valve.merge.ChangeFileValve;
import com.github.xiaolyuh.valve.merge.Valve;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.repo.GitRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * 删除分支
 *
 * @author yuhao.wang3
 */
public class DeleteBranchAction extends AbstractMergeAction {

    public DeleteBranchAction() {
        super("删除分支", "批量删除无效分支", IconLoader.getIcon("/icons/delete.svg", AbstractNewBranchAction.class));
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setText(I18n.getContent(I18nKey.DELETE_BRANCH_ACTION$TEXT));
//        if (event.getPresentation().isEnabled()) {
//            event.getPresentation().setEnabled(gitFlowPlus.isLock(event.getProject()));
//        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        assert project != null;
        final String currentBranch = gitFlowPlus.getCurrentBranch(project);
        final GitRepository repository = GitBranchUtil.getCurrentRepository(project);
        if (Objects.isNull(repository)) {
            return;
        }

        // 校验是否有文件没有提交
        if (gitFlowPlus.isExistChangeFile(project)) {
            return;
        }

        BranchDeleteDialog branchDeleteDialog = new BranchDeleteDialog(repository);
        branchDeleteDialog.show();
        if (!branchDeleteDialog.isOK()) {
            return;
        }

        int flag = Messages.showOkCancelDialog(project, "是否确认删除当前分支",
                "删除分支", I18n.getContent(I18nKey.OK_TEXT), I18n.getContent(I18nKey.CANCEL_TEXT),
                IconLoader.getIcon("/icons/warning.svg", AbstractNewBranchAction.class));
        if (flag != 0) {
            return;
        }

        new Task.Backgroundable(project, "是否确实删除当前分支", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                NotifyUtil.notifyGitCommand(event.getProject(), "===================================================================================");
                DeleteBranchOptions deleteBranchOptions = branchDeleteDialog.getDeleteBranchOptions();

                deleteBranchOptions.getBranches().forEach(branchVo -> {
                    gitFlowPlus.deleteBranch(repository, branchVo.getBranch(), deleteBranchOptions.isDeleteLocalBranch());
                });

                // 刷新
                repository.update();
                assert myProject != null;
                myProject.getMessageBus().syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repository);
                VirtualFileManager.getInstance().asyncRefresh(null);
            }
        }.queue();
    }


    @Override
    protected String getTargetBranch(Project project) {
        return ConfigUtil.getConfig(project).get().getMasterBranch();
    }

    @Override
    protected String getDialogTitle(Project project) {
        return I18n.getContent(I18nKey.FAILURE_RELEASE_ACTION$DIALOG_TITLE);
    }

    @Override
    protected String getDialogContent(Project project) {
        String release = ConfigUtil.getConfig(project).get().getReleaseBranch();
        return String.format(I18n.getContent(I18nKey.FAILURE_RELEASE_ACTION$DIALOG_CONTENT), release);
    }

    @Override
    protected String getTaskTitle(Project project) {
        String release = ConfigUtil.getConfig(project).get().getReleaseBranch();
        return String.format(I18n.getContent(I18nKey.FAILURE_RELEASE_ACTION$TASK_TITLE), release);
    }

    @Override
    protected List<Valve> getValves() {
        List<Valve> valves = new ArrayList<>();
        valves.add(ChangeFileValve.getInstance());
//        valves.add(UnLockCheckValve.getInstance());
//        valves.add(UnLockValve.getInstance());
        return valves;
    }
}
