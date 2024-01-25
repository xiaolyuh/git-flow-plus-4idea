package com.github.xiaolyuh.action.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * 分支
 *
 * @author wyh
 */
public class CommitMessageVo {

    /**
     * commit id
     */
    private String commitId;

    /**
     * shortId
     */
    private String shortId;

    /**
     * 提交时间
     */
    private String commitDate;

    /**
     * 提交消息
     */
    private String summary;

    /**
     * 提交消息
     */
    private String description;

    /**
     * 提交人
     */
    private String author;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 分支
     */
    private String branch;

    /**
     * git项目
     */
    private String project;

    /**
     * 影响文件列表
     */
    private String modifyFile;

    /**
     * 完整消息
     */
    private String fullMessage;

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(String commitDate) {
        this.commitDate = commitDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getModifyFile() {
        return modifyFile;
    }

    public void setModifyFile(String modifyFile) {
        this.modifyFile = modifyFile;
    }

    public String getFullMessage() {
        return fullMessage;
    }

    public void setFullMessage(String fullMessage) {
        this.fullMessage = fullMessage;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("commitId", commitId);
        map.put("shortId", shortId);
        map.put("commitDate", commitDate);
        map.put("summary", summary);
        map.put("description", description);
        map.put("authorOa", author);
        map.put("author", author);
        map.put("email", email);
        map.put("branch", branch);
        map.put("project", project);
        map.put("modifyFile", modifyFile);
        map.put("fullMsg", fullMessage);
        map.put("sourceType", "1");
        return map;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }
}
