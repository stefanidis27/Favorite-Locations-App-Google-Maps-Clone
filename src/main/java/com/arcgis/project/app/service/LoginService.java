package com.arcgis.project.app.service;

import com.arcgis.project.app.Main;
import com.arcgis.project.app.controller.AdminMenuController;
import com.arcgis.project.app.controller.UserMenuController;
import com.arcgis.project.app.dao.UserDao;
import com.arcgis.project.app.utils.Utils;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginService {
    private static Long ID;

    public static void logIn(TextField insertedEmail, TextField insertedPassword, Label wrongLogInWarningLabel) {
        Main main = new Main();

        if(insertedEmail.getText().isEmpty() || insertedPassword.getText().isEmpty()) {
            wrongLogInWarningLabel.setText("");
        } else {
            if (!checkCredentials(
                    Utils.textWithoutTrailingWhiteSpaces(insertedEmail.getText()),
                    insertedPassword.getText()
            )) {
                wrongLogInWarningLabel.setText("Wrong username or password!");
            } else {
                wrongLogInWarningLabel.setText("");
                if (!checkIfUserIsAdmin(ID)) {
                    try {
                        main.changeScene("/fxml/userMenu.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        main.changeScene("/fxml/adminMenu.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void logOut() {
        Main main = new Main();
        try {
            if (UserMenuController.mapView != null) {
                UserMenuController.mapView.getGraphicsOverlays().clear();
                UserMenuController.mapView.dispose();
            }
            main.changeScene("/fxml/login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkCredentials(String email, String password) {
        long userID = UserDao.getUserIDByEmailAndPassword(email, password);
        boolean checkCredentialsResult = false;
        if (userID != -1) {
            if (checkIfUserIsAdmin(userID)) {
                AdminMenuController.userID = userID;
            } else {
                UserMenuController.userID = userID;
            }
            ID = userID;
            checkCredentialsResult = true;
        }

        return checkCredentialsResult;
    }

    private static boolean checkIfUserIsAdmin(long userID) {
        return UserDao.getUserAdminStatusByID(userID);
    }

}
