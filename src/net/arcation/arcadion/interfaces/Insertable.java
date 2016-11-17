package net.arcation.arcadion.interfaces;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/15/2016.
 */
public interface Insertable
{
    void setParameters(PreparedStatement statement) throws SQLException;
    String getStatement();
}
