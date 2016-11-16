package net.arcation.interfaces;

import java.sql.PreparedStatement;

/**
 * Created by Mr_Little_Kitty on 11/15/2016.
 */
public interface Insertable
{
    void setParameters(PreparedStatement statement);
    String getStatement();
}
