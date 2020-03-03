package com.reminder;

import com.reminder.sql.Database;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static java.time.temporal.ChronoUnit.DAYS;

public class LicenseController implements Initializable {

    @FXML Button licenseButton;

    @FXML DatePicker datePicker;

    @FXML ComboBox txtContactName;

    @FXML AnchorPane topPanel;

    @FXML TextField txtLicenseName, txtSerialNumber, txtClientName;
    @FXML Text errorCode;

    @FXML
    private void addLicense() throws IOException {

        SwingUtilities.invokeLater(() -> {
            try {

                if (txtContactName.getValue() == null) {
                    errorCode.setText("You are missing important information areas marked in red are required!");
                    txtContactName.setStyle("-fx-border-color: red;");
                    return;
                }

                String contactName = txtContactName.getValue().toString();
                String licenseName = txtLicenseName.getText();
                String clientName = txtClientName.getText();
                String serialNumber = txtSerialNumber.getText();

                if (Database.checkLicense(licenseName)) {
                    errorCode.setText("The database cannot contain duplicate licenses change the license name!");
                    txtLicenseName.setStyle("-fx-border-color: red;");
                    return;
                }

                if (clientName.isEmpty()) {
                    errorCode.setText("You are missing important information areas marked in red are required!");
                    txtContactName.setStyle("-fx-border-color: #7289da;");
                    txtClientName.setStyle("-fx-border-color: red;");
                    return;
                }

                if (licenseName.isEmpty()) {
                    errorCode.setText("You are missing important information areas marked in red are required!");
                    txtClientName.setStyle("-fx-border-color: #7289da;");
                    txtLicenseName.setStyle("-fx-border-color: red;");
                    return;
                }

                String contactEmail = contactName.contains("Micheal") ? "Micheal.thompson@claratti.com" :
                                      contactName.contains("Vlad") ? "Vlad.nielsen@claratti.com" :
                                      contactName.contains("Selva") ? "selva.s@claratti.com" :
                                      contactName.contains("Christopher") ? "christophoer.louw@claratti.com" :
                                      contactName.contains("Kishore") ? "kishore.r@claratti.com" :
                                      contactName.contains("Anand") ? "anand.c@claratti.com" :
                                      contactName.contains("NetOps") ? "Netops@claratti.com" : "micheal.thompson@claratti.com";

                LocalDate date = datePicker.getValue();

                if (date == null) {
                    errorCode.setText("You are missing important information areas marked in red are required!");
                    datePicker.setStyle("-fx-border-color: red;");
                    txtLicenseName.setStyle("-fx-border-color: #7289da;");
                    return;
                }

                int daysRemaining = (int) DAYS.between(date.now(), date);

                System.out.println(daysRemaining);

                String displayedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                datePicker.setStyle("-fx-border-color: #7289da;");

                try (PreparedStatement stmt = Database.db.prepareStatement("INSERT INTO licenses(serialNumber, contactName, contactEmail, licenseName, clientName, date, displayedDate, daysRemaining)" +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?)")) {
                    stmt.setString(1, serialNumber);
                    stmt.setString(2, contactName);
                    stmt.setString(3, contactEmail);
                    stmt.setString(4, licenseName);
                    stmt.setString(5, clientName);
                    stmt.setDate(6, Date.valueOf(date));
                    stmt.setString(7, displayedDate);
                    stmt.setLong(8, -1);

                    stmt.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                openDashboard();

            } catch (Exception e) {

            }
        });
    }

    @FXML
    private void closeWindow() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void minimizeWindow() {
        Stage stage = (Stage) topPanel.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML private void openDashboard() throws IOException { Main.setRoot("Dashboard");}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setPromptText("Date");
        licenseButton.setStyle("-fx-background-color: #373e48;");

        txtContactName.getItems().addAll(
                "NetOps",
                "Micheal Thompson",
                "Vlad Nielsen",
                "Anand Chinnaswamy",
                "Selvakumar Sengotaiyan",
                "Kishore Ramasamy",
                "Christopher Louw"
        );
    }
}
