package com.github.xiaolyuh.action.options;

/**
 * 初始化参数
 *
 * @author yuhao.wang3
 */
public class InitOptions {

    /**
     * 主干分支名称
     */
    private String masterBranch;

    /**
     * 发布分支名称
     */
    private String releaseBranch;

    /**
     * 测试分支名称
     */
    private String testBranch;

    /**
     * 开发分支前缀
     */
    private String featurePrefix;

    /**
     * 修复分支前缀
     */
    private String hotfixPrefix;

    /**
     * 版本前缀
     */
    private String tagPrefix;

    /**
     * 钉钉Token
     */
    private String dingtalkToken;

    /**
     * 云图梭 token
     */
    private String helloToken;

    /**
     * (?:fix|chore|docs|feat|refactor|style|test)(?:\(.*\))?\s+(?:XM\d{7}-\d+)(?:\s*)(?:\S+)(?:[\n\r]{2,})(?:[\s\S]*)
     * (?:feat|fix|test|refactor|docs|style|chore)(?:\(.*\))? [A-Z].*\s#\d+
     * (?:.*XM\d{7}-\d+.*)
     * Commit 规范校验规则
     */
    private String pattern = "(?:fix|chore|docs|feat|refactor|style|test)(?:\\(.*\\))?\\s+(?:XM\\d{7}-\\d+)(?:\\s*)(?:\\S+)(?:[\\n\\r]{2,})(?:[\\s\\S]*)";

    /**
     * Commit 规范说明
     */
    private String patternExplain = "规范说明：https://blog.csdn.net/xiaolyuh123/article/details/129042182\n" +
            "正确示例：\n\n" +
            "feat(web) XM2102301-30 用户目标地图\n" +
            "\n" +
            "背景：\n" +
            "http://jira.xxx.com/browse/XM2607301-30\n" +
            "修改：\n" +
            "1. 使用idea自带http工具来替换postman\n" +
            "2. 新增查询我的目标接口\n" +
            "影响：\n" +
            "1. 影响PC端个人目标地图显示";

    public String getMasterBranch() {
        return masterBranch;
    }

    public void setMasterBranch(String masterBranch) {
        this.masterBranch = masterBranch;
    }

    public String getReleaseBranch() {
        return releaseBranch;
    }

    public void setReleaseBranch(String releaseBranch) {
        this.releaseBranch = releaseBranch;
    }

    public String getTestBranch() {
        return testBranch;
    }

    public void setTestBranch(String testBranch) {
        this.testBranch = testBranch;
    }

    public String getFeaturePrefix() {
        return featurePrefix;
    }

    public void setFeaturePrefix(String featurePrefix) {
        this.featurePrefix = featurePrefix;
    }

    public String getHotfixPrefix() {
        return hotfixPrefix;
    }

    public void setHotfixPrefix(String hotfixPrefix) {
        this.hotfixPrefix = hotfixPrefix;
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public void setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    public String getDingtalkToken() {
        return dingtalkToken;
    }

    public void setDingtalkToken(String dingtalkToken) {
        this.dingtalkToken = dingtalkToken;
    }

    public String getHelloToken() {
        return helloToken;
    }

    public void setHelloToken(String helloToken) {
        this.helloToken = helloToken;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPatternExplain() {
        return patternExplain;
    }

    public void setPatternExplain(String patternExplain) {
        this.patternExplain = patternExplain;
    }
}
