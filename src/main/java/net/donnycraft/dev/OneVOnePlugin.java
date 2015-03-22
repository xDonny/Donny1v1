
package net.donnycraft.dev;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Horse spawner plugin for Bukkit
 *
 * @author xDonny
 */
public class OneVOnePlugin extends JavaPlugin {

	public static int cullingTime = 30;
	
	boolean cullingThreadAlive = false;
	
	Thread challengeCuller;
	Runnable challengeCulling = new Runnable(){
		public void run()
		{
			while(cullingThreadAlive)
			{
				getLogger().info("Culling Challenge Requests...");
				BattleHandler.clearList();
				try {
					Thread.sleep(cullingTime * 1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
    @Override
    public void onDisable() {
    	cullingThreadAlive = false;
    	challengeCuller.stop();
        getLogger().info("Goodbye world!");
    }


    private final PlayerListener playerListener = new PlayerListener(this);
    
    @Override
    public void onEnable() {

    	
        // Register our commands
        getCommand("1v1").setExecutor(new CommandHandler());
        

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerListener, this);
        
        //start culling challenge requests
        challengeCuller = new Thread(challengeCulling);
        cullingThreadAlive = true;
        challengeCuller.start();
        
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

  
}
