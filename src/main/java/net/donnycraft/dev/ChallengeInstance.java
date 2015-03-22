package net.donnycraft.dev;

import org.bukkit.entity.Player;

public class ChallengeInstance 
{
	
	Player challenger;
	Player opponent;
	long challengeTime = 0;
	boolean inBattle = false;

	ChallengeInstance(Player playerOne, Player playerTwo, long time)
	{
		this.challenger = playerOne;
		this.opponent = playerTwo;
		this.challengeTime = time;
	}
	
	public void sendMessageBoth(String message)
	{
		challenger.sendMessage(message);
		opponent.sendMessage(message);
	}
	
	public void setBattling(boolean battling)
	{
		this.inBattle = battling;
	}
	
}
