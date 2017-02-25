# Arcadion
A Minecraft Bukkit plugin for standardizing database communication between plugins running on a server.

### Releases

__**Minecraft 1.10**__

[Arcadion v1.0.3](https://github.com/MrLittleKitty/Arcadion/releases/tag/v1.0.3)

[Arcadion v1.0.1](https://github.com/MrLittleKitty/Arcadion/releases/tag/v1.0.1)

[Arcadion v1.0.0](https://github.com/MrLittleKitty/Arcadion/releases/tag/v1.0.0)


### Server Owners
1. Download the Arcadion.jar for the current release. Do NOT download the ArcadionAPI.jar.
2. Put the Arcadion.jar file into your server's /plugins folder.
3. Start the server once so that Arcadion creates a default configuration file in the /plugins/Arcadion folder.
4. Edit the configuration file with all the necessary information.
5. Start the server and watch the console. Arcadion will print to the console if it successfully connects to a database or not.

### Plugin Developers
1. Download the ArcadionAPI.jar for the current release. You will also need the Arcadion.jar if you want to run the plugin on a test server.
2. Reference the API jar from your preferred development environment.
3. Use the Arcadion API to develop your plugin.

### API Overview
Here are some tips, examples, and information to help you use the Arcadion API in your plugin.

All of the classes used in this example (and more) can be found in this repository under the example plugin ``TestLogger``.

#### Arcadion Interface
You should store an instance of the ``Arcadion`` interface from your onEnable() method like so:
```java
public class YourPlugin extends JavaPlugin
{
    private final Arcadion arcadionInstance;

    @Override
    public void onEnable()
    {
        arcadionInstance = DatabaseManager.getArcadion();
        
        if(!arcadionInstance.isActive())
        {
            this.getLogger().info("The database is NOT active.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}
```
#### Insertable Interface
One of the main uses of Arcadion is the ability to asynchronously insert data using the ``Insertable`` interface.
An example of how to implement the ``Insertable`` interface is given here:
```java
//This is a simple class to log a block action using the Insertable interface.
public class BlockActionInsert implements Insertable
{
    //Store all your data in local variables. They should all be immutable.
    //Try not to hold references to large objects as there is no guarantee when they will be released.
    
    private final String playerName, material, action;
    private final int x,y,z;
   
    public BlockActionInsert(String playerName, int x, int y, int z, String material, String action)
    {
        //Get all the data you plan to insert from the constructor and store it in variables.
        
       this.playerName = playerName;
       this.x = x;
       this.y = y;
       this.z = z;
       this.material = material;
       this.action = action;
    }

    @Override
    public void setParameters(PreparedStatement statement) throws SQLException
    {
        //Set all the parameters for your statement here. This method will NOT be called on the game thread.
        
        statement.setString(1, playerName);
        statement.setInt(2, X);
        statement.setInt(3, Y);
        statement.setInt(4, Z);
        statement.setString(5, material);
        statement.setString(6, action);
    }

    @Override
    public String getStatement()
    {
        //Return the raw SQL statement in prepared statement parameter form.
    
        return "INSERT INTO tbl_block_actions (player_name,x,y,z,block_material,action) VALUES (?,?,?,?,?,?);";
    }
}
```
Instances of the above class can then be enqueued for insertion using the ``Arcadion`` interface:
```java
//Asynchronously insert
arcadionInstance.queueAsyncInsertable(new BlockActionInsert("Mr_Little_Kitty",0,0,0,"DIRT","BREAK"));
arcadionInstance.queueAsyncInsertable(new BlockActionInsert("Mr_Little_Kitty",1,1,1,"DIRT","PLACE"));

//Synchronously insert (in a synchronous insert, all methods are called from the game thread)
arcadionInstance.insert(new BlockActionInsert("Mr_Little_Kitty",0,0,0,"DIRT","BREAK"));
arcadionInstance.insert(new BlockActionInsert("Mr_Little_Kitty",1,1,1,"DIRT","PLACE"));
```
#### Selectable Interface
Another main feature of Arcadion is the ability to execute asynchronous select statements with synchronous callbacks.
This is accomplished using the ``Selectable`` interface as demonstrated here:
```java
//This is a simple class to select all block actions at a location and print them to a specified player.
public class BlockSelect implements Selectable
{
    //Store all your data in local variables. They should all be immutable.
    //Try not to hold references to large objects as there is no guarantee when they will be released.

    private final String playerName;
    private final int x,y,z;

    public BlockSelect(String playerName, int x, int y, int z)
    {
        //Get all the data needed for the select statement from the constructor and store it in variables.
    
        this.playerName = playerName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean shouldCallbackAsync()
    {
        //Whether or not you want the callBack() method to be on a seperate thread.
        //In most cases you want this to be false so that callBack() runs on the game thread.
    
        return false;
    }

    @Override
    public void setParameters(PreparedStatement statement) throws SQLException
    {
        //Set all the parameters for your statement here. This method will NOT be called on the game thread.
    
        statement.setInt(1,x);
        statement.setInt(2,y);
        statement.setInt(3,z);
    }

    @Override
    public String getQuery()
    {
        //Return the raw SQL statement in prepared statement parameter form.
    
        return "SELECT player_name, x, y, z, block_material FROM tbl_block_actions WHERE x=? AND y=? AND z=?;";
    }

    //Generally you will want to keep a local variable for storing data to use in the callback() method.
    
    private ArrayList<String> resultStrings;

    @Override
    public void receiveResult(ResultSet set) throws SQLException
    {
        //This method will receive the results of the select statement.
        //This method will NOT be run on the game thread.
        //Generally you should read from the result set and store the returned data in local variables
        // as quickly as possible to minimize computation and the time that the result set is open.
        
        //You should NOT close the result set when you are done. That is taken care of later.
    
        resultStrings = new ArrayList<>();
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
            resultStrings.add(builder.toString());
            builder.delete(0,builder.length());
        }
    }

    @Override
    public void callBack()
    {
        //Depending on what you return from the shouldCallbackAsync() method,
        // this method can be called from the game thread or a seperate thread.
        
        //This is the method where you should use the results from receiveResult() and act upon them.
    
        Player player = Bukkit.getPlayer(playerName);
        for(String message : resultStrings)
            player.sendMessage(message);
    }
}
```
Instances of the above class can then be enqueued using the ``Arcadion`` interface:
```java
//Asynchronously select
arcadionInstance.queueAsyncSelectable(new BlockSelect("Mr_Little_Kitty",0,0,0));
arcadionInstance.queueAsyncSelectable(new BlockSelect("Mr_Little_Kitty",1,1,1));

//Synchronously select (in a synchronous select, all methods are called from the game thread)
arcadionInstance.select(new BlockSelect("Mr_Little_Kitty",0,0,0));
arcadionInstance.select(new BlockSelect("Mr_Little_Kitty",1,1,1));
```