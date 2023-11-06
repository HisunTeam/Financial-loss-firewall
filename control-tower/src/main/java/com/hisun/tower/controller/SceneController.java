package com.hisun.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hisun.tower.common.vo.Result;
import com.hisun.tower.entity.Scene;
import com.hisun.tower.service.ISceneService;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/scene/")
public class SceneController {
    private final ISceneService iSceneService;

    public SceneController(ISceneService iSceneService) {
        this.iSceneService = iSceneService;
    }

    @GetMapping(value = "/list")
    public Result<IPage<Scene>> queryAllPageList(@RequestParam(name = "page", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "limit", defaultValue = "10") Integer pageSize,
                                                 @RequestParam(name = "name", required = false) String name,
                                                 @RequestParam(name = "sort", required = false) String sort) {
        QueryWrapper<Scene> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(sort)) {
            String order = sort.substring(0, 1);
            String column = sort.substring(1);
            queryWrapper.orderBy(true, StringUtils.equals("+", order), column);
        }

        if (StringUtils.isNotBlank(name)) {
            queryWrapper.eq("name", name);
        }

        Page<Scene> page = new Page<>(pageNo, pageSize);
        IPage<Scene> pageList = iSceneService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody Scene scene) {
        Result<String> result = new Result<>();

        if (Objects.isNull(iSceneService.getById(scene.getId()))) {
            return result.error500("未找到对应实体");
        }

        iSceneService.updateById(scene);
        return result.success("修改成功!");
    }

    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Scene scene) {
        iSceneService.save(scene);
        return Result.ok("添加成功！");
    }

    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam("id") String id) {
        iSceneService.removeById(id);
        return Result.ok("删除成功");
    }

    @PostMapping(value = "/publish")
    public Result<Void> publish(@RequestBody Scene scene){
        iSceneService.publish(scene);
        return Result.ok("发布成功");
    }

    @PostMapping("/sendEmail")
    public Result<Void> sendEmail(@RequestBody Map<String, String> models) {
        iSceneService.sendEmail(models.get("id"), models.get("alarm"));
        return Result.ok();
    }
}
