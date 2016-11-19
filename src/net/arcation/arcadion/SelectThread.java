package net.arcation.arcadion;

import net.arcation.arcadion.interfaces.Selectable;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
Created by Mr_Little_Kitty on 5/7/2015
*/
class SelectThread extends Thread implements DisableableThread
{
    private Arcadion arcadion;
    private boolean enabled;

    public SelectThread(Arcadion arcadion)
    {
        this.arcadion = arcadion;
        enabled = true;

    }

    @Override
    public void run()
    {
        while(enabled)
        {
            Selectable next = null;
            try
            {
                next = arcadion.getSelectableQueue().take();
            }
            catch (InterruptedException ex)
            {
                arcadion.getLogger().info("ERROR Thread interrupted getting Selectable: " + ex.getMessage());
                continue;
            }

            if(next != null)
            {
                try (Connection connection = arcadion.getDataSource().getConnection())
                {
                    try (PreparedStatement statement = connection.prepareStatement(next.getQuery()))
                    {
                        next.setParameters(statement);
                        try
                        {
                            ResultSet set = statement.executeQuery();

                            next.receiveResult(set);
                            set.close();

                            if(next.shouldCallbackAsync())
                                next.callBack();
                            else
                                Bukkit.getScheduler().scheduleSyncDelayedTask(arcadion,new SyncRun(next));
                        }
                        catch (SQLException ex)
                        {
                            arcadion.getLogger().info("ERROR Executing query statement: " + ex.getMessage());
                            continue;
                        }
                    }
                    catch (SQLException ex)
                    {
                        arcadion.getLogger().info("ERROR Preparing query statement: " + ex.getMessage());
                        continue;
                    } //Try with resources closes the statement when its over
                } //Try with resources closes the connection when its over
                catch (SQLException ex)
                {
                    arcadion.getLogger().info("ERROR Acquiring query connection: " + ex.getMessage());
                    continue;
                }
            }
        }
    }

    @Override
    public void disable()
    {
        this.enabled = false;
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


