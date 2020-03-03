package com.reminder;

import javafx.scene.control.Button;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public @Data class License {


    public String contactName, contactEmail, licenseName, serialNumber, clientName, htmlText;
    private LocalDate renewalDate;
    private int daysRemaining;
    private String displayedDate;
    public String emailText;

    public License(String serialNumber, String contactName, String contactEmail, String licenseName, String clientName, LocalDate renewalDate, String displayedDate, int daysRemaining) {
        this.contactName = contactName;
        this.serialNumber = serialNumber;
        this.clientName = clientName;
        this.contactEmail = contactEmail;
        this.licenseName = licenseName;
        this.renewalDate = renewalDate;
        this.displayedDate = displayedDate;
        this.daysRemaining = daysRemaining;
    }
}
