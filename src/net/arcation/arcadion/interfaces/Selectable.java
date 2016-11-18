package net.arcation.arcadion.interfaces;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/15/2016.
 * Provides an interface for asynchronously selecting from a database
 */
public interface Selectable
{
    /**
     * Whether or not the method callback() will be called from another thread or the game thread
     * @return True to be called from an arbitrary thread, False to be called from the game thread
     */
    boolean shouldCallbackAsync();

    /**
     * Set the parameters for the SQL command returned by getQuery()
     * @param statement The PreparedStatement to set the parameters of
     * @throws SQLException The SQLException that can be thrown by setParameter() methods
     */
    void setParameters(PreparedStatement statement) throws SQLException;

    /**
     * Return the SQL command formatted for setting parameters using the '?' character
     * @return The SQL command that will be run
     */
    String getQuery();

    /**
     * Receives the ResultSet that is returned from executing the SQL command
     * This method is called from an arbitrary asynchronous thread. NOT the game thread
     * @param set The ResultSet returned from executing the SQL command
     */
    void receiveResult(ResultSet set);

    /**
     * The method that will be called once the query is executed and the result is received
     */
    void callBack();
}
