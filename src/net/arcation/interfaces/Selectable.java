package net.arcation.interfaces;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Mr_Little_Kitty on 11/15/2016.
 */
public interface Selectable
{
    boolean shouldCallbackAsync();

    void setParameters(PreparedStatement statement);
    String getQuery();

    void receiveResult(ResultSet set);

    void callBack();
}
