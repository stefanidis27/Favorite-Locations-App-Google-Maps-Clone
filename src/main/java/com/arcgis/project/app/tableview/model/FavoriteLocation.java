package com.arcgis.project.app.tableview.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FavoriteLocation {

    private final StringProperty title;
    private final StringProperty shortDescription;
    private final DoubleProperty latitude;
    private final DoubleProperty longitude;
    private final StringProperty date;
    private final StringProperty time;
    private final LongProperty id;

    public FavoriteLocation(
            String title,
            String shortDescription,
            Double latitude,
            Double longitude,
            String date,
            String time,
            Long id
    ) {
        this.title = new SimpleStringProperty(title);
        this.shortDescription = new SimpleStringProperty(shortDescription);
        this.latitude = new SimpleDoubleProperty(latitude);
        this.longitude = new SimpleDoubleProperty(longitude);
        this.date = new SimpleStringProperty(date);
        this.time = new SimpleStringProperty(time);
        this.id = new SimpleLongProperty(id);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getShortDescription() {
        return shortDescription.get();
    }

    public StringProperty shortDescriptionProperty() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription.set(shortDescription);
    }

    public double getLatitude() {
        return latitude.get();
    }

    public DoubleProperty latitudeProperty() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude.set(latitude);
    }

    public double getLongitude() {
        return longitude.get();
    }

    public DoubleProperty longitudeProperty() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude.set(longitude);
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
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
