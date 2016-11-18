package net.arcation.arcadion.interfaces;

/**
 * Created by Mr_Little_Kitty on 11/15/2016.
 */
public class DatabaseManager
{
    /**
     * Returns THE instance of the Arcadion database manager
     * @return The instance of the Arcadion database manager
     */
    public static Arcadion getArcadion()
    {
        return net.arcation.arcadion.Arcadion.instance;
    }
}
