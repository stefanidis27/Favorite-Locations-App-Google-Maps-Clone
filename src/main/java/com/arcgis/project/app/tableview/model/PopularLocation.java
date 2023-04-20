package com.arcgis.project.app.tableview.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PopularLocation {

    private final DoubleProperty popularLatitude;
    private final DoubleProperty popularLongitude;
    private final LongProperty popularNumberOfUsers;
    private final StringProperty popularSelectedByUser;

    public PopularLocation(
            Double popularLatitude,
            Double popularLongitude,
            Long popularNumberOfUsers,
            String popularSelectedByUser
    ) {
        this.popularLatitude = new SimpleDoubleProperty(popularLatitude);
        this.popularLongitude = new SimpleDoubleProperty(popularLongitude);
        this.popularNumberOfUsers = new SimpleLongProperty(popularNumberOfUsers);
        this.popularSelectedByUser = new SimpleStringProperty(popularSelectedByUser);
    }

    public double getPopularLatitude() {
        return popularLatitude.get();
    }

    public DoubleProperty popularLatitudeProperty() {
        return popularLatitude;
    }

    public void setPopularLatitude(double popularLatitude) {
        this.popularLatitude.set(popularLatitude);
    }

    public double getPopularLongitude() {
        return popularLongitude.get();
    }

    public DoubleProperty popularLongitudeProperty() {
        return popularLongitude;
    }

    public void setPopularLongitude(double popularLongitude) {
        this.popularLongitude.set(popularLongitude);
    }

    public long getPopularNumberOfUsers() {
        return popularNumberOfUsers.get();
    }

    public LongProperty popularNumberOfUsersProperty() {
        return popularNumberOfUsers;
    }

    public void setPopularNumberOfUsers(long popularNumberOfUsers) {
        this.popularNumberOfUsers.set(popularNumberOfUsers);
    }

    public String getPopularSelectedByUser() {
        return popularSelectedByUser.get();
    }

    public StringProperty popularSelectedByUserProperty() {
        return popularSelectedByUser;
    }

    public void setPopularSelectedByUser(String popularSelectedByUser) {
        this.popularSelectedByUser.set(popularSelectedByUser);
    }

}
