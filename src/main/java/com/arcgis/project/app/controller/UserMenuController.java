package com.arcgis.project.app.controller;

import com.arcgis.project.app.service.AdminService;
import com.arcgis.project.app.service.ArcGISService;
import com.arcgis.project.app.service.LocationService;
import com.arcgis.project.app.service.LoginService;
import com.arcgis.project.app.service.UserService;
import com.arcgis.project.app.tableview.model.FavoriteLocation;
import com.arcgis.project.app.tableview.model.PopularLocation;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class UserMenuController implements Initializable {

    public static MapView mapView;
    public static long userID;

    @FXML
    private Label appInfoLabel;
    @FXML
    private Button clickedAddFavoriteLocationButton;
    @FXML
    private Button clickedDeleteFavoriteLocationButton;
    @FXML
    private Button clickedUpdatePasswordButton;
    @FXML
    private Button clickedLogOutButton;
    @FXML
    private Button clickedSearchButton;
    @FXML
    private Button clickedUpdateEmailButton;
    @FXML
    private Label currentEmailLabel;
    @FXML
    private TableColumn<FavoriteLocation, String> dateColumn;
    @FXML
    private TableView<FavoriteLocation> favoriteLocationsTableView;
    @FXML
    private TextField insertedNewEmail;
    @FXML
    private TextField insertedPOIKeyword;
    @FXML
    private TextField insertedShortDescriptionOfFavoriteLocation;
    @FXML
    private TextField insertedTitleOfFavoriteLocation;
    @FXML
    private TableColumn<FavoriteLocation, Double> latitudeColumn;
    @FXML
    private TableColumn<FavoriteLocation, Double> longitudeColumn;
    @FXML
    private Label numberOfFavoriteLocationsLabel;
    @FXML
    private TableColumn<FavoriteLocation, String> shortDescriptionColumn;
    @FXML
    private StackPane stackPane;
    @FXML
    private TableColumn<FavoriteLocation, String> timeColumn;
    @FXML
    private TableColumn<FavoriteLocation, String> titleColumn;
    @FXML
    private TableColumn<FavoriteLocation, Long> idColumn;
    @FXML
    private TextField insertedNewPassword;
    @FXML
    private TableColumn<PopularLocation, Double> popularLatitudeColumn;
    @FXML
    private TableView<PopularLocation> popularLocationsTableView;
    @FXML
    private TableColumn<PopularLocation, Double> popularLongitudeColumn;
    @FXML
    private TableColumn<PopularLocation, Long> popularNumberOfUsersColumn;
    @FXML
    private TableColumn<PopularLocation, String> popularSelectedByMeColumn;

    public void tryAddFavoriteLocation(ActionEvent event) {
        if (!insertedTitleOfFavoriteLocation.getText().isEmpty()) {
            UserService.addUserNewLocation(
                    userID,
                    insertedTitleOfFavoriteLocation,
                    insertedShortDescriptionOfFavoriteLocation,
                    numberOfFavoriteLocationsLabel,
                    favoriteLocationsTableView
            );
        }
        UserService.showUserFavoriteLocations(userID);
        LocationService.showAllPopularLocationsForArcGIS();
        refreshPopularLocationsTableView();
    }

    public void tryDeleteFavoriteLocation(ActionEvent event) {
        FavoriteLocation selectedFavoriteLocation = favoriteLocationsTableView.getSelectionModel().getSelectedItem();
        if(selectedFavoriteLocation != null) {
            UserService.deleteUserLocation(
                    userID,
                    selectedFavoriteLocation,
                    numberOfFavoriteLocationsLabel,
                    favoriteLocationsTableView
            );
        }
        UserService.showUserFavoriteLocations(userID);
        LocationService.showAllPopularLocationsForArcGIS();
        refreshPopularLocationsTableView();
    }

    public void trySearchByPOIKeywordByName(ActionEvent event) {
        UserService.searchByPOIKeywordByName(insertedPOIKeyword);
    }

    public void tryUpdateEmail(ActionEvent event) {
        if (!insertedNewEmail.getText().isEmpty()) {
            UserService.updateUserEmail(
                    userID,
                    insertedNewEmail.getText(),
                    currentEmailLabel,
                    insertedNewEmail
            );
        }
        insertedNewEmail.setText("");
    }

    public void tryUpdatePassword(ActionEvent event) {
        if (!insertedNewPassword.getText().isEmpty()) {
            UserService.updateUserPassword(userID, insertedNewPassword.getText());
        }
        insertedNewPassword.setText("");
    }

    public void tryLogOut(ActionEvent event) {
        LoginService.logOut();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentEmailLabel.setText(UserService.getCurrentUserEmail(userID));
        numberOfFavoriteLocationsLabel.setText(UserService.getNumberOfFavoriteLocations(userID).toString());
        appInfoLabel.setText(AdminService.getAppInfo());
        initializeFavoriteLocationsTableView();
        initializePopularLocationsTableView();
        initializeArcGISMap();
    }

    private void initializeArcGISMap() {
        String yourApiKey = "AAPKa171d2ad4b0a43529bcfeaefe470803cvBaztE6IlzkNc7xbm_SOIThGemQDOzdTrE8PEJAonCYghEnDFWiH-ijkCCPinXT8";
        ArcGISRuntimeEnvironment.setApiKey(yourApiKey);

        mapView = new MapView();
        stackPane.getChildren().add(mapView);
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);
        ArcGISService.setFeatureLayers(map);
        mapView.setMap(map);
        mapView.setViewpoint(new Viewpoint(
                ArcGISService.initialLocation.getY(),
                ArcGISService.initialLocation.getX(),
                ArcGISService.initialScale
        ));
        ArcGISService.setGraphicLayers(mapView);
        LocationService.showAllPopularLocationsForArcGIS();
        UserService.showUserFavoriteLocations(userID);
        ArcGISService.performGlobalSearch(mapView, stackPane);
        ArcGISService.performRouting(mapView, stackPane);
    }

    private void initializeFavoriteLocationsTableView() {
        ObservableList<FavoriteLocation> favoriteLocations = FXCollections.observableArrayList();
        UserService.getAllUserLocations(favoriteLocations, userID);

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        shortDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("shortDescription"));
        latitudeColumn.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longitudeColumn.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        favoriteLocationsTableView.setItems(favoriteLocations);
    }

    private void initializePopularLocationsTableView() {
        ObservableList<PopularLocation> popularLocationsLocations = FXCollections.observableArrayList();
        LocationService.getAllPopularLocations(popularLocationsLocations, userID);

        popularLatitudeColumn.setCellValueFactory(new PropertyValueFactory<>("popularLatitude"));
        popularLongitudeColumn.setCellValueFactory(new PropertyValueFactory<>("popularLongitude"));
        popularNumberOfUsersColumn.setCellValueFactory(new PropertyValueFactory<>("popularNumberOfUsers"));
        popularSelectedByMeColumn.setCellValueFactory(new PropertyValueFactory<>("popularSelectedByUser"));

        popularLocationsTableView.setItems(popularLocationsLocations);
    }

    private void refreshPopularLocationsTableView() {
        ObservableList<PopularLocation> popularLocationsLocations = FXCollections.observableArrayList();
        LocationService.getAllPopularLocations(popularLocationsLocations, userID);
        popularLocationsTableView.setItems(popularLocationsLocations);
    }

}
