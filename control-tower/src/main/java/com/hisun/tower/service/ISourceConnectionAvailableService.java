package com.hisun.tower.service;

import com.hisun.tower.model.TestConnection;

import java.sql.SQLException;
import java.util.List;

public interface ISourceConnectionAvailableService {
    boolean test(TestConnection testConnection) throws SQLException;
    List<String> queryTableColum(TestConnection testConnection) throws SQLException;
}
