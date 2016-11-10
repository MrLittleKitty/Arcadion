package net.arcation.api;

import net.arcation.util.Receiver;

import java.sql.ResultSet;

/**
 * Created by Mr_Little_Kitty on 11/10/2016.
 */
public interface SelectRecord
{
    ResultSet select(SelectLayout layout);

    void asyncSelect(SelectLayout layout, Receiver<ResultSet> callback);
}
