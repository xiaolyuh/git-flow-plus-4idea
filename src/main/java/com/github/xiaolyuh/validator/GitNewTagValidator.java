package com.github.xiaolyuh.validator;

import com.intellij.openapi.ui.InputValidatorEx;
import git4idea.validators.GitRefNameValidator;
import org.jetbrains.annotations.NotNull;

/**
 * Tag名称校验
 *
 * @author yuhao.wang3
 */
public final class GitNewTagValidator implements InputValidatorEx {

    private String myErrorText;

    private GitNewTagValidator() {
    }

    public static GitNewTagValidator newInstance() {
        return new GitNewTagValidator();
    }

    @Override
    public boolean checkInput(@NotNull String inputString) {
        if (!GitRefNameValidator.getInstance().checkInput(inputString)) {
            myErrorText = "无效的Tag名称";
            return false;
        }
        return true;
    }

    @Override
    public boolean canClose(String inputString) {
        return checkInput(inputString);
    }

    @Override
    public String getErrorText(String inputString) {
        return myErrorText;
    }
}
