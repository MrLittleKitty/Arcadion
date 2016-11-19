package net.arcation.arcadion.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/15/2016.
 * Represents the interface for communicating with the Database
 */
public interface Arcadion
{
    /**
     * Determines if the Database connection is active
     * @return True if the plugin is active
     */
    boolean isActive();

    /**
     * Queues an Insertable for being run on an asynchronous thread
     * @param insertable The Insertable to queue
     */
    void queueAsyncInsertable(Insertable insertable);

    /**
     * Synchronously runs the provided Insertable on the calling thread
     * @param insertable The Insertable to run
     * @return True if the Insertable ran without error
     */
    boolean insert(Insertable insertable);

    /**
     * Queues a Selectable for being run on an asynchronous thread
     * @param selectable The Selectable to queue
     */
    void queueAsyncSelectable(Selectable selectable);

    /**
     * Synchronously runs the provided Selectable on the calling thread
     * @param selectable The Selectable to run
     * @return True if the Selectable ran without error
     */
    boolean select(Selectable selectable);

    /**
     * Provides a method for executing an arbitrary SQL command
     * @param command The raw SQL command to be executed
     * @return True if the command executed with error.
     */
    boolean executeCommand(String command);

    /**
     * Gets a connection from the connection pool for running SQL commands
     * @return A Connection object for running SQL commands
     * @throws SQLException A SQLException that can occur from the connection pool
     */
    Connection getConnection() throws SQLException;
}
