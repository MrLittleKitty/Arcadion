package net.arcation.arcadion;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import net.arcation.arcadion.interfaces.BatchLayout;
import net.arcation.arcadion.interfaces.InsertBatcher;
import net.arcation.arcadion.interfaces.Insertable;
import net.arcation.arcadion.interfaces.Selectable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Created by Mr_Little_Kitty on 11/7/2016.
 */
public class Arcadion extends JavaPlugin implements net.arcation.arcadion.interfaces.Arcadion
{
    public static net.arcation.arcadion.interfaces.Arcadion instance;

    private int shutdownState = 0;

    private List<Thread> threads;
    private HikariDataSource dataSource;

    private LinkedTransferQueue<Insertable> asyncInsertables;
    private LinkedTransferQueue<Selectable> asyncSelectables;

    private static String HOST_PATH = "database.host";
    private static String PORT_PATH = "database.port";
    private static String DATABASE_PATH = "database.database";
    private static String USERNAME_PATH = "database.username";
    private static String PASSWORD_PATH = "database.password";

    private static String MAX_CONNECTIONS_PATH = "settings.maxConnections";
    private static String SELECT_THREADS_PATH = "settings.selectThreads";
    private static String INSERT_THREADS_PATH = "settings.insertThreads";

    @Override
    public void onEnable()
    {
        instance = this;

        addDefaults();

        FileConfiguration pluginConfig = getConfig();
        String hostname = pluginConfig.getString(HOST_PATH);
        int port = pluginConfig.getInt(PORT_PATH);
        String databaseName = pluginConfig.getString(DATABASE_PATH);
        String user = pluginConfig.getString(USERNAME_PATH);
        String pass = pluginConfig.getString(PASSWORD_PATH);

        int maxConnections = pluginConfig.getInt(MAX_CONNECTIONS_PATH);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + databaseName);
        config.setUsername(user);
        config.setPassword(pass);
        config.setMaximumPoolSize(maxConnections);

        asyncInsertables = new LinkedTransferQueue<>();
        asyncSelectables = new LinkedTransferQueue<>();

        try
        {
            dataSource = new HikariDataSource(config);
        }
        catch(HikariPool.PoolInitializationException e)
        {
            dataSource = null;
            e.printStackTrace();
        }

