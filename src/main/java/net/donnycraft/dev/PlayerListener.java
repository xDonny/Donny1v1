package net.donnycraft.dev;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener{

	OneVOnePlugin pluginInstance = null;
	
	PlayerListener(OneVOnePlugin instance)
	{
		this.pluginInstance = instance; 
	}
	
	@EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {

		ChallengeInstance ci = null;
		//check if hit by player
		if (event.getDamager() instanceof Player)
		{
			Player p = (Player) event.getDamager();
			if (BattleHandler.playersInList.contains(p))
			{
				ci = findChallengeInstance(p);
			}
		}
		//check if arrow shot
		if (event.getDamager() instanceof Arrow)
		{
			if (((Arrow) event.getDamager()).getShooter() instanceof Player)
			{
				Player p = (Player) ((Arrow) event.getDamager()).getShooter();
				if (BattleHandler.playersInList.contains(p))
				{
					ci = findChallengeInstance(p);
				}
			}
		}
		//check if fireball shot
		if (event.getDamager() instanceof Fireball)
		{
			if (((Fireball) event.getDamager()).getShooter() instanceof Player)
			{
				Player p = (Player) ((Fireball) event.getDamager()).getShooter();
				if (BattleHandler.playersInList.contains(p))
				{
					ci = findChallengeInstance(p);
				}
			}
		}
		//check if challenge is found
		if (ci != null)
		{
			Player p = null;
			//couple cases for p to populate
			if (event.getDamager() instanceof Fireball)
				if (((Fireball)(event.getDamager())).getShooter() instanceof Player)
					p = (Player) ((Fireball) event.getDamager()).getShooter();
			if (event.getDamager() instanceof Arrow)
				if (((Arrow)(event.getDamager())).getShooter() instanceof Player)
					p = (Player) ((Arrow) event.getDamager()).getShooter();
			if (event.getDamager() instanceof Player)
				p = (Player) event.getDamager();
			//check if p has been found
			if (p != null)
			{
				//check if I'm in a battle
				if (ci.inBattle && (ci.opponent.getName().toLowerCase().equals(p.getName().toLowerCase())
						|| ci.challenger.getName().toLowerCase().equals(p.getName().toLowerCase())))
				{
					//check if it's me
					if (event.getEntity() instanceof Player)
					{
						//check if gonna die
						if (((Player)event.getEntity()).getHealth() - event.getFinalDamage() < 0.5)
						{
							Bukkit.broadcastMessage(p.getName() + " has defeated " + event.getEntity().getName() + " in a 1v1");
							//clean up
							BattleHandler.playersInList.remove(ci.opponent);
							BattleHandler.playersInList.remove(ci.challenger);
							BattleHandler.challengingPlayers.remove(ci);
							ci = null;
						}
					}
				}
				else
				{
					//don't allow other players to his us
					event.setCancelled(true);
				}
			}
			else
			{	
				//don't allow other mobs to hit us
					event.setCancelled(true);
			}
			//don't allow me to hit other playersif in battle
			if (event.getEntity() instanceof Player)
			{
				Player opp = null;
				//couple cases for opp to populate
				if (event.getDamager() instanceof Fireball)
					if (((Fireball)(event.getDamager())).getShooter() instanceof Player)
						opp = (Player) ((Fireball) event.getDamager()).getShooter();
				if (event.getDamager() instanceof Arrow)
					if (((Arrow)(event.getDamager())).getShooter() instanceof Player)
						opp = (Player) ((Arrow) event.getDamager()).getShooter();
				if (event.getDamager() instanceof Player)
					opp = (Player) event.getDamager();
				//check if opp has been found
				if (opp != null && ci != null)
				{
					if (ci.opponent.getName().toLowerCase().equals(opp.getName().toLowerCase()) 
							&& !ci.challenger.getName().toLowerCase().equals(((Player)event.getEntity()).getName().toLowerCase()))
					{
						event.setCancelled(true);
					}
					if (ci.challenger.getName().toLowerCase().equals(opp.getName().toLowerCase()) 
							&& !ci.opponent.getName().toLowerCase().equals(((Player)event.getEntity()).getName().toLowerCase()))
					{
						event.setCancelled(true);
					}
				}
			}
		}	
	}

	private ChallengeInstance findChallengeInstance(Player p) {
		Iterator<ChallengeInstance> it = BattleHandler.challengingPlayers.iterator();
		while(it.hasNext())
		{
			ChallengeInstance challenge = it.next();
			if (challenge.challenger.getName().toLowerCase().equals(p.getName().toLowerCase()) 
					|| challenge.opponent.getName().toLowerCase().equals(p.getName().toLowerCase()))
			{
				return challenge;
			}
		}
		return null;
	}

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event)
    {
    	//try to not let mobs target us
	    if (event.getTarget() instanceof Player)
	    {
	    	Iterator<ChallengeInstance> it = BattleHandler.challengingPlayers.iterator();
			while(it.hasNext())
			{
				ChallengeInstance challenge = it.next();
				if (challenge.challenger.getName().toLowerCase().equals(event.getTarget().getName().toLowerCase()) 
						|| challenge.opponent.getName().toLowerCase().equals(event.getTarget().getName().toLowerCase()))
				{
					 if (event.getEntity() instanceof Monster)
					    {
						    event.setCancelled(true);
						    return;
					    }
					    if (event.getEntity() instanceof Witch)
					    {
						    event.setCancelled(true);
						    return;
					    }
				}
			}
		   
	    }
    }
    
    @EventHandler
    public void onPlayerQuitEvent (PlayerQuitEvent  event)
    {
    	//check if player leaving is in the challenge list
    	ChallengeInstance flag = null;
    	Iterator<ChallengeInstance> it = BattleHandler.challengingPlayers.iterator();
		while(it.hasNext())
		{
			ChallengeInstance challenge = it.next();
			if (challenge.challenger.getName().toLowerCase().equals(event.getPlayer().getName().toLowerCase()) 
					|| challenge.opponent.getName().toLowerCase().equals(event.getPlayer().getName().toLowerCase()))
			{
				flag = challenge;
			}
		}
		if (flag != null)
		{
			//clean up
			BattleHandler.playersInList.remove(flag.opponent);
			BattleHandler.playersInList.remove(flag.challenger);
			BattleHandler.challengingPlayers.remove(flag);
		}
    }
	
}
