package net.arcation.arcadion;

import net.arcation.arcadion.interfaces.Insertable;

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
            Insertable next = null;
            try
            {
                next = arcadion.getInsertableQueue().take();
            }
            catch (InterruptedException ex)
            {
                arcadion.getLogger().info("INFO Insert Thread interrupted: " + ex.getMessage());
                break;
            }

            executeInsertable(next);
        }

        ArrayList<Insertable> finalToExecute = new ArrayList<>();
        arcadion.getInsertableQueue().drainTo(finalToExecute);

        for(Insertable i : finalToExecute)
            executeInsertable(i);
    }

    private void executeInsertable(Insertable insertable)
    {
        if(insertable != null)
        {
            try (Connection connection = arcadion.getDataSource().getConnection())
            {
                try (PreparedStatement statement = connection.prepareStatement(insertable.getStatement()))
                {
                    insertable.setParameters(statement);
                    try
                    {
                        statement.execute();
                    }
                    catch (SQLException ex)
                    {
                        arcadion.getLogger().info("ERROR Executing statement: " + ex.getMessage());
                        return;
                    }
                }
                catch (SQLException ex)
                {
                    arcadion.getLogger().info("ERROR Preparing statement: " + ex.getMessage());
                    return;
                } //Try with resources closes the statement when its over
            } //Try with resources closes the connection when its over
            catch (SQLException ex)
            {
                arcadion.getLogger().info("ERROR Acquiring connection: " + ex.getMessage());
                return;
            }
        }
    }
}
