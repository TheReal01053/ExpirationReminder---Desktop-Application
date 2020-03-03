package com.reminder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.ResourceBundle;
import com.reminder.sql.Database;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.swing.*;

import static java.time.temporal.ChronoUnit.DAYS;

public class DashboardController implements Initializable {

    @FXML Button dashButton;
    private static Path htmlDoc = Paths.get("\\\\clar_fs01\\GroupDrive\\Apps\\ExpirationReminder\\", "index.html");

    @FXML public TableView<License> tblIcon;
    @FXML public TableColumn<License, String> tblLicenseSerial, tblLicenseName, tblContactName, tblContactEmail, tblExpiryDate, tblClientName;
    @FXML public TableColumn<License, License> tblDeleteColumn;
    @FXML public DatePicker datePicker;

    @FXML public AnchorPane topPanel;

    private static DashboardController instance;

    public DashboardController() {
        instance = this;
    }

    public static DashboardController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        loadData();
    }

    public void initTable() {
        initCols();
    }

    public void initCols() {
        dashButton.setStyle("-fx-background-color: #373e48;");
        tblLicenseSerial.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        tblLicenseName.setCellValueFactory(new PropertyValueFactory<>("licenseName"));
        tblContactName.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        tblContactEmail.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));
        tblExpiryDate.setCellValueFactory(new PropertyValueFactory<>("displayedDate"));
        tblClientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));

        tblLicenseSerial.setPrefWidth(125);
        tblLicenseName.setPrefWidth(125);
        tblContactName.setPrefWidth(125);
        tblContactEmail.setPrefWidth(150);
        tblExpiryDate.setPrefWidth(80);
        tblClientName.setPrefWidth(115);

        TableColumn<License, License> removeColumn = new TableColumn<>("");
        removeColumn.setPrefWidth(90);
        removeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        removeColumn.setCellFactory(param -> new TableCell<License, License>() {
            private final Button deleteButton = new Button("Delete");

            @Override
            protected void updateItem(License license, boolean empty) {
                super.updateItem(license, empty);
                deleteButton.setId("deleteButton");

                if (license == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(deleteButton);
                deleteButton.setOnAction(event -> {
                    Main.getLicenses().remove(license);
                    try {
                        Database.deleteLicense(license.getLicenseName());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        tblIcon.getColumns().add(removeColumn);

    }

    private LocalDate date;

    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            tblIcon.getItems().clear();
            Main.getLicenses().clear();

            try {
                Database.fetchLicenses();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            dashButton.setStyle("-fx-background-color: #373e48;");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(htmlDoc.toString()));
                for (License item : Main.getLicenses()) {
                    int days = (int) DAYS.between(date.now(), item.getRenewalDate());
                    String renewal = item.getRenewalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    while ((item.emailText = reader.readLine()) != null) {
                        item.htmlText += item.emailText.replace("_CONTACT_", item.getContactName()).
                                replace("_SERIAL_", item.getSerialNumber()).
                                replace("_CLIENT_", item.getClientName()).
                                replace("_LICENSE_", item.getLicenseName()).replace("_DATE_", renewal).
                                replace("_DAYS_", Integer.toString(1)) + "\n";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ObservableList<License> sortedList = Main.getLicenses().sorted(Comparator.comparingInt(License::getDaysRemaining));
            tblIcon.setItems(sortedList);
        });
    }


    @FXML private void openLicense() throws IOException { Main.setRoot("License");}

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
}
