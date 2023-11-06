package com.hisun.tower.config.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hisun.tower.entity.*;
import com.hisun.tower.mapper.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.toBoolean;

public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;

    public CustomUserDetailsService(SysUserMapper sysUserMapper, SysUserRoleMapper sysUserRoleMapper, SysRoleMapper sysRoleMapper, SysRolePermissionMapper sysRolePermissionMapper, SysPermissionMapper sysPermissionMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysRolePermissionMapper = sysRolePermissionMapper;
        this.sysPermissionMapper = sysPermissionMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUsername, username));

        if (sysUser == null) {
            throw new UsernameNotFoundException(username);
        }

        List<SysUserRole> sysUserRoleList = sysUserRoleMapper.selectList(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, sysUser.getId()));

        List<String> roleIds = sysUserRoleList.stream().map(SysUserRole::getRoleId).toList();

        List<SysRole> sysRoles = Collections.emptyList();
        List<SysRolePermission> sysRolePermissionList = Collections.emptyList();
        if (!roleIds.isEmpty()) {
            sysRoles = sysRoleMapper.selectList(new QueryWrapper<SysRole>().lambda().in(SysRole::getId, roleIds));
            sysRolePermissionList = sysRolePermissionMapper.selectList(new QueryWrapper<SysRolePermission>().lambda().in(SysRolePermission::getRoleId, roleIds));
        }

        List<String> permissionIds = sysRolePermissionList.stream().map(SysRolePermission::getPermissionId).toList();

        List<SysPermission> sysPermissionList = Collections.emptyList();
        if(!permissionIds.isEmpty()){
            sysPermissionList = sysPermissionMapper.selectList(new QueryWrapper<SysPermission>().lambda().in(SysPermission::getId, permissionIds));
        }

        return new User(sysUser.getUsername(),
                sysUser.getPassword(),
                toBoolean(sysUser.getDelFlag(), Integer.valueOf(0), Integer.valueOf(1)),
                true,
                true,
                toBoolean(sysUser.getStatus(), Integer.valueOf(1), Integer.valueOf(2)),
                getAuthorities(sysPermissionList)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<SysPermission> permissions) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.getName()))
                .forEach(authorities::add);
        return authorities;
    }

}
