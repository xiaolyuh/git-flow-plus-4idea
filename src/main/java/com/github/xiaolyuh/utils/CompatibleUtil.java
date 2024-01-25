package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.notify.NotifyUtil;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.util.BuildNumber;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.impl.HashImpl;
import git4idea.commands.Git;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

/**
 * @author yuhao.wang3
 * @since 2020/3/30 17:46
 */
public class CompatibleUtil {
    private CompatibleUtil() {
    }

    private static final int VERSION_190 = 190;

    public static void updateAndRefreshVfs(GitRepository repository) {
        BuildNumber buildNumber = ApplicationInfo.getInstance().getBuild();
        int baselineVersion = buildNumber.getBaselineVersion();
        if (baselineVersion >= VERSION_190) {

        } else {

        }
    }

    static Hash getHead(@NotNull GitRepository repository) {
        GitCommandResult result = Git.getInstance().tip(repository, "HEAD");
        if (!result.success()) {
            NotifyUtil.notifyError(repository.getProject(), "Error", String.format("Couldn't identify the HEAD for %s : %s", repository.toString(), result.getErrorOutputAsJoinedString()));
            return null;
        }
        String head = result.getOutputAsJoinedString();
        return HashImpl.build(head);
    }
}
