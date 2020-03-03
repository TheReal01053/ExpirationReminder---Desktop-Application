package com.reminder.mail;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.activation.DataSource;
import javax.mail.Transport;
import javax.swing.*;

import com.reminder.License;
import com.reminder.Main;
import com.reminder.sql.Database;
import com.reminder.task.Expiry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static java.time.temporal.ChronoUnit.DAYS;


public class Send {

    public static ObservableList<License> getLicenses() {
        return Main.licenses;
    }

    private static LocalDate date;

    private static long messageRelay;

    public static void MailTask() {

        System.out.println("Executing mail task");
        messageRelay = System.currentTimeMillis();
        new Expiry().schedule(() -> {

            for (License node : getLicenses()) {
                try {
                    int days = (int) DAYS.between(date.now(), node.getRenewalDate());
                    switch (days) {
                        case 0:
                        case 1:
                        case 5:
                        case 7:
                        case 14:
                        case 30:
                        case 60:
                            if (node.getDaysRemaining() != days) {
                                final String username = "micheal.thompson@claratti.com";
                                final String password = "Ak4K3OL1";

                                Properties props = new Properties();
                                props.put("mail.smtp.auth", "true");
                                props.put("mail.smtp.starttls.enable", "true");
                                props.put("mail.smtp.host", "outlook.office365.com");
                                props.put("mail.smtp.port", "587");


                                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                                    protected PasswordAuthentication getPasswordAuthentication() {
                                        return new PasswordAuthentication(username, password);
                                    }
                                });
                                //message instance
                                Message message = new MimeMessage(session);
                                //set who the message is coming from
                                message.setFrom(new InternetAddress("micheal.thompson@claratti.com"));
                                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(node.getContactEmail()));

                                Date date = new Date();

                                message.setSubject("License Due to Expire in " + days + " days from " + date.toString().substring(0, 10));
                                message.setDataHandler(new DataHandler(new HTMLDataSource(node.getHtmlText().replace("null", ""))));

                                Transport.send(message);

                                node.setDaysRemaining(days);
                                Database.UpdateDays(node.getLicenseName(), days);

                                System.out.println("Email sent");

                                if (days == 0) {
                                    getLicenses().remove(node);
                                    Database.deleteLicense(node.getSerialNumber());
                                }

                                node.setHtmlText("");
                                node.setDaysRemaining(days);
                                Database.UpdateDays(node.getLicenseName(), days);
                            }
                            break;
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    public static void main(String[]args) throws IOException {
        MailTask();
    }

    static class HTMLDataSource implements DataSource {

        private String html;

        public HTMLDataSource(String htmlString) {
            html = htmlString;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (html == null) throw new IOException("html message is null");
            return new ByteArrayInputStream(html.getBytes());
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new IOException("This DataHandler cannot write HTML");
        }

        @Override
        public String getContentType() {
            return "text/html";
        }

        @Override
        public String getName() {
            return "HTMLDataSource";
        }
    }
}

