package com.reminder;

import com.reminder.mail.Send;
import com.reminder.sql.Database;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Micheal Thompson
 * License Managing Software
 * Started Development: 10/11/2019
 * Stable Version: 1.0
 */
public class Main extends Application {

    public static ObservableList<License> licenses = FXCollections.observableArrayList();
    public static ObservableList<License> getLicenses() {
        return licenses;
    }

    private static Scene scene;
    private static double xOffset, yOffset;

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        scene = new Scene(loadFXML("Dashboard"));
        Image icon = new Image(getClass().getResourceAsStream("icon.png"));
        stage.getIcons().add(icon);

        scene.getStylesheets().add(ClassLoader.getSystemResource("style.css").toExternalForm());

        scene.setOnMousePressed((event) -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        scene.setOnMouseDragged((event) -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        // Do not uncomment below unless you're creating a jar for the server side...
        Send.MailTask();

        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
    public void stop() throws Exception {
        scene.getX();
        super.stop();
    }

    private static LocalDate date;
    public static void main(String[] args) throws SQLException {
        Database.initDatabase("sqladmin", "Cl0uddcc!");
        launch();
    }
}