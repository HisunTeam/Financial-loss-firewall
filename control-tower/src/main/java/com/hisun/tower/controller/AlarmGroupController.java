package com.hisun.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hisun.tower.common.vo.Result;
import com.hisun.tower.entity.AlarmGroup;
import com.hisun.tower.service.IAlarmGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/alarm/group")
public class AlarmGroupController {
    private final IAlarmGroupService iAlarmGroupService;

    public AlarmGroupController(IAlarmGroupService iAlarmGroupService) {
        this.iAlarmGroupService = iAlarmGroupService;
    }

    @GetMapping(value = "/list")
    public Result<IPage<AlarmGroup>> queryAllPageList(@RequestParam(name = "page", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "limit", defaultValue = "10") Integer pageSize,
                                                      @RequestParam(name = "name", required = false) String name,
                                                      @RequestParam(name = "sort", required = false) String sort) {
        QueryWrapper<AlarmGroup> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(sort)) {
            String order = sort.substring(0, 1);
            String column = sort.substring(1);
            queryWrapper.orderBy(true, StringUtils.equals("+", order), column);
        }

        if (StringUtils.isNotBlank(name)) {
            queryWrapper.eq("`name`", name);
        }

        Page<AlarmGroup> page = new Page<>(pageNo, pageSize);
        IPage<AlarmGroup> pageList = iAlarmGroupService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<List<AlarmGroup>> queryAllList() {
        return Result.ok(iAlarmGroupService.list());
    }

    @PostMapping("/add")
    public Result<Void> save(@RequestBody AlarmGroup alarmGroup) {
        if (iAlarmGroupService.save(alarmGroup)) {
            return Result.OK("添加成功！");
        } else {
            return Result.error("添加失败！");
        }
    }

    @PutMapping("/edit")
    public Result<Void> update(@RequestBody AlarmGroup alarmGroup) {
        if (Objects.isNull(iAlarmGroupService.getById(alarmGroup.getId()))) {
            return Result.error("未找到对应实体");
        }
        if (iAlarmGroupService.updateById(alarmGroup)) {
            return Result.OK("添加成功！");
        } else {
            return Result.error("添加失败！");
        }
    }

    @DeleteMapping(value = "/delete")
    public Result<Void> delete(@RequestParam("id") String id) {
        iAlarmGroupService.removeById(id);
        return Result.ok("删除成功");
    }
}
