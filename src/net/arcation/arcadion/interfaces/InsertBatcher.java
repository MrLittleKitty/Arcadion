package net.arcation.arcadion.interfaces;

/**
 * Created by Mr_Little_Kitty on 11/18/2016.
 */
public interface InsertBatcher<T>
{
    void setBatchSize(int newBatchSize);

    int getBatchSize();

    boolean addToBatch(T item);
}
