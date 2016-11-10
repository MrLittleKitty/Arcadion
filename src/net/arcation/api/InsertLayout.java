package net.arcation.api;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/8/2016.
 */
public interface InsertLayout<T>
{
    String getStatement();

    void prepareForInsert(PreparedStatement statement, T item) throws SQLException;
}
