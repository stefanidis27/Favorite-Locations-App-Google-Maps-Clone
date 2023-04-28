package com.arcgis.project.app.dao;

import com.arcgis.project.app.tableview.model.FavoriteLocation;
import com.arcgis.project.app.tableview.model.PopularLocation;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LocationDao {

    public record PopularLocationArcgis(double latitude, double longitude, long numberOfUsers) {

        public long getNumberOfUsers() {
            return numberOfUsers;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }
    }

    public static List<Long> getAllLocationsIDs() {
        String query = "SELECT id FROM public.\"LOCATIONS\" ORDER BY id ASC";
        PreparedStatement preparedStatement;
        List<Long> locationIDs = new ArrayList<>();

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                locationIDs.add(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return locationIDs;
    }

    public static void saveLocation(
            long ID, String title, String description,
            Double latitude, Double longitude, Timestamp timestamp, long userID
    ) {
        String query = "INSERT INTO public.\"LOCATIONS\"(id, title, description, latitude, longitude, timestamp, user_id) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, ID);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, description);
            preparedStatement.setDouble(4, latitude);
            preparedStatement.setDouble(5, longitude);
            preparedStatement.setTimestamp(6, timestamp);
            preparedStatement.setLong(7, userID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteLocationByID(long ID) {
        String query = "DELETE FROM public.\"LOCATIONS\" WHERE id=?";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, ID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getAllLocationsByUserID(ObservableList<FavoriteLocation> favoriteLocations, long userID) {
        String query = "SELECT public.\"LOCATIONS\".* FROM public.\"LOCATIONS\" "
                + "WHERE public.\"LOCATIONS\".user_id=?";
        PreparedStatement preparedStatement;
        long id;
        String title, description;
        double latitude, longitude;
        Timestamp timestamp;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getLong("id");
                title = resultSet.getString("title");
                description = resultSet.getString("description");
                latitude = resultSet.getDouble("latitude");
                longitude = resultSet.getDouble("longitude");
                timestamp = resultSet.getTimestamp("timestamp");
                String[] timestampData = timestamp.toString().split("[ ]");
                favoriteLocations.add(new FavoriteLocation(
                        title,
                        description,
                        latitude,
                        longitude,
                        timestampData[0],
                        timestampData[1].substring(0, timestampData[1].length() - 2),
                        id
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Long> getAllLocationIDsByUserID(long userID) {
        String query = "SELECT public.\"LOCATIONS\".id FROM public.\"LOCATIONS\" "
                + "WHERE public.\"LOCATIONS\".user_id=?";
        PreparedStatement preparedStatement;
        List<Long> locationIDs = new ArrayList<>();
        long id;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getLong("id");
                locationIDs.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return locationIDs;
    }

    public static Long getNumberOfFavoriteLocationsByUserID(long userID) {
        String query = "SELECT COUNT(*) FROM public.\"LOCATIONS\" "
                + "WHERE public.\"LOCATIONS\".user_id=?";
        PreparedStatement preparedStatement;
        long numberOfFavoriteLocations = 0L;

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

    public static void getAllLocationsByUserIDForArcgis(List<Point> favoriteLocationsPoints, long userID) {
        String query = "SELECT public.\"LOCATIONS\".latitude,public.\"LOCATIONS\".longitude "
                + "FROM public.\"LOCATIONS\" "
                + "WHERE public.\"LOCATIONS\".user_id=?";
        PreparedStatement preparedStatement;
        double latitude, longitude;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                latitude = resultSet.getDouble("latitude");
                longitude = resultSet.getDouble("longitude");
                favoriteLocationsPoints.add(new Point(longitude, latitude, SpatialReferences.getWgs84()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getAllLocationsPopularForArcGIS(List<Point> popularLocationsPoints) {
        String query = "SELECT public.\"LOCATIONS\".latitude, public.\"LOCATIONS\".longitude, COUNT(*) " +
                "FROM public.\"LOCATIONS\" " +
                "GROUP BY public.\"LOCATIONS\".latitude, public.\"LOCATIONS\".longitude ";
        PreparedStatement preparedStatement;
        List<PopularLocationArcgis> popularLocations = new ArrayList<>();
        double latitude, longitude;
        long numberOfUsers;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                latitude = resultSet.getDouble("latitude");
                longitude = resultSet.getDouble("longitude");
                numberOfUsers = resultSet.getLong("count") - getNumberOfDuplicatesLocationByLatitudeAndLongitude(
                        latitude,
                        longitude
                );
                popularLocations.add(new PopularLocationArcgis(latitude, longitude, numberOfUsers));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        popularLocations.sort(Comparator.comparing(PopularLocationArcgis::getNumberOfUsers).reversed());
        int numberOfShownLocations = Math.min(popularLocations.size(), 10);
        for (int i = 0; i < numberOfShownLocations; i++) {
            Point point = new Point(
                    popularLocations.get(i).getLongitude(),
                    popularLocations.get(i).getLatitude(),
                    SpatialReferences.getWgs84()
            );
            popularLocationsPoints.add(point);
        }
    }

    public static void getAllLocationsPopular(ObservableList<PopularLocation> popularLocations, long userID) {
        String query = "SELECT public.\"LOCATIONS\".latitude, public.\"LOCATIONS\".longitude, COUNT(*) " +
                "FROM public.\"LOCATIONS\" " +
                "GROUP BY public.\"LOCATIONS\".latitude, public.\"LOCATIONS\".longitude";
        PreparedStatement preparedStatement;
        double latitude, longitude;
        long numberOfUsers;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                latitude = resultSet.getDouble("latitude");
                longitude = resultSet.getDouble("longitude");
                numberOfUsers = resultSet.getLong("count");
                popularLocations.add(new PopularLocation(
                        latitude,
                        longitude,
                        numberOfUsers - getNumberOfDuplicatesLocationByLatitudeAndLongitude(
                                latitude,
                                longitude
                        ),
                        checkIfPopularLocationByLatitudeAndLongitudeIsSelectedByUserID(userID, latitude, longitude)
                                ? "*" : ""
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Long getNumberOfDuplicatesLocationByLatitudeAndLongitude(double latitude, double longitude) {
        long totalNumberOfDuplicates = 0L;
        List<Long> userIDs = UserDao.getAllUsersIDs();
        for (Long userID : userIDs) {
            totalNumberOfDuplicates += getNumberOfDuplicatesLocationByLatitudeAndLongitudeForUserByID(
                    userID,
                    latitude,
                    longitude
            );
        }
        return totalNumberOfDuplicates;
    }

    private static Long getNumberOfDuplicatesLocationByLatitudeAndLongitudeForUserByID(
            long userID,
            double latitude,
            double longitude
    ) {
        String query = "SELECT COUNT(*) FROM public.\"LOCATIONS\" " +
                "WHERE public.\"LOCATIONS\".user_id=? " +
                "AND public.\"LOCATIONS\".latitude=?\n" +
                "AND public.\"LOCATIONS\".longitude=?";
        long numberOfDuplicates = 0L;
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, userID);
            preparedStatement.setDouble(2, latitude);
            preparedStatement.setDouble(3, longitude);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                numberOfDuplicates = resultSet.getLong("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (numberOfDuplicates == 0L || numberOfDuplicates == 1L) {
            return 0L;
        }
        return numberOfDuplicates - 1;
    }

    private static boolean checkIfPopularLocationByLatitudeAndLongitudeIsSelectedByUserID(
            long userID,
            double latitude,
            double longitude
    ) {
        String query = "SELECT public.\"LOCATIONS\".id FROM public.\"LOCATIONS\" " +
                "WHERE public.\"LOCATIONS\".user_id=? " +
                "AND public.\"LOCATIONS\".latitude=?\n" +
                "AND public.\"LOCATIONS\".longitude=?";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, userID);
            preparedStatement.setDouble(2, latitude);
            preparedStatement.setDouble(3, longitude);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
