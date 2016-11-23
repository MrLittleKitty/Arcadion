package net.arcation.arcadion;

import net.arcation.arcadion.interfaces.Insertable;
import net.arcation.arcadion.util.Action;
import net.arcation.arcadion.util.Provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/*
Created by Mr_Little_Kitty on 5/7/2015
*/
class InsertThread extends Thread
{
    private Arcadion arcadion;

    public InsertThread(Arcadion arcadion, ThreadGroup group)
    {
        super(group,"Arcadion Insert Thread");
        this.arcadion = arcadion;
    }

    @Override
    public void run()
    {
        while (!this.isInterrupted())
        {
            Action next = null;
            try
            {
                next = arcadion.getInsertableQueue().take();
            }
            catch (InterruptedException ex)
            {
                arcadion.getLogger().info("WARN Insert Thread interrupted: " + ex.getMessage());
                break;
            }

            next.act();
        }

        ArrayList<Action> finalToExecute = new ArrayList<>();
        arcadion.getInsertableQueue().drainTo(finalToExecute);

        for(Action i : finalToExecute)
            i.act();
    }

//    private void executeInsert(Provider<PreparedStatement> provider)
//    {
//        PreparedStatement statement = provider.provide();
//        if(statement != null)
//        {
//            Connection conn = null;
//            try
//            {
//                conn = statement.getConnection();
//                statement.execute();
//            }
//            catch (SQLException e)
//            {
//                arcadion.getLogger().info("ERROR Executing statement: " + e.getMessage());
//            }
//            finally
//            {
//                try
//                {
//                    if (statement != null)
//                        statement.close();
//                }
//                catch (SQLException e)
//                {
//                    arcadion.getLogger().info("ERROR closing statement: " + e.getMessage());
//                }
//
//                try
//                {
//                    if (conn != null)
//                        conn.close();
//                }
//                catch (SQLException e)
//                {
//                    arcadion.getLogger().info("ERROR closing connection: " + e.getMessage());
//                }
//            }
//        }
//    }

//    private void executeInsertable(Insertable insertable)
//    {
//        if(insertable != null)
//        {
//            try (Connection connection = arcadion.getDataSource().getConnection())
//            {
//                try (PreparedStatement statement = connection.prepareStatement(insertable.getStatement()))
//                {
//                    insertable.setParameters(statement);
//                    try
//                    {
//                        statement.execute();
//                    }
//                    catch (SQLException ex)
//                    {
//                        arcadion.getLogger().info("ERROR Executing statement: " + ex.getMessage());
//                        return;
//                    }
//                }
//                catch (SQLException ex)
//                {
//                    arcadion.getLogger().info("ERROR Preparing statement: " + ex.getMessage());
//                    return;
//                } //Try with resources closes the statement when its over
//            } //Try with resources closes the connection when its over
//            catch (SQLException ex)
//            {
//                arcadion.getLogger().info("ERROR Acquiring connection: " + ex.getMessage());
//                return;
//            }
//        }
//    }
}
