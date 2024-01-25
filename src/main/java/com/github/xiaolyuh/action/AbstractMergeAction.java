package com.github.xiaolyuh.action;

import com.github.xiaolyuh.action.options.TagOptions;
import com.github.xiaolyuh.git.GitFlowPlus;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.notify.NotifyUtil;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.GitBranchUtil;
import com.github.xiaolyuh.utils.StringUtils;
import com.github.xiaolyuh.valve.merge.Valve;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.repo.GitRepository;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Merge 抽象Action
 *
 * @author yuhao.wang3
 */
public abstract class AbstractMergeAction extends AnAction {
    protected GitFlowPlus gitFlowPlus = GitFlowPlus.getInstance();

    public AbstractMergeAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        if (Objects.isNull(event.getProject())) {
            event.getPresentation().setEnabled(false);
            return;
        }
        boolean isInit = GitBranchUtil.isGitProject(event.getProject()) && ConfigUtil.isInit(event.getProject());
        if (!isInit) {
            event.getPresentation().setEnabled(false);
            return;
        }

        String currentBranch = gitFlowPlus.getCurrentBranch(event.getProject());
        String featurePrefix = ConfigUtil.getConfig(event.getProject()).get().getFeaturePrefix();
        String hotfixPrefix = ConfigUtil.getConfig(event.getProject()).get().getHotfixPrefix();
        // 已经初始化并且前缀是开发分支才显示
        boolean isDevBranch = StringUtils.startsWith(currentBranch, featurePrefix) || StringUtils.startsWith(currentBranch, hotfixPrefix);
        event.getPresentation().setEnabled(isDevBranch && !isConflicts(event.getProject()));

        setEnabledAndText(event);
    }

    /**
     * 设置是否启用和Text
     *
     * @param event
     */
    protected abstract void setEnabledAndText(AnActionEvent event);

    /**
     * 代码是否存在冲突
     *
     * @param project project
     * @return 是=true
     */
    boolean isConflicts(@NotNull Project project) {
        Collection<Change> changes = ChangeListManager.getInstance(project).getAllChanges();
        if (changes.size() > 1000) {
            return true;
        }
        return changes.stream().anyMatch(it -> it.getFileStatus() == FileStatus.MERGED_WITH_CONFLICTS);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        actionPerformed(event, null);
    }

    void actionPerformed(@NotNull AnActionEvent event, TagOptions tagOptions) {
        final Project project = event.getProject();
        final String currentBranch = gitFlowPlus.getCurrentBranch(project);
        final String targetBranch = getTargetBranch(project);

        final GitRepository repository = GitBranchUtil.getCurrentRepository(project);
        if (Objects.isNull(repository)) {
            return;
        }

        int flag = Messages.showOkCancelDialog(project, getDialogContent(project),
                getDialogTitle(project), I18n.getContent(I18nKey.OK_TEXT), I18n.getContent(I18nKey.CANCEL_TEXT),
                IconLoader.getIcon("/icons/warning.svg", AbstractNewBranchAction.class));
        if (flag == 0) {
            new Task.Backgroundable(project, getTaskTitle(project), false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    NotifyUtil.notifyGitCommand(event.getProject(), "===================================================================================");
                    List<Valve> valves = getValves();
                    for (Valve valve : valves) {
                        if (!valve.invoke(project, repository, currentBranch, targetBranch, tagOptions)) {
                            return;
                        }
                    }

                    // 刷新
                    repository.update();
                    myProject.getMessageBus().syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repository);
                    VirtualFileManager.getInstance().asyncRefresh(null);
                }
            }.queue();
        }
    }

    /**
     * 获取目标分支
     *
     * @param project project
     * @return String
     */
    protected abstract String getTargetBranch(Project project);

    /**
     * 获取标题
     *
     * @param project project
     * @return String
     */
    protected abstract String getDialogTitle(Project project);

    /**
     * 获取弹框内容
     *
     * @param project project
     * @return String
     */
    protected String getDialogContent(Project project) {
        return String.format(I18n.getContent(I18nKey.MERGE_BRANCH_MSG), gitFlowPlus.getCurrentBranch(project), getTargetBranch(project));
    }

    /**
     * 获取Task标题
     *
     * @param project project
     * @return String
     */
    protected abstract String getTaskTitle(Project project);

    /**
     * 获取需要执行的阀门
     *
     * @return boolean
     */
    protected abstract List<Valve> getValves();
}
