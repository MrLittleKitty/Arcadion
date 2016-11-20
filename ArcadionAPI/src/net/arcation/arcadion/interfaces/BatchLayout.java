package net.arcation.arcadion.interfaces;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/18/2016.
 * Provides a layout for formatting a batch insert statement
 */
public interface BatchLayout<T>
{
    /**
     * Sets the parameters for the batch insert statement with the values of the given item
     * @param statement The PreparedStatement to set the values of
     * @param item The item to use to set the values of the PreparedStatement
     * @throws SQLException The SQLException that is thrown by the PreparedStatement's setParameter() methods
     */
    void setParameters(PreparedStatement statement, T item) throws SQLException;

    /**
     * Return the SQL command formatted for setting parameters using the '?' character
     * @return The SQL command that will be run
     */
    String getStatement();
}
