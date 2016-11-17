package net.arcation.testlogger;

import net.arcation.arcadion.interfaces.Insertable;
import org.bukkit.event.block.BlockBreakEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/16/2016.
 */
public class BlockBreakInsert implements Insertable
{
    private final String playerName,blockMaterial;
    private final int x,y,z;

    public BlockBreakInsert(BlockBreakEvent event)
    {
        playerName = event.getPlayer().getName();
        blockMaterial = event.getBlock().getType().name();
        x = event.getBlock().getX();
        y = event.getBlock().getY();
        z = event.getBlock().getZ();
    }

    @Override
    public void setParameters(PreparedStatement statement)
    {
        try
        {
            statement.setString(1,playerName);
            statement.setInt(2,x);
            statement.setInt(3,y);
            statement.setInt(4,z);
            statement.setString(5,blockMaterial);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getStatement()
    {
        return "INSERT INTO "+TestLogger.BLOCK_BREAK_TABLE+" (player_name,x,y,z,block_material) VALUES (?,?,?,?,?);";
    }
}
