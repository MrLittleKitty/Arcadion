package net.arcation.arcadion;

import net.arcation.arcadion.interfaces.BatchLayout;
import net.arcation.arcadion.interfaces.InsertBatcher;
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
            statement = statement.getConnection().prepareStatement(layout.getStatement());

            //Pass the old statement into an async thread so it can be run/closed
            Bukkit.getScheduler().runTaskAsynchronously(arcadion, new RunBatch(temp));
        }
        catch (SQLException e)
        {
            arcadion.getLogger().info("ERROR inserting batch: " + e.getMessage());
        }
    }

    @Override
    public void close() throws IOException
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

    private class RunBatch implements Runnable
    {
        private final PreparedStatement statement;

        public RunBatch(PreparedStatement statement)
        {
            this.statement = statement;
        }

        @Override
        public void run()
        {
            try
            {
                //Execute the batch (this runnable should NOT be running on the game thread)
                statement.executeBatch();

                //Close the statement and its underlying connection
                Connection conn = statement.getConnection();
                statement.close();
                conn.close();
            }
            catch (SQLException e)
            {
                arcadion.getLogger().info("ERROR on running async batch: "+e.getMessage());
            }
        }
    }
}
