package com.github.xiaolyuh.notify;

import com.alibaba.fastjson.JSON;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * OkHttpClient工具
 *
 * @author yuhao.wang3
 */
public abstract class OkHttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(OkHttpClientUtil.class);

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    /**
     * 发起 application/json 的 post 请求
     *
     * @param url           地址
     * @param param         参数
     * @param interfaceName 接口名称
     * @return T
     * @throws Exception Exception
     */
    public static <T> T postApplicationJson(String url, Object param, String interfaceName, Class<T> clazz) {
        // 生成requestBody
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , JSON.toJSONString(param));

        return post(url, interfaceName, requestBody, param, null, clazz);
    }

    /**
     * 发起 application/json 的 post 请求
     *
     * @param url           地址
     * @param param         参数
     * @param interfaceName 接口名称
     * @return T
     * @throws Exception Exception
     */
    public static <T> T postApplicationJson(String url, Object param, Map<String, String> headers, String interfaceName, Class<T> clazz) {
        // 生成requestBody
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , JSON.toJSONString(param));

        return post(url, interfaceName, requestBody, param, headers, clazz);
    }

    /**
     * 发起post请求，不做任何签名
     *
     * @param url           发送请求的URL
     * @param interfaceName 接口名称
     * @param requestBody   请求体
     * @param param         参数
     */
    public static <T> T post(String url, String interfaceName, RequestBody requestBody, Object param, Map<String, String> headers, Class<T> clazz) {
        Request.Builder builder = new Request.Builder()
                //请求的url
                .url(url)
                .post(requestBody);

        if (!isEmpty(headers)) {
            for (String key : headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        Request request = builder.build();

        Response response = null;
        String result = "";
        String errorMsg = "";
        try {
            //创建/Call
            response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                logger.error("访问外部系统异常 {}: {}", url, response.toString());
                errorMsg = String.format("访问外部系统异常:%s", response.toString());
                throw new RuntimeException(errorMsg);
            }
            result = response.body().string();
        } catch (RuntimeException e) {
            logger.warn(e.getMessage(), e);
            result = e.getMessage();
            throw e;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            if (Objects.isNull(response)) {
                errorMsg = String.format("访问外部系统异常::%s", e.getMessage());
                throw new RuntimeException(errorMsg, e);
            }
            errorMsg = String.format("访问外部系统异常:::%s", response.toString());
            throw new RuntimeException(errorMsg, e);
        } finally {
            logger.info("请求 {}  {}，请求参数：{}, header:{}, 返回参数：{}", interfaceName, url, JSON.toJSONString(param),
                    JSON.toJSONString(headers), StringUtils.isEmpty(result) ? errorMsg : result);
        }

        return JSON.parseObject(result, clazz);
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }
}
