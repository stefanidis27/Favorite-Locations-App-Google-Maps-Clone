package com.arcgis.project.app.dao;

import com.arcgis.project.app.tableview.model.User;
import javafx.collections.ObservableList;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public static void getAllUsers(ObservableList<User> users) {
        String query = "SELECT id,email,admin FROM public.\"USERS\"";
        PreparedStatement preparedStatement;
        long id;
        String email;
        boolean adminStatus;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getLong("id");
                email = resultSet.getString("email");
                adminStatus = resultSet.getBoolean("admin");
                users.add(new User(
                        email,
                        LocationDao.getNumberOfFavoriteLocationsByUserID(id),
                        adminStatus ? "*" : "",
                        id
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Long> getAllUsersIDs() {
        String query = "SELECT id FROM public.\"USERS\" ORDER BY id ASC";
        PreparedStatement preparedStatement;
        List<Long> userIDs = new ArrayList<>();

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userIDs.add(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userIDs;
    }

    public static List<String> getAllUsersEmails() {
        String query = "SELECT email FROM public.\"USERS\"";
        PreparedStatement preparedStatement;
        List<String> emails = new ArrayList<>();

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                emails.add(resultSet.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emails;
    }

    public static void saveUser(long ID, String userEmail, String userPassword, boolean adminStatus) {
        String query = "INSERT INTO public.\"USERS\"(id, email, password, admin) VALUES(?, ?, ?, ?)";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, ID);
            preparedStatement.setString(2, userEmail);
            preparedStatement.setString(3, encryptPassword(userPassword));
            preparedStatement.setBoolean(4, adminStatus);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUserByID(long ID) {
        String query = "DELETE FROM public.\"USERS\" WHERE id=?";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, ID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static long getUserIDByEmailAndPassword(String userEmail, String userPassword) {
        long userID = -1L;
        String query = "SELECT id FROM public.\"USERS\" WHERE email=? AND password=?";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setString(1, userEmail);
            preparedStatement.setString(2, encryptPassword(userPassword));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userID = resultSet.getLong("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userID;
    }

    public static String getUserEmailByID(long userID) {
        String userEmail = null;
        String query = "SELECT email FROM public.\"USERS\" WHERE id=?";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userEmail = resultSet.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userEmail;
    }

    public static boolean getUserAdminStatusByID(long userID) {
        String query = "SELECT admin FROM public.\"USERS\" WHERE id=?";
        PreparedStatement preparedStatement;
        boolean adminStatus = false;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setLong(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                adminStatus = resultSet.getBoolean("admin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return adminStatus;
    }

    public static void updateUserEmailByID(long userID, String newUserEmail) {
        String query = "UPDATE public.\"USERS\" SET email=? WHERE id=?;";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setString(1, newUserEmail);
            preparedStatement.setLong(2, userID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUserPasswordByID(long userID, String newUserPassword) {
        String query = "UPDATE public.\"USERS\" SET password=? WHERE id=?;";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            preparedStatement.setString(1, newUserPassword);
            preparedStatement.setLong(2, userID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Long getNumberOfUsers() {
        long numberOfUsers = 0L;
        String query = "SELECT COUNT(*) FROM public.\"USERS\"";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = JDBCPostgreSQL.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                numberOfUsers = resultSet.getLong("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return numberOfUsers;
    }

    private static String encryptPassword(String password) {
        String encryptedPassword = null;
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(password.getBytes());
            byte[] bytes = m.digest();
            StringBuilder s = new StringBuilder();
            for (byte aByte : bytes) {
                s.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            encryptedPassword = s.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return encryptedPassword;
    }
}
