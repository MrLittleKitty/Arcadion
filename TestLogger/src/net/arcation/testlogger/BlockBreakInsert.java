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
    private BlockAction item;

    public BlockBreakInsert(BlockAction action)
    {
       this.item = action;
    }

    @Override
    public void setParameters(PreparedStatement statement)
    {
        try
        {
            statement.setString(1, item.PlayerName);
            statement.setInt(2, item.X);
            statement.setInt(3, item.Y);
            statement.setInt(4, item.Z);
            statement.setString(5, item.BlockMaterial);
            statement.setString(6, item.Action);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getStatement()
    {
        return "INSERT INTO "+TestLogger.BLOCK_BREAK_TABLE+" (player_name,x,y,z,block_material,action) VALUES (?,?,?,?,?,?);";
    }
}
