package com.arcgis.project.app.service;

import com.arcgis.project.app.dao.LocationDao;
import com.arcgis.project.app.dao.UserDao;
import com.arcgis.project.app.tableview.model.FavoriteLocation;
import com.arcgis.project.app.utils.Utils;
import com.esri.arcgisruntime.geometry.Point;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserService {

    public static void searchByPOIKeywordByName(TextField insertedPOIKeyword) {
        String poiKeyword = insertedPOIKeyword.getText();
        if (!poiKeyword.isEmpty()) {
            ArcGISService.performLocalSearchByName(poiKeyword);
        }
    }

    public static void showUserFavoriteLocations(long userID) {
        List<Point> userFavoriteLocations = new ArrayList<>();
        LocationService.getAllUserLocationsForArcGIS(userFavoriteLocations, userID);
        ArcGISService.displayDatabaseLocations(userFavoriteLocations);
    }

    public static String getCurrentUserEmail(long userID) {
        return UserDao.getUserEmailByID(userID);
    }

    public static void updateUserEmail(
            long userID, String newEmail,
            Label currentEmailLabel,
            TextField insertedNewEmail
    ) {
        UserDao.updateUserEmailByID(userID, Utils.textWithoutWhiteSpaces(newEmail));
        currentEmailLabel.setText(insertedNewEmail.getText());
    }

    public static void updateUserPassword(long userID, String newPassword) {
        UserDao.updateUserPasswordByID(userID, Utils.textWithoutWhiteSpaces(newPassword));
    }

    public static Long getNumberOfFavoriteLocations(long userID) {
        return LocationDao.getNumberOfFavoriteLocationsByUserID(userID);
    }

    public static void getAllUserLocations(ObservableList<FavoriteLocation> favoriteLocations, long userID) {
        LocationService.getUserLocationsByUserID(favoriteLocations, userID);
    }

    public static void addUserNewLocation(
            long ID,
            TextField insertedTitleOfFavoriteLocation,
            TextField insertedShortDescriptionOfFavoriteLocation,
            Label numberOfFavoriteLocationsLabel,
            TableView<FavoriteLocation> favoriteLocationsTableView
    ) {
        Timestamp timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
        String[] timestampData = timestamp.toString().split("[ ]");
        String[] timeData = timestampData[1].split("[.]");
        FavoriteLocation newFavoriteLocation = new FavoriteLocation(
                insertedTitleOfFavoriteLocation.getText(),
                insertedShortDescriptionOfFavoriteLocation.getText(),
                ArcGISService.currentLatitude,
                ArcGISService.currentLongitude,
                timestampData[0],
                timeData[0],
                LocationService.generateNextLocationID()
        );
        LocationService.createNewFavoriteLocation(newFavoriteLocation, ID);

        long updatedNumber = Long.parseLong(numberOfFavoriteLocationsLabel.getText()) + 1;
        numberOfFavoriteLocationsLabel.setText(Long.toString(updatedNumber));
        insertedTitleOfFavoriteLocation.setText("");
        insertedShortDescriptionOfFavoriteLocation.setText("");
        favoriteLocationsTableView.getItems().add(newFavoriteLocation);
    }

    public static List<Long> getUserLocationIDs(long ID) {
        return LocationDao.getAllLocationIDsByUserID(ID);
    }

    public static void deleteUserLocation(
            FavoriteLocation selectedFavoriteLocation,
            Label numberOfFavoriteLocationsLabel,
            TableView<FavoriteLocation> favoriteLocationsTableView
    ) {
        LocationService.deleteUserLocationByLocationID(selectedFavoriteLocation.getId());

        long updatedNumber = Long.parseLong(numberOfFavoriteLocationsLabel.getText()) - 1;
        numberOfFavoriteLocationsLabel.setText(Long.toString(updatedNumber));

        int selectedRowIndex = favoriteLocationsTableView.getSelectionModel().getSelectedIndex();
        favoriteLocationsTableView.getItems().remove(selectedRowIndex);
    }

}
