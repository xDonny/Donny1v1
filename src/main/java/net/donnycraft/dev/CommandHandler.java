package net.donnycraft.dev;


import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String arg2,
			String[] args) {
		
		if ((sender instanceof Player) == false)
		{
			sender.sendMessage("1v1 can only be started by Players");
			return true;
		}
		
		//initiate players for 1v1
		Player player1 = (Player) sender;
		Player player2 = null;
	
		//make sure we have at least 1 arg to start game
		if (args.length >= 1)
		{	
			
			if (args[0].toLowerCase().equals("accept"))
			{
				//player1 is actually player2 in this case
				BattleHandler.checkChallenge(player1);
			}
			if (args[0].toLowerCase().equals("refuse"))
			{
				//player1 is actually player2 in this case
				BattleHandler.rejectChallenge(player1);
				
			}
			else 
			{
				//we need to check for the player
				List<Player> playerList = player1.getWorld().getPlayers();
				Iterator<Player> currentPosition = playerList.iterator();
				//gotta check'em all
				while (currentPosition.hasNext())
				{
					//temp player for each iteration
					Player playerSearch = (Player) currentPosition.next();
					//check if the other player exists (and isn't you, you cheeky bastard)
					if (playerSearch.getName().toLowerCase().equals(args[0].toLowerCase()))
							//&& !playerSearch.getName().toLowerCase().equals(player1.getName().toLowerCase()))
					{
						//success!
						player2 = playerSearch;
					}
				}
				
				//if we have a player 2, obviously we can send a request to them.
				if (player2 != null)
				{	
					BattleHandler.challengeSent(player1, player2);
				}
				else
				{
					player1.sendMessage("Could not find " + args[0]);
				}
			}
		}
		
		
		return true;
	}

}
