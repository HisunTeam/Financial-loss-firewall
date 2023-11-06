package com.hisun.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hisun.tower.common.vo.Result;
import com.hisun.tower.entity.Rule;
import com.hisun.tower.service.IRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/rule/")
public class RuleController {
    private final IRuleService iRuleService;

    public RuleController(IRuleService iRuleService) {
        this.iRuleService = iRuleService;
    }

    @GetMapping(value = "/list")
    public Result<IPage<Rule>> queryAllPageList(@RequestParam(name = "page", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "limit", defaultValue = "10") Integer pageSize,
                                                @RequestParam(name = "name", required = false) String name,
                                                @RequestParam(name = "sort", required = false) String sort) {
        QueryWrapper<Rule> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(sort)) {
            String order = sort.substring(0, 1);
            String column = sort.substring(1);
            queryWrapper.orderBy(true, StringUtils.equals("+", order), column);
        }

        if (StringUtils.isNotBlank(name)) {
            queryWrapper.eq("name", name);
        }

        Page<Rule> page = new Page<>(pageNo, pageSize);
        IPage<Rule> pageList = iRuleService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<List<Rule>> queryAllList() {
        LambdaQueryWrapper<Rule> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(Rule::getId, Rule::getName);
        return Result.ok(iRuleService.list(lambdaQueryWrapper));
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody Rule rule) {
        Result<String> result = new Result<>();

        if (Objects.isNull(iRuleService.getById(rule.getId()))) {
            return result.error500("未找到对应实体");
        }

        iRuleService.updateById(rule);
        return result.success("修改成功!");
    }

    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Rule rule) {
//        query().eq("id", value).one();
        LambdaQueryWrapper<Rule> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Rule::getName, rule.getName());
        boolean exist = iRuleService.exists(lambdaQueryWrapper);
        if (exist) {
            return Result.error("已经存在同名规则！");
        }
        iRuleService.save(rule);
        return Result.ok("添加成功！");
    }

    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam("id") String id) {
        iRuleService.removeById(id);
        return Result.ok("删除成功");
    }
}
