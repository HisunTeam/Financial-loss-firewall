package com.hisun.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hisun.tower.common.vo.Result;
import com.hisun.tower.entity.AlarmInstance;
import com.hisun.tower.service.IAlarmInstanceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/alarm/instance")
public class AlarmInstanceController {
    private final IAlarmInstanceService iAlarmInstanceService;

    public AlarmInstanceController(IAlarmInstanceService iAlarmInstanceService) {
        this.iAlarmInstanceService = iAlarmInstanceService;
    }

    @GetMapping(value = "/list")
    public Result<IPage<AlarmInstance>> queryAllPageList(@RequestParam(name = "page", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "limit", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(name = "name", required = false) String name,
                                                         @RequestParam(name = "sort", required = false) String sort) {
        QueryWrapper<AlarmInstance> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(sort)) {
            String order = sort.substring(0, 1);
            String column = sort.substring(1);
            queryWrapper.orderBy(true, StringUtils.equals("+", order), column);
        }

        if (StringUtils.isNotBlank(name)) {
            queryWrapper.eq("`name`", name);
        }

        Page<AlarmInstance> page = new Page<>(pageNo, pageSize);
        IPage<AlarmInstance> pageList = iAlarmInstanceService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<List<AlarmInstance>> queryAllList() {
        return Result.ok(iAlarmInstanceService.list());
    }

    @PostMapping("/add")
    public Result<Void> save(@RequestBody AlarmInstance alarmInstance) {
        if (iAlarmInstanceService.save(alarmInstance)) {
            return Result.OK("添加成功！");
        } else {
            return Result.error("添加失败！");
        }
    }

    @PutMapping("/edit")
    public Result<Void> update(@RequestBody AlarmInstance alarmInstance) {
        if (Objects.isNull(iAlarmInstanceService.getById(alarmInstance.getId()))) {
            return Result.error("未找到对应实体");
        }
        if (iAlarmInstanceService.updateById(alarmInstance)) {
            return Result.OK("添加成功！");
        } else {
            return Result.error("添加失败！");
        }
    }

    @DeleteMapping(value = "/delete")
    public Result<Void> delete(@RequestParam("id") String id) {
        iAlarmInstanceService.removeById(id);
        return Result.ok("删除成功");
    }

    @PostMapping("/test")
    public Result<Void> sendAlertMsgTest(@RequestBody AlarmInstance alarmInstance) {
        if (iAlarmInstanceService.testAlert(alarmInstance)) {
            return Result.OK("邮件连通性测试成功");
        } else {
            return Result.error("邮件连通性测试失败");
        }
    }
}
