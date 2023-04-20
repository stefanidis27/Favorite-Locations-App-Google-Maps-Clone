package com.arcgis.project.app.controller;

import com.arcgis.project.app.service.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private Button clickedLogInButton;
    @FXML
    private TextField insertedEmail;
    @FXML
    private PasswordField insertedPassword;
    @FXML
    private Label wrongLogInWarningLabel;

    public void tryLogIn(ActionEvent event) {
        LoginService.logIn(insertedEmail, insertedPassword, wrongLogInWarningLabel);
    }

}
