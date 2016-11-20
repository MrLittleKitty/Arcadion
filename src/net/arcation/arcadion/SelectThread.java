package net.arcation.arcadion;

import net.arcation.arcadion.interfaces.Selectable;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*
Created by Mr_Little_Kitty on 5/7/2015
*/
class SelectThread extends Thread
{
    private Arcadion arcadion;

    public SelectThread(Arcadion arcadion,ThreadGroup group)
    {
        super(group,"Arcadion Select Thread");
        this.arcadion = arcadion;
    }

    @Override
    public void run()
    {
        while(this.isInterrupted())
        {
            Selectable next = null;
            try
            {
                next = arcadion.getSelectableQueue().take();
            }
            catch (InterruptedException ex)
            {
                arcadion.getLogger().info("ERROR Select Thread interrupted: " + ex.getMessage());
                break;
            }

            executeSelectable(next);
        }

        ArrayList<Selectable> finalToExecute = new ArrayList<>();
        arcadion.getSelectableQueue().drainTo(finalToExecute);
        for(Selectable s : finalToExecute)
            executeSelectable(s);
    }

    private void executeSelectable(Selectable selectable)
    {
        if(selectable != null)
        {
            try (Connection connection = arcadion.getDataSource().getConnection())
            {
                try (PreparedStatement statement = connection.prepareStatement(selectable.getQuery()))
                {
                    selectable.setParameters(statement);
                    try
                    {
                        try(ResultSet set = statement.executeQuery())
                        {
                            selectable.receiveResult(set);
                        }

                        if (selectable.shouldCallbackAsync())
                            selectable.callBack();
                        else
                            Bukkit.getScheduler().scheduleSyncDelayedTask(arcadion, new SyncRun(selectable));
                    }
                    catch (SQLException ex)
                    {
                        arcadion.getLogger().info("ERROR Executing query statement: " + ex.getMessage());
                        return;
                    }
                }
                catch (SQLException ex)
                {
                    arcadion.getLogger().info("ERROR Preparing query statement: " + ex.getMessage());
                    return;
                } //Try with resources closes the statement when its over
            } //Try with resources closes the connection when its over
            catch (SQLException ex)
            {
                arcadion.getLogger().info("ERROR Acquiring query connection: " + ex.getMessage());
                return;
            }
        }
    }

    private class SyncRun implements Runnable
    {
        private Selectable record;
        public SyncRun(Selectable record)
        {
            this.record = record;
        }

        @Override
        public void run()
        {
            record.callBack();
        }
    }
}


