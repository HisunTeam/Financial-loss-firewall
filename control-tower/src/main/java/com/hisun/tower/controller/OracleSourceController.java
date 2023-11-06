package com.hisun.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hisun.tower.common.vo.Result;
import com.hisun.tower.entity.OracleSource;
import com.hisun.tower.service.IOracleSourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/source/oracle")
public class OracleSourceController {
    private final IOracleSourceService iOracleSourceService;

    public OracleSourceController(IOracleSourceService iOracleSourceService) {
        this.iOracleSourceService = iOracleSourceService;
    }

    @GetMapping(value = "/list")
    public Result<IPage<OracleSource>> queryAllPageList(@RequestParam(name = "page", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "limit", defaultValue = "10") Integer pageSize,
                                                        @RequestParam(name = "table", required = false) String table,
                                                        @RequestParam(name = "sort", required = false) String sort) {
        QueryWrapper<OracleSource> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(sort)) {
            String order = sort.substring(0, 1);
            String column = sort.substring(1);
            queryWrapper.orderBy(true, StringUtils.equals("+", order), column);
        }

        if (StringUtils.isNotBlank(table)) {
            queryWrapper.eq("`table`", table);
        }

        Page<OracleSource> page = new Page<>(pageNo, pageSize);
        IPage<OracleSource> pageList = iOracleSourceService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<List<OracleSource>> queryAllList() {
        return Result.ok(iOracleSourceService.list());
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody OracleSource oracleSource) {
        Result<String> result = new Result<>();

        if (Objects.isNull(iOracleSourceService.getById(oracleSource.getId()))) {
            return result.error500("未找到对应实体");
        }

        iOracleSourceService.updateById(oracleSource);
        return result.success("修改成功!");
    }

    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody OracleSource oracleSource) {
        iOracleSourceService.save(oracleSource);
        return Result.ok("添加成功！");
    }

    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam("id") String id) {
        iOracleSourceService.removeById(id);
        return Result.ok("删除成功");
    }
}
