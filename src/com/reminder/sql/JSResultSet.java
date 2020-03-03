package com.reminder.sql;

import java.sql.*;

public class JSResultSet implements AutoCloseable
{
    private ResultSet _rs;

    public JSResultSet(ResultSet rs)
    {
        _rs = rs;
    }

    public boolean next()
            throws SQLException
    {
        return _rs.next();
    }

    public int getInt(String columnLabel)
            throws SQLException
    {
        return _rs.getInt(columnLabel);
    }

    public int getIntByInt(int column)
            throws SQLException
    {
        return _rs.getInt(column);
    }

    public String getString(String columnLabel)
            throws SQLException
    {
        return _rs.getString(columnLabel);
    }

    public void close()
            throws SQLException
    {
        _rs.close();
    }
}