package net.arcation.arcadion.interfaces;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/15/2016.
 * Provides an Interface for something to be asynchronously inserted in a database
 */
public interface Insertable
{
    /**
     * Set the parameters for the statement returned from the getStatement() method
     * @param statement The PreparedStatement object to set the parameters for
     * @throws SQLException The SQLException that should be thrown by any of the PreparedStatement setParameter methods
     */
    void setParameters(PreparedStatement statement) throws SQLException;

    /**
     * Return the SQL command formatted for setting parameters using the '?' character
     * @return The SQL command that will be run
     */
    String getStatement();
}
