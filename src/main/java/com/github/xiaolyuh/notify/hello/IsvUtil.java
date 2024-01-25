package com.github.xiaolyuh.notify.hello;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 描述:第三方签名工具类
 *
 * @author haojinlong
 * @date 2021/4/26
 */
public class IsvUtil {


    private IsvUtil() {
    }

    /**
     * 签名有效时间:30秒
     */
    private static final long VALID_TIME = 30000;

    /**
     * 根据参数创建签名
     * <p>
     * 适用于有加密数据的场景
     * </p>
     *
     * @param secretKey 秘钥
     * @param appKey    应用key
     * @param signData  加密数据,如果没有需要加密的数据
     * @return {@link String} 签名
     * @author haojinlong
     * @date 2021/4/26
     */
    public static String createSignWithSignData(String secretKey, String appKey, Object signData) {

        SignBody<Object> payload = new SignBody<>();
        payload.setTimestamp(System.currentTimeMillis());
        payload.setSecretKey(secretKey);
        payload.setAppKey(appKey);
        payload.setData(signData);

        return AESUtil.encrypt(toJson(payload), secretKey);
    }

    /**
     * 根据参数创建签名
     * <p>
     * 适用于无加密数据的场景
     * </p>
     *
     * @param secretKey 秘钥
     * @param appKey    应用key
     * @return {@link String} 签名
     * @author haojinlong
     * @date 2021/4/26
     */
    public static String createSign(String secretKey, String appKey) {
        SignBody<Object> payload = new SignBody<>();
        payload.setTimestamp(System.currentTimeMillis());
        payload.setSecretKey(secretKey);
        payload.setAppKey(appKey);

        return AESUtil.encrypt(toJson(payload), secretKey);
    }

    /**
     * 校验签名有效性
     * <p>
     * 该方法校验有效性时,非法请求会以{@link SignVerifyResult}值返回
     * </p>
     *
     * @param secretKey 秘钥
     * @param sign      签名
     * @return 解密后的数据
     */
    public static SignVerifyResult verifySign(String secretKey, String sign) {

        SignBody signData = null;
        try {
            signData = fromJson(AESUtil.decrypt(sign, secretKey), SignBody.class);
        } catch (Exception exception) {
            return new SignVerifyResult(SignEnum.IM1403);
        }


        if (System.currentTimeMillis() > signData.getTimestamp() + VALID_TIME) {
            return new SignVerifyResult(SignEnum.IM1402);
        }

        return new SignVerifyResult(true);
    }

    /**
     * 校验签名并获取签名加密内容
     * <p>
     * 该方法校验有效性时,非法请求会以异常抛出
     * </p>
     *
     * @param secretKey    秘钥
     * @param sign         签名
     * @param responseType 响应类型
     * @return 解密后的数据
     */
    public static <T> T verifySignAndGetSignBody(String secretKey, String sign, Class<T> responseType) {
        return parseSignData(secretKey, sign, responseType, true);
    }

    /**
     * 将json字符串转为指定的对象
     *
     * @param value json字符串
     * @param clazz 指定对象的字节码
     * @return 指定对象
     */
    private static <T> T fromJson(String value, Class<T> clazz) {

        try {
            return SingletonObject.OBJECT_MAPPER.readValue(value, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 只解密数据不验证是否超时
     *
     * @param secretKey    密钥
     * @param sign         签名数据
     * @param responseType 响应的数据类型
     * @return {@link T}
     * @author hanlining
     * @date 2021/6/7
     */
    public static <T> T decryptSignData(String secretKey, String sign, Class<T> responseType) {
        return parseSignData(secretKey, sign, responseType, false);
    }


    /**
     * 解析签名数据
     *
     * @param secretKey          密钥
     * @param sign               签名数据
     * @param responseType       响应的数据类型
     * @param needVerifySignTime 是否需要验证签名是否超时
     * @return {@link T}
     * @author hanlining
     * @date 2021/6/7
     */
    public static <T> T parseSignData(String secretKey, String sign, Class<T> responseType, boolean needVerifySignTime) {

        SignBody signBody = null;
        try {
            signBody = fromJson(AESUtil.decrypt(sign, secretKey), SignBody.class);
        } catch (Exception exception) {
            throw new RuntimeException(String.format("签名解析失败, sign =%s", sign), exception);
        }


        if (needVerifySignTime && System.currentTimeMillis() > signBody.getTimestamp() + VALID_TIME) {
            throw new RuntimeException(String.format("签名已超时, sign =%s", sign));
        }
        if (null == signBody.getData()) {
            throw new RuntimeException(String.format("签名解析失败, sign =%s", sign));
        }

        return fromJson(toJson(signBody.getData()), responseType);
    }

    /**
     * 将对象转为json
     *
     * @param value 要转换的对象
     * @return json字符串
     */
    private static String toJson(Object value) {
        if (null == value) {
            throw new NullPointerException("StringUtil.toJson(null)");
        }
        try {
            return SingletonObject.OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
