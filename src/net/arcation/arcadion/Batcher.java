package net.arcation.arcadion;

import net.arcation.arcadion.interfaces.BatchLayout;
import net.arcation.arcadion.interfaces.InsertBatcher;
import net.arcation.arcadion.util.Action;
import net.arcation.arcadion.util.Provider;
import org.bukkit.Bukkit;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/18/2016.
 */
class Batcher<T> extends InsertBatcher<T>
{
    private Arcadion arcadion;
    private BatchLayout<T> layout;

    private PreparedStatement statement;

    public Batcher(Arcadion arcadion, BatchLayout<T> layout) throws SQLException
    {
        this.arcadion = arcadion;
        this.layout = layout;

        statement = arcadion.getConnection().prepareStatement(layout.getStatement());
    }

    @Override
    public void addToBatch(T item)
    {
        try
        {
            layout.setParameters(statement,item);
            statement.addBatch();
        }
        catch (SQLException e)
        {
            arcadion.getLogger().info("ERROR while setting parameters for batch: "+e.getMessage());
        }
    }

    @Override
    public void insertBatch()
    {
        try
        {
            //Create a temp variable for holding the statement we will pass into an async thread
            PreparedStatement temp = statement;

            //Set the current statement to be a new one (other statement will be run/closed)
            statement = arcadion.getConnection().prepareStatement(layout.getStatement());

            //Excute the batch insert synchronously (on the game thread)
            new BatchInsert(temp).act();
        }
        catch (SQLException e)
        {
            arcadion.getLogger().info("ERROR inserting sync batch: " + e.getMessage());
        }
    }

    @Override
    public void insertBatchAsync()
    {
        try
        {
            //Create a temp variable for holding the statement we will pass into an async thread
            PreparedStatement temp = statement;

            //Set the current statement to be a new one (other statement will be run/closed)
            statement = arcadion.getConnection().prepareStatement(layout.getStatement());

            //Pass the old statement into an async thread so it can be run/closed
            arcadion.queueAsyncInsertable(new BatchInsert(temp));
        }
        catch (SQLException e)
        {
            arcadion.getLogger().info("ERROR inserting async batch: " + e.getMessage());
        }
    }

    @Override
    public void close()
    {
        try
        {
            //Close the statement and its underlying connection
            Connection conn = statement.getConnection();
            statement.close();
            conn.close();
        }
        catch (SQLException e)
        {
            arcadion.getLogger().info("ERROR closing batcher: "+e.getMessage());
        }
    }

    private static class BatchInsert implements Action
    {
        private PreparedStatement statement;

        public BatchInsert(PreparedStatement statement)
        {
            this.statement = statement;
        }

        @Override
        public void act()
        {
            if(statement != null)
            {
                try
                {
                    statement.executeBatch();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    Connection conn = null;
                    try
                    {
                        conn = statement.getConnection();
                        statement.close();
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }

                    if(conn != null)
                    {
                        try
                        {
                            conn.close();
                        }
                        catch (SQLException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
