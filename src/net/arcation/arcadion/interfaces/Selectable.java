package net.arcation.arcadion.interfaces;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/15/2016.
 */
public interface Selectable
{
    boolean shouldCallbackAsync();

    void setParameters(PreparedStatement statement) throws SQLException;
    String getQuery();

    void receiveResult(ResultSet set);

    void callBack();
}
