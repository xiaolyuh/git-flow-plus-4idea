package com.github.xiaolyuh.notify.hello;


/**
 * 描述:签名用的实体
 *
 * @author haojinlong
 * @date 2021/4/26
 */
public class SignBody<T> {

    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 秘钥
     */
    private String secretKey;
    /**
     * appKey
     */
    private String appKey;
    /**
     * 数据
     */
    private T data;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
