package net.arcation.arcadion.interfaces;

import java.io.Closeable;

/**
 * Created by Mr_Little_Kitty on 11/18/2016.
 * Provides an interface for allowing the inserting of items into the database in a batch
 */
public abstract class InsertBatcher<T> implements AutoCloseable
{
    /**
     * Adds the provided item to the batch to be inserted
     * @param item The item that will be added to the insert batch
     */
    public abstract void addToBatch(T item);

    /**
     * Runs this batch insert synchronously (on the game thread)
     */
    public abstract void insertBatch();

    /**
     * Runs this batch insert asynchronously (NOT on the game thread)
     */
    public abstract void insertBatchAsync();
}
