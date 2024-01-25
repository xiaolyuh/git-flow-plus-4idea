package com.github.xiaolyuh.action.options;

import com.github.xiaolyuh.action.vo.BranchVo;
import java.util.List;

/**
 * 删除分支参数
 *
 * @author yuhao.wang3
 */
public class DeleteBranchOptions {
    /**
     * 删除的分支列表
     */
    private List<BranchVo> branches;

    /**
     * 是否删除本地分支
     */
    private boolean isDeleteLocalBranch;

    public List<BranchVo> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchVo> branches) {
        this.branches = branches;
    }

    public boolean isDeleteLocalBranch() {
        return isDeleteLocalBranch;
    }

    public void setDeleteLocalBranch(boolean deleteLocalBranch) {
        isDeleteLocalBranch = deleteLocalBranch;
    }
}
