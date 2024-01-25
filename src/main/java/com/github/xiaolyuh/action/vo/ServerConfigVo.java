package com.github.xiaolyuh.action.vo;

import java.util.Map;

/**
 * 分支
 *
 * @author wyh
 */
public class ServerConfigVo {

    /**
     * URL
     */
    private String url;

    /**
     * header Map
     */
    private Map<String, String> headerMap;

    /**
     * param Map
     */
    private Map<String, String> paramMap;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }
}
