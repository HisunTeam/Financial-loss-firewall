package com.hisun.tower.service.impl;

import com.hisun.tower.model.TestConnection;
import com.hisun.tower.service.ISourceConnectionAvailableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class SourceConnectionAvailableServiceImpl implements ISourceConnectionAvailableService {
    @Override
    public boolean test(TestConnection testConnection) throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", testConnection.getUsername());
        connectionProps.put("password", testConnection.getPassword());

        try (Connection connection = DriverManager.getConnection(createConnectionURL(testConnection), connectionProps); PreparedStatement ps = createTestPreparedStatement(testConnection, connection); ResultSet rs = ps.executeQuery()) {
            return true;
        }
    }

    @Override
    public List<String> queryTableColum(TestConnection testConnection) throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", testConnection.getUsername());
        connectionProps.put("password", testConnection.getPassword());

        try (Connection connection = DriverManager.getConnection(createConnectionURL(testConnection), connectionProps); PreparedStatement ps = createQueryPreparedStatement(testConnection, connection); ResultSet rs = ps.executeQuery()) {
            List<String> list = new ArrayList<>();
            while (rs.next()) {
                list.add((String) rs.getObject(1));
            }
            return list;
        }

    }

    private String createConnectionURL(TestConnection testConnection) {
        String dbms = testConnection.getDbms();
        boolean isRac = testConnection.isRac();
        String hostname = testConnection.getHostname();
        int port = testConnection.getPort();
        String database = testConnection.getDatabase();
        String url = null;
        if (dbms.equals("oracle")) {
            if (isRac) {
                // jdbc:oracle:thin:@//myhost.example.com:1521/my_service
                url = "jdbc:oracle:thin:@//" + hostname + ":" + port + "/" + database;
            } else {
                //  jdbc:oracle:thin:@myhost.us.example.com:1521:prod
                url = "jdbc:oracle:thin:@" + hostname + ":" + port + ":" + database;
            }
        }
        return url;
    }

    private PreparedStatement createTestPreparedStatement(TestConnection testConnection, Connection connection) throws SQLException {
        String sql = "select * from " + testConnection.getTable() + " where rownum = 1";
        return connection.prepareStatement(sql);
    }

    private PreparedStatement createQueryPreparedStatement(TestConnection testConnection, Connection connection) throws SQLException {
        String sql = "select t.column_name from user_col_comments t where t.table_name = '" + testConnection.getTable().toUpperCase() + "'";
        return connection.prepareStatement(sql);
    }
}
