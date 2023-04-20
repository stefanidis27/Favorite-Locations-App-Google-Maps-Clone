package com.arcgis.project.app.controller;

import com.arcgis.project.app.service.AdminService;
import com.arcgis.project.app.service.LoginService;
import com.arcgis.project.app.service.UserService;
import com.arcgis.project.app.tableview.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {
    public static long userID;

    @FXML
    private Label totalNumberOfUsersLabel;
    @FXML
    private CheckBox adminStatusCheckBox;
    @FXML
    private Label wrongNewEmailLabel;
    @FXML
    private TableColumn<User, String> adminStatusColumn;
    @FXML
    private Button clickedCreatedUserButton;
    @FXML
    private Button clickedDeleteUserButton;
    @FXML
    private Button clickedLogOutButtonAdmin;
    @FXML
    private Button clickedUpdateAppInfoButton;
    @FXML
    private Button clickedUpdateEmailButtonAdmin;
    @FXML
    private Button clickedUpdatePasswordButtonAdmin;
    @FXML
    private Button clickedUpdateUserPasswordButton;
    @FXML
    private Label currentEmailLabelAdmin;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TextField insertedAppInfo;
    @FXML
    private TextField insertedNewEmailAdmin;
    @FXML
    private TextField insertedNewPasswordAdmin;
    @FXML
    private TextField insertedNewPasswordForSelectedUser;
    @FXML
    private TextField insertedNewUserEmail;
    @FXML
    private TextField insertedNewUserPassword;
    @FXML
    private TableColumn<User, Long> numberOfFavoriteLocationsColumn;
    @FXML
    private TableColumn<User, Long> userIdColumn;
    @FXML
    private TableView<User> usersTableView;

    public void tryCreateUser(ActionEvent event) {
        if (!insertedNewUserEmail.getText().isEmpty() && !insertedNewUserPassword.getText().isEmpty()) {
            AdminService.createNewUser(
                    insertedNewUserEmail,
                    insertedNewUserPassword,
                    adminStatusCheckBox,
                    totalNumberOfUsersLabel,
                    usersTableView,
                    wrongNewEmailLabel
            );
        }
    }

    public void tryDeleteUser(ActionEvent event) {
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null && selectedUser.getId() != userID) {
            AdminService.deleteSelectedUser(
                    selectedUser,
                    totalNumberOfUsersLabel,
                    usersTableView
            );
        }
    }

    public void tryLogOutAdmin(ActionEvent event) {
        LoginService.logOut();
    }

    public void tryUpdateAppInfo(ActionEvent event) {
        AdminService.updateAppInfo(insertedAppInfo);
    }

    public void tryUpdateEmailAdmin(ActionEvent event) {
        if (!insertedNewEmailAdmin.getText().isEmpty()) {
            UserService.updateUserEmail(
                    userID,
                    insertedNewEmailAdmin.getText(),
                    currentEmailLabelAdmin,
                    insertedNewEmailAdmin
            );
        }
        insertedNewEmailAdmin.setText("");
    }

    public void tryUpdatePasswordAdmin(ActionEvent event) {
        if (!insertedNewPasswordAdmin.getText().isEmpty()) {
            UserService.updateUserPassword(userID, insertedNewPasswordAdmin.getText());
        }
        insertedNewPasswordAdmin.setText("");
    }

    public void tryUpdateUserPassword(ActionEvent event) {
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null && !insertedNewPasswordForSelectedUser.getText().isEmpty()) {
            AdminService.changePasswordForSelectedUser(
                    selectedUser,
                    insertedNewPasswordForSelectedUser
            );
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentEmailLabelAdmin.setText(UserService.getCurrentUserEmail(userID));
        totalNumberOfUsersLabel.setText(AdminService.getTotalNumberOfUsers().toString());
        insertedAppInfo.setText(AdminService.getAppInfo());
        initializeUserTableView();
    }

    private void initializeUserTableView() {
        ObservableList<User> users = FXCollections.observableArrayList();
        AdminService.getAllUsersData(users);

        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        numberOfFavoriteLocationsColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfFavoriteLocations"));
        adminStatusColumn.setCellValueFactory(new PropertyValueFactory<>("adminStatus"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        usersTableView.setItems(users);
    }

}
