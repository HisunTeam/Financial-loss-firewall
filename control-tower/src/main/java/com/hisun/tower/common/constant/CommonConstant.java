package com.hisun.tower.common.constant;

public interface CommonConstant {
    /**
     * 删除标志
     */
    Integer DEL_FLAG_1 = 1;
    /**
     * 未删除
     */
    Integer DEL_FLAG_0 = 0;

    /**
     * 是否用户已被冻结 1正常(解冻) 2冻结 3离职
     */
    Integer USER_NORMAL = 1;
    Integer USER_FREEZE = 2;
    Integer USER_QUIT = 3;

    /** 登录用户Token令牌缓存KEY前缀 */
    String PREFIX_USER_TOKEN  = "prefix_user_token:";
}
