package net.arcation.arcadion.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Mr_Little_Kitty on 11/15/2016.
 */
public interface Arcadion
{
    boolean isActive();

    void queueAsyncInsertable(Insertable insertable);

    boolean insert(Insertable insertable);

    void queueAsyncSelectable(Selectable selectable);

    boolean select(Selectable selectable);

    Connection getConnection() throws SQLException;
}
