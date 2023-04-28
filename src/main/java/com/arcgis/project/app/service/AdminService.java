package com.arcgis.project.app.service;

import com.arcgis.project.app.dao.DescriptionDao;
import com.arcgis.project.app.dao.UserDao;
import com.arcgis.project.app.tableview.model.User;
import com.arcgis.project.app.utils.Utils;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class AdminService {

    public static Long getTotalNumberOfUsers() {
        return UserDao.getNumberOfUsers();
    }

    public static void getAllUsersData(ObservableList<User> users) {
        UserDao.getAllUsers(users);
    }

    public static void createNewUser(
            TextField insertedNewUserEmail,
            TextField insertedNewUserPassword,
            CheckBox adminStatusCheckBox,
            Label totalNumberOfUsersLabel,
            TableView<User> usersTableView,
            Label wrongEmailLabel
    ) {
        if (checkInvalidNewUserEmail(insertedNewUserEmail.getText())) {
            wrongEmailLabel.setText("A user with this email already exists!");
            return;
        }
        wrongEmailLabel.setText("");
        long nextUserID = generateNextUserID();
        UserDao.saveUser(
                nextUserID,
                Utils.textWithoutWhiteSpaces(insertedNewUserEmail.getText()),
                Utils.textWithoutWhiteSpaces(insertedNewUserPassword.getText()),
                adminStatusCheckBox.isSelected()
        );

        long updatedNumber = Long.parseLong(totalNumberOfUsersLabel.getText()) + 1;
        totalNumberOfUsersLabel.setText(Long.toString(updatedNumber));
        insertedNewUserPassword.setText("");
        usersTableView.getItems().add(new User(
                insertedNewUserEmail.getText(),
                0L,
                adminStatusCheckBox.isSelected() ? "*" : "",
                nextUserID
        ));
        insertedNewUserEmail.setText("");
    }

    private static boolean checkInvalidNewUserEmail(String email) {
        List<String> userEmails = UserDao.getAllUsersEmails();
        return userEmails.stream().anyMatch(userEmail -> userEmail.equals(
                Utils.textWithoutWhiteSpaces(email)
        ));
    }

    private static long generateNextUserID() {
        List<Long> userIDs = UserDao.getAllUsersIDs();
        if (userIDs.isEmpty()) {
            return 1L;
        }

        for (int i = 0; i < userIDs.size() - 1; i++) {
            if (userIDs.get(i + 1) - userIDs.get(i) != 1) {
                return userIDs.get(i) + 1;
            }
        }

        return userIDs.size() + 1;
    }

    public static String getAppInfo() {
        return DescriptionDao.getDescription();
    }

    public static void updateAppInfo(TextField insertedAppInfo) {
        DescriptionDao.updateDescription(insertedAppInfo.getText());
    }

    public static void changePasswordForSelectedUser(User selectedUser, TextField insertedNewPasswordForSelectedUser) {
        UserService.updateUserPassword(
                selectedUser.getId(),
                insertedNewPasswordForSelectedUser.getText()
        );
        insertedNewPasswordForSelectedUser.setText("");
    }

    public static void deleteSelectedUser(User selectedUser, Label userCount, TableView<User> usersTableView) {
        List<Long> userLocationsIds = UserService.getUserLocationIDs(selectedUser.getId());
        for (Long userLocationId : userLocationsIds) {
            LocationService.deleteUserLocationByLocationID(userLocationId);
        }
        UserDao.deleteUserByID(selectedUser.getId());

        long updatedNumber = Long.parseLong(userCount.getText()) - 1;
        userCount.setText(Long.toString(updatedNumber));

        int selectedRowIndex = usersTableView.getSelectionModel().getSelectedIndex();
        usersTableView.getItems().remove(selectedRowIndex);
    }

}
