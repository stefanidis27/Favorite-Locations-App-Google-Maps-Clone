package com.arcgis.project.app.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCPostgreSQL {

    private static final JDBCPostgreSQL instance = new JDBCPostgreSQL();
    public static Connection connection;

    private void connectToDatabase() {
        String url = "jdbc:postgresql://localhost:5432/project-arcgis-app-db";
        String user = "postgres";
        String password = "postgres";

        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JDBCPostgreSQL() {
        connectToDatabase();
    }

    public static JDBCPostgreSQL getInstance(){
        return instance;
    }

}
