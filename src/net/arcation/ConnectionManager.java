package net.arcation;

import net.arcation.api.*;

/**
 * Created by Mr_Little_Kitty on 11/7/2016.
 */
public class ConnectionManager
{
    public ConnectionManager(String host, String port, String database, String username, String password)
    {

    }

    <T> PreparedInsertRecord<T> prepareInsertRecord(InsertLayout<T> layout, String statement)
    {
        return null;
    }

    SelectRecord prepareSelectRecord(String statement)
    {
        return null;
    }

//    <T extends Insertable> PreparedInsertRecord<T> prepareInsertRecord(Class<T> classItem)
//    {
//
//    }
}
