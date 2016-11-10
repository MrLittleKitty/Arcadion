package net.arcation.api;

/**
 * Created by Mr_Little_Kitty on 11/8/2016.
 */
public interface PreparedInsertRecord<T>
{
    void asyncInsert(T t);

    void syncInsert(T t);
}
