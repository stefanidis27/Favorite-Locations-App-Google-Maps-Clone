package com.arcgis.project.app;

import com.arcgis.project.app.controller.UserMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
public class Main extends Application {
    private static Stage mainStage;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        stage.setResizable(false);
        stage.setTitle("Favorite Locations App");
        stage.setWidth(900);
        stage.setHeight(640);
        stage.show();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent loginScene = null;
        try {
            loginScene = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert loginScene != null;
        Scene scene = new Scene(loginScene, stage.getMaxWidth(), stage.getMaxHeight());
        stage.setScene(scene);
    }

    public void changeScene(String fxmlName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
        mainStage.getScene().setRoot(loader.load());
    }

    @Override
    public void stop() {
        if (UserMenuController.mapView != null) {
            UserMenuController.mapView.dispose();
        }
    }

}
