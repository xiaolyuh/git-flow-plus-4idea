package com.github.xiaolyuh.validator;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.intellij.openapi.ui.InputValidatorEx;
import git4idea.GitBranch;
import git4idea.branch.GitBranchUtil;
import git4idea.branch.GitBranchesCollection;
import git4idea.repo.GitRepository;
import git4idea.validators.GitRefNameValidator;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * 分支名称校验
 *
 * @author yuhao.wang3
 */
public final class GitNewBranchNameValidator implements InputValidatorEx {

    private final Collection<GitRepository> myRepositories;
    private String myErrorText;
    private String prefix;

    private GitNewBranchNameValidator(@NotNull Collection<GitRepository> repositories, String prefix) {
        this.myRepositories = repositories;
        this.prefix = prefix;
    }

    public static GitNewBranchNameValidator newInstance(@NotNull Collection<GitRepository> repositories, @NotNull String prefix) {
        return new GitNewBranchNameValidator(repositories, prefix);
    }

    @Override
    public boolean checkInput(@NotNull String inputString) {
        if (!GitRefNameValidator.getInstance().checkInput(inputString)) {
            myErrorText = I18n.getContent(I18nKey.BRANCH_VALIDATOR$INVALID_BRANCH);
            return false;
        }
        return checkBranchConflict(prefix + inputString);
    }

    private boolean checkBranchConflict(@NotNull String inputString) {
        if (isNotPermitted(inputString) || conflictsWithLocalBranch(inputString) || conflictsWithRemoteBranch(inputString)) {
            return false;
        }
        myErrorText = null;
        return true;
    }

    private boolean isNotPermitted(@NotNull String inputString) {
        if (inputString.equalsIgnoreCase("head")) {
            myErrorText = String.format(I18n.getContent(I18nKey.BRANCH_VALIDATOR$NOT_PERMITTED), inputString);
            return true;
        }
        return false;
    }

    private boolean conflictsWithLocalBranch(@NotNull String inputString) {
        return conflictsWithLocalOrRemote(inputString, true, " " + I18n.getContent(I18nKey.BRANCH_VALIDATOR$LOCAL_CONFLICTS));
    }

    private boolean conflictsWithRemoteBranch(@NotNull String inputString) {
        return conflictsWithLocalOrRemote(inputString, false, " " + I18n.getContent(I18nKey.BRANCH_VALIDATOR$REMOTE_CONFLICTS));
    }

    private boolean conflictsWithLocalOrRemote(@NotNull String inputString, boolean local, @NotNull String message) {
        int conflictsWithCurrentName = 0;
        for (GitRepository repository : myRepositories) {
            if (inputString.equals(repository.getCurrentBranchName())) {
                conflictsWithCurrentName++;
            } else {
                GitBranchesCollection branchesCollection = repository.getBranches();
                Collection<? extends GitBranch> branches = local ? branchesCollection.getLocalBranches() : branchesCollection.getRemoteBranches();
                for (GitBranch branch : branches) {
                    if (branch.getName().equals(inputString)) {
                        myErrorText = "Branch name " + inputString + message;
                        if (myRepositories.size() > 1 && !allReposHaveBranch(inputString, local)) {
                            myErrorText += " in repository " + repository.getPresentableUrl();
                        }
                        return true;
                    }
                }
            }
        }
        if (conflictsWithCurrentName == myRepositories.size()) {
            myErrorText = String.format(I18n.getContent(I18nKey.BRANCH_VALIDATOR$CURRENT_BRANCH), inputString);;
            return true;
        }
        return false;
    }

    private boolean allReposHaveBranch(String inputString, boolean local) {
        for (GitRepository repository : myRepositories) {
            GitBranchesCollection branchesCollection = repository.getBranches();
            Collection<? extends GitBranch> branches = local ? branchesCollection.getLocalBranches() : branchesCollection.getRemoteBranches();
            if (!GitBranchUtil.convertBranchesToNames(branches).contains(inputString)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canClose(String inputString) {
        return checkInput(prefix + inputString);
    }

    @Override
    public String getErrorText(String inputString) {
        return myErrorText;
    }
}