        if(isActive())
        {
            this.getLogger().info("Successfully connected to the database server!");
            threads = new ArrayList<>();

            int insertThreads = pluginConfig.getInt(INSERT_THREADS_PATH);
            int selectThreads = pluginConfig.getInt(SELECT_THREADS_PATH);

            if(selectThreads <= 0)
                selectThreads = 1;
            if(insertThreads <= 0)
                insertThreads = 1;

            for(int i = 0; i < selectThreads; i++)
            {
                SelectThread t = new SelectThread(this);
                threads.add(t);
                t.start();
                this.getLogger().info("Created Select Thread #" + (i + 1));
            }
            for(int i = 0; i < insertThreads; i++)
            {
                InsertThread t = new InsertThread(this);
                threads.add(t);
                t.start();
                this.getLogger().info("Created Insert Thread #" + (i + 1));
            }
        }
        else
            this.getLogger().info("ERROR Could not connect to the database server!");
    }

    @Override
    public void onDisable()
    {
        shutdownState++;

        for(Thread t : threads)
            t.interrupt();
    }

    private void addDefaults()
    {
        FileConfiguration config = getConfig();

        config.addDefault(HOST_PATH,"127.0.0.1");
        config.addDefault(PORT_PATH, 3380);
        config.addDefault(DATABASE_PATH,"civex");
        config.addDefault(USERNAME_PATH,"root");
        config.addDefault(PASSWORD_PATH,"pass");

        config.addDefault(MAX_CONNECTIONS_PATH, 6);
        config.addDefault(SELECT_THREADS_PATH, 1);
        config.addDefault(INSERT_THREADS_PATH, 1);

        config.options().copyDefaults(true);

        this.saveConfig();
    }

    public boolean isActive()
    {
        return dataSource != null && !dataSource.isClosed() && shutdownState == 0;
    }

    public void queueAsyncInsertable(Insertable insertable)
    {
        if(insertable != null && shutdownState == 0)
            asyncInsertables.offer(insertable);
    }

    public boolean insert(Insertable insertable)
    {
        if(shutdownState == 0)
        {
            try (Connection connection = dataSource.getConnection())
            {
                try (PreparedStatement statement = connection.prepareStatement(insertable.getStatement()))
                {
                    insertable.setParameters(statement);
                    try
                    {
                        statement.execute();
                    }
                    catch (SQLException ex)
                    {
                        getLogger().info("ERROR Executing statement: " + ex.getMessage());
                        return false;
                    }
                }
                catch (SQLException ex)
                {
                    getLogger().info("ERROR Preparing statement: " + ex.getMessage());
                    return false;
                } //Try with resources closes the statement when its over
            } //Try with resources closes the connection when its over
            catch (SQLException ex)
            {
                getLogger().info("ERROR Acquiring connection: " + ex.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    public void queueAsyncSelectable(Selectable selectable)
    {
        if(selectable != null && shutdownState == 0)
            asyncSelectables.offer(selectable);
    }

    public boolean select(Selectable selectable)
    {
        if(shutdownState == 0)
        {
            try (Connection connection = dataSource.getConnection())
            {
                try (PreparedStatement statement = connection.prepareStatement(selectable.getQuery()))
                {
                    selectable.setParameters(statement);
                    try
                    {
                        ResultSet set = statement.executeQuery();

                        selectable.receiveResult(set);
                        set.close();

                        selectable.callBack();
                    }
                    catch (SQLException ex)
                    {
                        getLogger().info("ERROR Executing query statement: " + ex.getMessage());
                        return false;
                    }
                }
                catch (SQLException ex)
                {
                    getLogger().info("ERROR Preparing query statement: " + ex.getMessage());
                    return false;
                } //Try with resources closes the statement when its over
            } //Try with resources closes the connection when its over
            catch (SQLException ex)
            {
                getLogger().info("ERROR Acquiring query connection: " + ex.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    LinkedTransferQueue<Insertable> getInsertableQueue()
    {
        return asyncInsertables;
    }

    LinkedTransferQueue<Selectable> getSelectableQueue()
    {
        return asyncSelectables;
    }

    HikariDataSource getDataSource()
    {
        return dataSource;
    }

    synchronized void shutDownCallback()
    {
        //Increment the shutdown state if it was started from onDisable()
        //If this method is called and shutdownState == 0, then a thread failed and it isn't shutting down
        if(shutdownState > 0)
            shutdownState++;

        //Once onDisable() is called, shutdownState immediately goes to 1
        //Then, every thread increments it when that thread is done.
        //So, if it equals the number of threads + 1, all the threads have shut down
        if(shutdownState == threads.size()+1)
            if(dataSource != null && !dataSource.isClosed())
                dataSource.close();
    }

    public boolean executeCommand(String command)
    {
        if(shutdownState == 0)
        {
            try (Connection connection = dataSource.getConnection())
            {
                try (PreparedStatement statement = connection.prepareStatement(command))
                {
                    return statement.execute();
                }
                catch (SQLException ex)
                {
                    getLogger().info("ERROR Preparing command statement: " + ex.getMessage());
                    return false;
                }
            }
            catch (SQLException ex)
            {
                getLogger().info("ERROR Acquiring command connection: " + ex.getMessage());
                return false;
            }
        }
        return false;
    }

    public Connection getConnection() throws SQLException
    {
        if(!isActive() || shutdownState != 0)
            return null;
        return dataSource.getConnection();
    }

    public <T> InsertBatcher<T> prepareInsertBatcher(BatchLayout<T> layout, int startingBatchSize)
    {
        return null;
    }
}
