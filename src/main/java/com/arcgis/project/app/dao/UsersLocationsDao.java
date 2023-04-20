package com.arcgis.project.app.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersLocationsDao {

    public static List<Long> getAllUserLocationLinkIDs() {
        String query = "SELECT id FROM public.\"USERS_LOCATIONS_LINK\" ORDER BY id ASC";
        PreparedStatement preparedStatement;
        List<Long> userLocationLinkIDs = new ArrayList<>();

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userLocationLinkIDs.add(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userLocationLinkIDs;
    }

    public static List<Long> getAllUserLocationLinkIDsByUserID(long userID) {
        String query = "SELECT id FROM public.\"USERS_LOCATIONS_LINK\" WHERE user_id=?";
        PreparedStatement preparedStatement;
        List<Long> userLocationLinkIDs = new ArrayList<>();

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userLocationLinkIDs.add(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userLocationLinkIDs;
    }

    public static void saveUserLocationLink(long ID, long userID, long locationID) {
        String query = "INSERT INTO public.\"USERS_LOCATIONS_LINK\"(id, user_id, location_id) VALUES(?, ?, ?)";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, ID);
            preparedStatement.setLong(2, userID);
            preparedStatement.setLong(3, locationID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUserLocationLinkByUserIDAndLocationID(long userID, long locationID) {
        String query = "DELETE FROM public.\"USERS_LOCATIONS_LINK\" WHERE user_id=? AND location_id=?";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, userID);
            preparedStatement.setLong(2, locationID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Long getNumberOfFavoriteLocationsByUserID(long userID) {
        long numberOfFavoriteLocations = 0L;
        String query = "SELECT COUNT(*) FROM public.\"USERS_LOCATIONS_LINK\" WHERE user_id=?";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                numberOfFavoriteLocations = resultSet.getLong("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return numberOfFavoriteLocations;
    }

}
