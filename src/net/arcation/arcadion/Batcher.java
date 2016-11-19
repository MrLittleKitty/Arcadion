package net.arcation.arcadion;

import net.arcation.arcadion.interfaces.BatchLayout;
import net.arcation.arcadion.interfaces.InsertBatcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/18/2016.
 */
class Batcher<T> implements InsertBatcher<T>
{
    private int batchSize;

    private int currentInBatch;

    private Arcadion arcadion;
    private BatchLayout<T> layout;

    private PreparedStatement statement;

    public Batcher(Arcadion arcadion, BatchLayout<T> layout, int startingBatchSize) throws SQLException
    {
        this.arcadion = arcadion;
        this.layout = layout;
        batchSize = startingBatchSize;
        currentInBatch = 0;

        try(Connection connection = arcadion.getConnection())
        {
            statement = connection.prepareStatement(layout.getStatement());
        } //Try with resources closes the connection when its done
    }

    @Override
    public void setBatchSize(int newBatchSize)
    {
        if(currentInBatch >= newBatchSize)
            flushBatch();
        batchSize = newBatchSize;
    }

    @Override
    public int getBatchSize()
    {
        return batchSize;
    }

    @Override
    public boolean addToBatch(T item)
    {
        layout.setParameters(statement,item);
        try
        {
            statement.addBatch();
            currentInBatch++;

            if(currentInBatch >= batchSize)
                flushBatch();
        }
        catch (SQLException e)
        {
            arcadion.getLogger().info("ERROR adding statement to batch: "+e.getMessage());
            return false;
        }
        return true;
    }

    private void flushBatch()
    {
        currentInBatch = 0;
    }
}
