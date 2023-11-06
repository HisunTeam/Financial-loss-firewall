package com.hisun.tower.controller;

import com.hisun.tower.common.vo.Result;
import com.hisun.tower.model.TestConnection;
import com.hisun.tower.service.ISourceConnectionAvailableService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/source/connection")
public class SourceConnectionAvailableController {

    private final ISourceConnectionAvailableService iSourceConnectionAvailableService;

    public SourceConnectionAvailableController(ISourceConnectionAvailableService iSourceConnectionAvailableService) {
        this.iSourceConnectionAvailableService = iSourceConnectionAvailableService;
    }

    @PostMapping(value = "/test")
    public Result<String> test(@RequestBody TestConnection testConnection) {
        boolean result;
        try {
            result = iSourceConnectionAvailableService.test(testConnection);
        } catch (SQLException e) {
            return Result.error(e.getMessage());
        }

        if (result) {
            return Result.ok("连接成功！");
        }
        return Result.error("连接失败！");
    }

    @PostMapping(value = "/queryTableColumn")
    public Result<List<String>> queryTableColumn(@RequestBody TestConnection testConnection) {
        List<String> list;
        try {
            list = iSourceConnectionAvailableService.queryTableColum(testConnection);
        } catch (SQLException e) {
            return Result.error(e.getMessage());
        }
        return Result.ok(list);
    }

}
