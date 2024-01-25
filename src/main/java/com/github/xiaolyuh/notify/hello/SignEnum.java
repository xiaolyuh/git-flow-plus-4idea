package com.github.xiaolyuh.notify.hello;

public enum SignEnum {
    IM1402("IM1402", "签名过期"),
    IM1403("IM1403", "签名错误");

    private final String code;
    private final String value;

    private SignEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
}
