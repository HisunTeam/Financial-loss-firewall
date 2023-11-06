package com.hisun.tower.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hisun.tower.common.vo.Result;
import com.hisun.tower.entity.SysUser;

public interface ISysUserService extends IService<SysUser> {
    /**
     * 校验用户是否有效
     * @param sysUser
     * @return
     */
    Result<ObjectNode> checkUserIsEffective(SysUser sysUser);
    /**
     * 添加用户和用户角色关系
     * @param user
     * @param roles
     */
    public void addUserWithRole(SysUser user,String roles);
}
