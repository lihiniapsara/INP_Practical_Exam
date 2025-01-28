package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/Server.fxml"))));
            stage.setTitle("Server Form");
            stage.centerOnScreen();
            stage.show();

            Stage stage1 = new Stage();

            stage1.setScene(new Scene(FXMLLoader.load(getClass().getResource("/Client.fxml"))));
            stage1.setTitle("Client Form");
            stage1.centerOnScreen();
            stage1.show();

            Stage stage2 = new Stage();

            stage2.setScene(new Scene(FXMLLoader.load(getClass().getResource("/Client1.fxml"))));
            stage2.setTitle("Client Form");
            stage2.centerOnScreen();
            stage2.show();


        }catch (Exception e){
            e.printStackTrace();
        }

    }
}




