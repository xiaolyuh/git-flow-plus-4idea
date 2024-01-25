package com.github.xiaolyuh.notify.hello;

public class SignVerifyResult {

    public SignVerifyResult(SignEnum error, boolean success) {
        this.error = error;
        this.success = success;
    }

    public SignVerifyResult(SignEnum error) {
        this.error = error;
    }

    public SignVerifyResult(boolean success) {
        this.success = success;
    }

    private SignEnum error;
    private boolean success;

    public SignEnum getError() {
        return this.error;
    }

    public void setError(SignEnum error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
