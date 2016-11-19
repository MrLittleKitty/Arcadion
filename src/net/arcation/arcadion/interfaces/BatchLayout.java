package net.arcation.arcadion.interfaces;

import java.sql.PreparedStatement;

/**
 * Created by Mr_Little_Kitty on 11/18/2016.
 */
public interface BatchLayout<T>
{
    void setParameters(PreparedStatement statement, T item);

    String getStatement();
}
