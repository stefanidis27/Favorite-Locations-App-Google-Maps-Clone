package com.arcgis.project.app.service;

import com.arcgis.project.app.dao.LocationDao;
import com.arcgis.project.app.tableview.model.FavoriteLocation;
import com.arcgis.project.app.tableview.model.PopularLocation;
import com.esri.arcgisruntime.geometry.Point;
import javafx.collections.ObservableList;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LocationService {

    public static void getAllPopularLocations(ObservableList<PopularLocation> popularLocationsLocations, long userID) {
        LocationDao.getAllLocationsPopular(popularLocationsLocations, userID);
    }

    public static void showAllPopularLocationsForArcGIS() {
        List<Point> popularLocationsLocations = new ArrayList<>();
        LocationDao.getAllLocationsPopularForArcGIS(popularLocationsLocations);
        ArcGISService.displayPopularLocations(popularLocationsLocations);
    }

    public static void deleteUserLocationByLocationID(long locationID) {
        LocationDao.deleteLocationByID(locationID);
    }

    public static void getUserLocationsByUserID(ObservableList<FavoriteLocation> favoriteLocations, long userID) {
        LocationDao.getAllLocationsByUserID(favoriteLocations, userID);
    }

    public static void getAllUserLocationsForArcGIS(List<Point> favoriteLocationsPoints, long userID) {
        LocationDao.getAllLocationsByUserIDForArcgis(favoriteLocationsPoints, userID);
    }

    public static void createNewFavoriteLocation(FavoriteLocation newFavoriteLocation) {
        String timestampString = newFavoriteLocation.getDate() + " " + newFavoriteLocation.getTime();
        Timestamp timestamp = Timestamp.valueOf(timestampString);

        LocationDao.saveLocation(
            newFavoriteLocation.getId(),
            newFavoriteLocation.getTitle(),
            newFavoriteLocation.getShortDescription(),
            newFavoriteLocation.getLatitude(),
            newFavoriteLocation.getLongitude(),
            timestamp
        );
    }

    public static long generateNextLocationID() {
        long nextLocationID = 1L;
        List<Long> locationIDs = LocationDao.getAllLocationsIDs();
        if (locationIDs.isEmpty()) {
            return nextLocationID;
        }

        for (int i = 0; i < locationIDs.size() - 1; i++) {
            if (locationIDs.get(i + 1) - locationIDs.get(i) != 1) {
                return locationIDs.get(i) + 1;
            }
        }

        return locationIDs.size() + 1;
    }

}
