//package com.github.xiaolyuh.action;
//
//import com.github.xiaolyuh.action.options.LocalConfig;
//import com.github.xiaolyuh.i18n.I18n;
//import com.github.xiaolyuh.i18n.I18nKey;
//import com.github.xiaolyuh.notify.ThirdPartyService;
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.actionSystem.CommonDataKeys;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.ui.Messages;
//import com.intellij.openapi.util.IconLoader;
//import com.intellij.openapi.vcs.VcsDataKeys;
//import com.intellij.openapi.vcs.history.VcsRevisionNumber;
//import com.intellij.util.containers.ContainerUtil;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import org.jetbrains.annotations.NotNull;
//
///**
// * 上报Commit信息
// *
// * @author yuhao.wang3
// */
//public class EscalationCommitAction extends AnAction {
//    public EscalationCommitAction() {
//        super("上报Commit信息", "上报Commit信息", IconLoader.getIcon("/icons/server.svg", AbstractNewBranchAction.class));
//    }
//
//    @Override
//    public void update(@NotNull AnActionEvent event) {
//        super.update(event);
//        event.getPresentation().setText(I18n.getContent(I18nKey.ESCALATION_COMMIT_ACTION$TEXT));
//
//        final LocalConfig localConfig = LocalConfig.getInstance();
//        event.getPresentation().setEnabled(!getRevisionNumbersFromContext(event).isEmpty() && localConfig.commitSync);
//    }
//
//    @Override
//    public void actionPerformed(AnActionEvent event) {
//
//        int result = Messages.showOkCancelDialog(event.getProject(), I18n.getContent(I18nKey.ESCALATION_COMMIT_ACTION$SYNC_COMMIT_TEXT),
//                "Sync Commit Information To Server", Messages.getOkButton(), Messages.getCancelButton(), Messages.getInformationIcon());
//
//        if (result != Messages.OK) {
//            return;
//        }
//        Project project = event.getRequiredData(CommonDataKeys.PROJECT);
//        List<VcsRevisionNumber> revisions = getRevisionNumbersFromContext(event);
//        revisions = ContainerUtil.reverse(revisions);
//        revisions.forEach(vcsRevisionNumber -> {
//            ThirdPartyService.getInstance().escalationCommit(project, vcsRevisionNumber.asString());
//        });
//    }
//
//    private static List<VcsRevisionNumber> getRevisionNumbersFromContext(@NotNull AnActionEvent e) {
//        VcsRevisionNumber[] revisionNumbers = e.getData(VcsDataKeys.VCS_REVISION_NUMBERS);
//
//        return revisionNumbers != null ? Arrays.asList(revisionNumbers) : Collections.emptyList();
//    }
//}
