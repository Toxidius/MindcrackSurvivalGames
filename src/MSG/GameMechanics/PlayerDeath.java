package MSG.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import MSG.Main.Core;

public class PlayerDeath implements Listener{

	public PlayerDeath() {
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e){
		if (Core.gameStarted == true){
			Player player = e.getPlayer();
			e.setRespawnLocation(e.getPlayer().getLocation().clone().add(0.0, 10.0, 0.0));
			Location locationToRespawn = e.getPlayer().getLocation().clone().add(0.0, 1.0, 0.0);
			
			// to avoid a weird bug where the player is in "half spectator mode" we setup this runnable to set them in spectator mode
			// after 1 tick from when the respawn
			Bukkit.getScheduler().scheduleSyncDelayedTask(Core.thisPlugin, new Runnable(){
				@Override
				public void run() {
					// one tick later
					player.setGameMode(GameMode.SPECTATOR);
					player.teleport(locationToRespawn);
				}
			}, 1L);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		// exp orb drop calculations
		//e.setDroppedExp(1000);
		Player player = e.getEntity();
		int amountXpDropped = 55;
		if (e.getDroppedExp() > 55){
			amountXpDropped = e.getDroppedExp();
		}
		ExperienceOrb orb = (ExperienceOrb) player.getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
		orb.setExperience(amountXpDropped);
		
		// auto respawning and reset player values
		player.setHealth(20); // auto respawn the player if necessary
		player.setFallDistance(0F);
		player.setFoodLevel(20); // set food level to full
		player.setSaturation(20); // set saturation to 20
		player.setFireTicks(1); // reset fire ticks
		player.setExp(0);
		player.setLevel(0);
		for (PotionEffect effect : player.getActivePotionEffects()){ // remove all potion effects
			player.removePotionEffect(effect.getType());
		}
		player.setGameMode(GameMode.SPECTATOR);
		player.setVelocity(new Vector(0.0, 0.0, 0.0)); // reset velocity
		Location newLocation = player.getLocation().add(0.0, 2.0, 0.0);
		player.teleport(newLocation);
		player.sendTitle(ChatColor.RED + "You Died!", "");
		
		// player name colorizing
		int spaceIndex = e.getDeathMessage().indexOf(" ") + 1;
		String cause = e.getDeathMessage().substring(spaceIndex); // returns the text after the players name (cause of death)
		
		Player otherPlayer;
		for (String string : cause.split(" ")){
			if (isStringAPlayerName(string) == true){
				otherPlayer = Bukkit.getPlayer(string);
				if (otherPlayer != null){
					int otherPlayerTeam = Core.gameManager.getPlayerTeam(otherPlayer);
					if (otherPlayerTeam == 1){
						cause = cause.replace(string, Core.team1Color + otherPlayer.getName() + ChatColor.GRAY);
					}
					else if (otherPlayerTeam == 2){
						cause = cause.replace(string, Core.team2Color + otherPlayer.getName() + ChatColor.GRAY);
					}
					else if (otherPlayerTeam == 3){
						cause = cause.replace(string, Core.team3Color + otherPlayer.getName() + ChatColor.GRAY);
					}
					else if (otherPlayerTeam == 4){
						cause = cause.replace(string, Core.team4Color + otherPlayer.getName() + ChatColor.GRAY);
					}
					else{
						// player is on an unknown team or -1 (no teams for this game)
						cause = cause.replace(string, ChatColor.WHITE + otherPlayer.getName() + ChatColor.GRAY);
					}
				}
			}
		}
		
		int team = Core.gameManager.getPlayerTeam(player);
		if (team == 1){
			e.setDeathMessage(Core.team1Color + player.getName() + " " + ChatColor.GRAY + cause);
		}
		else if (team == 2){
			e.setDeathMessage(Core.team2Color + player.getName() + " " + ChatColor.GRAY + cause);
		}
		else if (team == 3){
			e.setDeathMessage(Core.team3Color + player.getName() + " " + ChatColor.GRAY + cause);
		}
		else if (team == 4){
			e.setDeathMessage(Core.team4Color + player.getName() + " " + ChatColor.GRAY + cause);
		}
		else{
			e.setDeathMessage(ChatColor.WHITE + player.getName() + " " + ChatColor.GRAY + cause);
		}
	}
	
	public boolean isStringAPlayerName(String input){
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getName().equals(input)){
				return true;
			}
		}
		return false;
	}
}