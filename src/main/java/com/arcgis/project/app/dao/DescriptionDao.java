package com.arcgis.project.app.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DescriptionDao {

    public static String getDescription() {
        String description = null;
        String query = "SELECT description FROM public.\"DESCRIPTION\"";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                description = resultSet.getString("description");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return description;
    }

    public static void updateDescription(String newDescription) {
        String query = "UPDATE public.\"DESCRIPTION\" SET description=?;";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setString(1, newDescription);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
