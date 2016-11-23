package net.arcation.testlogger;

import net.arcation.arcadion.interfaces.Arcadion;
import net.arcation.arcadion.interfaces.DatabaseManager;
import net.arcation.arcadion.interfaces.InsertBatcher;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Mr_Little_Kitty on 11/16/2016.
 */
public class TestLogger extends JavaPlugin implements Listener
{
    public static final String BLOCK_BREAK_TABLE = "tbl_block_log";

    private Arcadion arcadion;

    private ArrayList<BlockAction> actions = new ArrayList<>();

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
        for(BlockAction a : actions)
            arcadion.queueAsyncInsertable(new BlockBreakInsert(a));
    }

    private void createTables()
    {
        String statementString
                = "CREATE TABLE IF NOT EXISTS "+BLOCK_BREAK_TABLE+" ("
                + "`row_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`player_name` varchar(16) NOT NULL,"
                + "`x` int(10) NOT NULL,"
                + "`y` int(10) NOT NULL,"
                + "`z` int(10) NOT NULL,"
                + "`block_material` varchar(30) NOT NULL,"
                + "`action` varchar(30) NOT NULL,"
                + "PRIMARY KEY (`row_id`));";

        arcadion.executeCommand(statementString);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event)
    {
        this.getLogger().info("Logged place break event");

        BlockAction action = new BlockAction();
        action.Action = "Place";
        action.X = event.getBlock().getX();
        action.Y = event.getBlock().getY();
        action.Z = event.getBlock().getZ();
        action.PlayerName = event.getPlayer().getName();
        action.BlockMaterial = event.getBlock().getType().name();

        actions.add(action);
        checkBatch();
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event)
    {
        this.getLogger().info("Logged block break event");

        BlockAction action = new BlockAction();
        action.Action = "Break";
        action.X = event.getBlock().getX();
        action.Y = event.getBlock().getY();
        action.Z = event.getBlock().getZ();
        action.PlayerName = event.getPlayer().getName();
        action.BlockMaterial = event.getBlock().getType().name();

        actions.add(action);
        checkBatch();
    }

    private void checkBatch()
    {
        if(actions.size() >= 15)
        {
            try
            {
                try(InsertBatcher<BlockAction> batcher = arcadion.prepareInsertBatcher(new ActionLayout()))
                {
                    for (BlockAction a : actions)
                        batcher.addToBatch(a);
                    batcher.insertBatchAsync();
                }
                actions.clear();
                this.getLogger().info("Inserted Batch!");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

//    Testing normal insertable and selectable
//    @EventHandler
//    public void blockBreak(BlockBreakEvent event)
//    {
//        arcadion.insert(new BlockBreakInsert(event));
//        this.getLogger().info("Logged block break event");
//        arcadion.queueAsyncSelectable(new BlockSelect(event.getPlayer().getName(),event.getBlock().getX(),event.getBlock().getY(),event.getBlock().getZ()));
//    }
}
