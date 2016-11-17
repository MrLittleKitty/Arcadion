package net.arcation.testlogger;

import net.arcation.arcadion.interfaces.Selectable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Mr_Little_Kitty on 11/16/2016.
 */
public class BlockSelect implements Selectable
{
    private final String playerName;
    private final int x,y,z;

    public BlockSelect(String playerName, int x, int y, int z)
    {
        this.playerName = playerName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean shouldCallbackAsync()
    {
        return false;
    }

    @Override
    public void setParameters(PreparedStatement statement)
    {
        try
        {
            statement.setInt(1,x);
            statement.setInt(2,y);
            statement.setInt(3,z);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getQuery()
    {
        return "SELECT player_name, x, y, z, block_material FROM "+TestLogger.BLOCK_BREAK_TABLE+" WHERE x=? AND y=? AND z=?;";
    }

    private ArrayList<String> strings;

    @Override
    public void receiveResult(ResultSet set)
    {
        strings = new ArrayList<>();
        try
        {
            StringBuilder builder = new StringBuilder();
            while(set.next())
            {
                builder.append(set.getString(1))
                        .append(" broke ")
                        .append(set.getString(5))
                        .append(" at ")
                        .append(set.getInt(2))
                        .append(' ')
                        .append(set.getInt(3))
                        .append(' ')
                        .append(set.getInt(4));
                strings.add(builder.toString());
                builder.delete(0,builder.length());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void callBack()
    {
        Player player = Bukkit.getPlayer(playerName);
        for(String message : strings)
            player.sendMessage(message);
    }
}
