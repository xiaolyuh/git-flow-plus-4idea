package com.github.xiaolyuh.action.vo;

import java.util.Objects;

/**
 * 分支
 *
 * @author wyh
 */
public class BranchVo {

    private String id;

    /**
     * 分支最后提交时间（按提交时间排序）
     */
    private String lastCommitDate;

    /**
     * 分支
     */
    private String branch;

    /**
     * 创建人
     */
    private String createUser;

    public String getLastCommitDate() {
        return lastCommitDate;
    }

    public void setLastCommitDate(String lastCommitDate) {
        this.lastCommitDate = lastCommitDate;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BranchVo branchVo = (BranchVo) o;
        return Objects.equals(getBranch(), branchVo.getBranch());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBranch());
    }
}
