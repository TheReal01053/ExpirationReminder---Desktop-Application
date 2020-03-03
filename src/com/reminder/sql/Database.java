package com.reminder.sql;

import com.reminder.License;
import com.reminder.Main;

import java.sql.*;
import java.time.LocalDate;

public class Database implements AutoCloseable {

    public static Connection db;

    private static final String URL = "jdbc:sqlserver://clar_con2:1433;";

    public static void initDatabase(String user, String pass) throws SQLException {
        db = DriverManager.getConnection(URL, user, pass);

        execute("USE expirationreminder");

        System.out.println("Reading Database..");
    }

    public static void fetchLicenses() throws SQLException {
        try (PreparedStatement stmt = Database.db.prepareStatement("SELECT * FROM Licenses")) {
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String serialNumber = rs.getString("serialNumber");
                    String contactName = rs.getString("contactName");
                    String contactEmail = rs.getString("contactEmail");
                    String licenseName = rs.getString("licenseName");
                    String clientName = rs.getString("clientName");
                    Date date = rs.getDate("date");
                    String displayedDate = rs.getString("displayedDate");
                    int daysRemaining = rs.getInt("daysRemaining");

                    Main.licenses.add(new License(serialNumber, contactName, contactEmail, licenseName, clientName, date.toLocalDate(), displayedDate, daysRemaining));
                }
            }
        }
    }

    public static void UpdateDays(String key, int value) throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement("UPDATE licenses SET daysRemaining = ("+ value +") WHERE licenseName = '"+ key +"'")) {
            stmt.executeUpdate();
        }
    }

    public static boolean hasLicense;

    public static boolean checkLicense(String licenseName) throws SQLException {
         try (PreparedStatement stmt = db.prepareStatement("SELECT * FROM LICENSES WHERE licenseName = '" + licenseName +"'")) {
             try (ResultSet rs = stmt.executeQuery()) {
                 while (rs.next()) {
                     return true;
                     //hasLicense = true;
                 }
             }
         }
         return false;
    }

    public static void deleteLicense(String licenseName) throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement("DELETE FROM licenses WHERE licenseName = ?")) {
            System.out.println(licenseName + " deleted from DB");
            stmt.setString(1, licenseName);
            stmt.executeUpdate();
        }
    }

    @Override
    public void close() throws SQLException {
        if (db != null)
            db.close();
    }

    public String getString(ResultSet s, String b)
            throws SQLException
    {
        return s.getString(b);
    }

    public static int executeUpdate(String sql)
            throws SQLException
    {
        PreparedStatement s = DisposalManager.manage(db.prepareStatement(sql));
        return s.executeUpdate();
    }

    public JSResultSet executeQuery(String sql)
            throws SQLException
    {
        PreparedStatement s = DisposalManager.manage(db.prepareStatement(sql));
        return DisposalManager.manage(new JSResultSet(s.executeQuery()));
    }

    public static boolean execute(String sql)
            throws SQLException
    {
        PreparedStatement s = DisposalManager.manage(db.prepareStatement(sql));
        return s.execute();
    }
}
