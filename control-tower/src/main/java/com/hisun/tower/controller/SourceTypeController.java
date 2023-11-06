package com.hisun.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hisun.tower.common.vo.Result;
import com.hisun.tower.entity.OracleSource;
import com.hisun.tower.entity.SourceType;
import com.hisun.tower.service.ISourceTypeService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Source;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/source/type")
public class SourceTypeController {
    private final ISourceTypeService iSourceTypeService;

    public SourceTypeController(ISourceTypeService iSourceTypeService) {
        this.iSourceTypeService = iSourceTypeService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<SourceType>> queryAllPageList(@RequestParam(name = "page", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "limit", defaultValue = "10") Integer pageSize,
                                                      @RequestParam(name = "sourceType", required = false) String sourceType,
                                                      @RequestParam(name = "sort", required = false) String sort) {
        QueryWrapper<SourceType> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(sort)) {
            String order = sort.substring(0, 1);
            String column = sort.substring(1);
            queryWrapper.orderBy(true, StringUtils.equals("+", order), column);
        }

        if(StringUtils.isNotBlank(sourceType)){
            queryWrapper.eq("source_type", sourceType);
        }

        Page<SourceType> page = new Page<>(pageNo, pageSize);
        IPage<SourceType> pageList = iSourceTypeService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<List<SourceType>> queryAllList() {
        LambdaQueryWrapper<SourceType> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(SourceType::getId, SourceType::getSourceType, SourceType::getSourceName);
        return Result.ok(iSourceTypeService.list(lambdaQueryWrapper));
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody SourceType sourceType) {
        Result<String> result = new Result<>();

        if (Objects.isNull(iSourceTypeService.getById(sourceType.getId()))) {
            return result.error500("未找到对应实体");
        }

        iSourceTypeService.updateById(sourceType);
        return result.success("修改成功!");
    }

    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SourceType sourceType) {
        iSourceTypeService.save(sourceType);
        return Result.ok("添加成功！");
    }

    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam("id") String id) {
        iSourceTypeService.removeById(id);
        return Result.ok("删除成功");
    }
}
