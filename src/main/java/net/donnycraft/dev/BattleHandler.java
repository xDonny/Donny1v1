package net.donnycraft.dev;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BattleHandler {
	
	public static ArrayList<ChallengeInstance> challengingPlayers = new ArrayList<ChallengeInstance>();
	public static ArrayList<Player> playersInList = new ArrayList<Player>();
	
	public static void startBattle(ChallengeInstance players)
	{
		playersInList.add(players.opponent);
		playersInList.add(players.challenger);
		players.setBattling(true);
		final ChallengeInstance inThread = players;
		Runnable battleTimer = new Runnable(){
			public void run()
			{

				boolean battling = false;
				
				int count = 0;
				while(!battling)
				{
					if (count - 3 == 0)
					{
						battling = true;
						break;
					}
					inThread.sendMessageBoth("Battle starting in "+ (3 - count) + " seconds!");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					count++;
				}
				
				//make us both in PVP
				inThread.opponent.setGameMode(GameMode.SURVIVAL);
				inThread.challenger.setGameMode(GameMode.SURVIVAL);
				
				
				//full health
				inThread.opponent.setHealth(20.0);
				inThread.challenger.setHealth(20.0);
				
				//don't allow anybody to fly
				inThread.opponent.setAllowFlight(false);
				inThread.challenger.setAllowFlight(false);
				
				
				inThread.sendMessageBoth("Battle!");
				
			}
		};
		new Thread(battleTimer).start();
		
		
	}
	
	public static void checkChallenge(Player playerTwo)
	{
		Iterator<ChallengeInstance> it = challengingPlayers.iterator();
		while (it.hasNext())
		{
			ChallengeInstance ci = it.next();
			if (ci.opponent.getName().toLowerCase().equals(playerTwo.getName().toLowerCase()))
			{
				startBattle(ci);
				break;
			}
		}

		playerTwo.sendMessage("You don't have any challengers.");
	}
	
	public static void challengeSent(Player playerOne, Player playerTwo)
	{
		boolean alreadyChallenged = false;
		
		Iterator<ChallengeInstance> it = challengingPlayers.iterator();
		while (it.hasNext())
		{
			if (it.next().opponent == playerTwo || it.next().opponent == playerOne
					|| it.next().challenger == playerTwo || it.next().challenger == playerOne)
			{
				alreadyChallenged = true;
			}
		}
		if (!alreadyChallenged)
		{

			playerTwo.sendMessage(playerOne.getName() + " would like to battle you!");
			playerTwo.sendMessage("Use /1v1 accept to battle, /1v1 refuse or ignore for " + 
					OneVOnePlugin.cullingTime + " seconds to refuse the battle.");
			
			challengingPlayers.add(new ChallengeInstance(playerOne,playerTwo,System.currentTimeMillis()));
			
			playerOne.sendMessage("Battle request sent to " + playerTwo.getName());
		}
		else
		{
			playerOne.sendMessage(playerTwo.getName() + " already has a challenger, please try again shortly.");
		}
			
	}
	
	public static void clearList()
	{
		Iterator<ChallengeInstance> it = challengingPlayers.iterator();
		while (it.hasNext())
		{
			try
			{
				if ((System.currentTimeMillis() - it.next().challengeTime) >= OneVOnePlugin.cullingTime * 1000L
						&& !it.next().inBattle)
				{
					it.remove();
				}
			}
			catch (Exception e)
			{
			}
		}
	}

	public static void rejectChallenge(Player player1) {
		Iterator<ChallengeInstance> it = challengingPlayers.iterator();
		ChallengeInstance flag = null;
		while (it.hasNext())
		{
			ChallengeInstance challenge = it.next();
			if (challenge.opponent.getName().toLowerCase().equals(player1.getName().toLowerCase()) 
					|| challenge.challenger.getName().toLowerCase().equals(player1.getName().toLowerCase()))
			{
				flag = challenge;
			}
		}
		BattleHandler.challengingPlayers.remove(flag);
	}

}
