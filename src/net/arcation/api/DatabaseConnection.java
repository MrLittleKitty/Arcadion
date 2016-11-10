package net.arcation.api;

/**
 * Created by Mr_Little_Kitty on 11/8/2016.
 */
public interface DatabaseConnection
{
    <T> PreparedInsertRecord<T> prepareInsertRecord(InsertLayout<T> layout);

    <T extends Insertable> PreparedInsertRecord<T> prepareInsertRecord(Class<T> classItem);
}
