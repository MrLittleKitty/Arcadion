package net.arcation.testlogger;

import net.arcation.arcadion.interfaces.Arcadion;
import net.arcation.arcadion.interfaces.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/16/2016.
 */
public class TestLogger extends JavaPlugin implements Listener
{
    public static final String BLOCK_BREAK_TABLE = "tbl_logs_block_breaks";

    private Arcadion arcadion;

    @Override
    public void onEnable()
    {
        arcadion = DatabaseManager.getArcadion();
        if(!arcadion.isActive())
        {
            this.getLogger().info("Could NOT start the logger");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        else
        {
            this.getLogger().info("Successfully started the logger");
            createTables();
            Bukkit.getPluginManager().registerEvents(this,this);
        }
    }

    @Override
    public void onDisable()
    {

    }

    private void createTables()
    {
        try(Connection connection = arcadion.getConnection())
        {
            String statementString = "CREATE TABLE IF NOT EXISTS "+BLOCK_BREAK_TABLE+" ("
                    + "`row_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                    + "`player_name` varchar(16) NOT NULL,"
                    + "`x` int(10) NOT NULL,"
                    + "`y` int(10) NOT NULL,"
                    + "`z` int(10) NOT NULL,"
                    + "`block_material` varchar(30) NOT NULL,"
                    + "PRIMARY KEY (`row_id`));";
            try(PreparedStatement statement = connection.prepareStatement(statementString))
            {
                statement.execute();
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event)
    {
        arcadion.insert(new BlockBreakInsert(event));
        this.getLogger().info("Logged block break event");
        arcadion.queueAsyncSelectable(new BlockSelect(event.getPlayer().getName(),event.getBlock().getX(),event.getBlock().getY(),event.getBlock().getZ()));
    }
}
