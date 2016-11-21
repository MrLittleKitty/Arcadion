package net.arcation.testlogger;

import net.arcation.arcadion.interfaces.BatchLayout;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/20/2016.
 */
public class ActionLayout implements BatchLayout<BlockAction>
{
    @Override
    public void setParameters(PreparedStatement statement, BlockAction item) throws SQLException
    {
        statement.setString(1,item.PlayerName);
        statement.setInt(2,item.X);
        statement.setInt(3,item.Y);
        statement.setInt(4,item.Z);
        statement.setString(5,item.BlockMaterial);
        statement.setString(6,item.Action);
    }

    @Override
    public String getStatement()
    {
        return "INSERT INTO "+TestLogger.BLOCK_BREAK_TABLE+" (player_name,x,y,z,block_material,action) VALUES (?,?,?,?,?,?);";
    }
}
