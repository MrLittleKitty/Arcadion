package net.arcation;

import net.arcation.api.DatabaseConnection;
import net.arcation.api.InsertLayout;
import net.arcation.api.Insertable;
import net.arcation.api.PreparedInsertRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/8/2016.
 */
public class MySQLDatabaseConnection implements DatabaseConnection
{
    private Connection databaseConnection;
    public MySQLDatabaseConnection(Connection databaseConnection)
    {
        this.databaseConnection = databaseConnection;
    }


    @Override
    public <T> PreparedInsertRecord<T> prepareInsertRecord(InsertLayout<T> layout)
    {
        return null;
    }

    @Override
    public <T extends Insertable> PreparedInsertRecord<T> prepareInsertRecord(Class<T> classItem)
    {
        return null;
    }


    private class LayedOutInsertRecord<T> implements PreparedInsertRecord<T>
    {
        private final InsertLayout<T> layout;
        private final PreparedStatement statement;

        public LayedOutInsertRecord(InsertLayout<T> layout, PreparedStatement statement)
        {
            this.layout = layout;
            this.statement = statement;
        }

        @Override
        public void asyncInsert(T t)
        {

        }

        @Override
        public void syncInsert(T t)
        {
            try
            {
                layout.prepareForInsert(statement,t);
                statement.execute();
            }
            catch(SQLException e)
            {

            }
        }
    }
}
