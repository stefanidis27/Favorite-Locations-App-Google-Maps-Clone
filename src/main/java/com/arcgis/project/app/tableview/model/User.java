package com.arcgis.project.app.tableview.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

    private final StringProperty email;
    private final LongProperty numberOfFavoriteLocations;
    private final StringProperty adminStatus;
    private final LongProperty id;

    public User(
            String email,
            Long numberOfFavoriteLocations,
            String adminStatus,
            Long id
    ) {
        this.email = new SimpleStringProperty(email);
        this.numberOfFavoriteLocations = new SimpleLongProperty(numberOfFavoriteLocations);
        this.adminStatus = new SimpleStringProperty(adminStatus);
        this.id = new SimpleLongProperty(id);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public long getNumberOfFavoriteLocations() {
        return numberOfFavoriteLocations.get();
    }

    public LongProperty numberOfFavoriteLocationsProperty() {
        return numberOfFavoriteLocations;
    }

    public void setNumberOfFavoriteLocations(long numberOfFavoriteLocations) {
        this.numberOfFavoriteLocations.set(numberOfFavoriteLocations);
    }

    public String getAdminStatus() {
        return adminStatus.get();
    }

    public StringProperty adminStatusProperty() {
        return adminStatus;
    }

    public void setAdminStatus(String adminStatus) {
        this.adminStatus.set(adminStatus);
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }
}
