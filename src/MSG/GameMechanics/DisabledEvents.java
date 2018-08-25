package MSG.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import MSG.Main.Core;

public class DisabledEvents implements Listener{
	
	public DisabledEvents() {
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerLobbyDamage(EntityDamageEvent e){
		if (Core.gameStarted == false
				&& e.getEntity() instanceof Player){
			e.setCancelled(true); // no lobby damage
			
			if (e.getCause() == DamageCause.VOID){
				e.getEntity().teleport(Core.lobbySpawn);
			}
		}
	}
	
	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent e){
		if (e.getEntity() instanceof Monster){
			e.setCancelled(true); // monsters are not allowed to spawn in game (from monster spawner or other cause)
		}
	}
	
	@EventHandler
	public void onServerPing(ServerListPingEvent e){
		int numOnline = Bukkit.getOnlinePlayers().size();
		String serverName = ChatColor.GREEN + "MSG!";
		String MOTD = Core.MOTD;
		
		if (numOnline <= 0){
			e.setMotd(serverName + ChatColor.WHITE + " - " + ChatColor.WHITE + MOTD);
			//e.setMotd(ChatColor.DARK_PURPLE + "Crack Attack " + ChatColor.LIGHT_PURPLE + "Remake!" + ChatColor.WHITE + " - " + ChatColor.WHITE + CACore.MOTD);
		}
		else if (numOnline > 0 && MOTD.length() <= 28){
			e.setMotd(serverName + ChatColor.WHITE + " - " + ChatColor.WHITE + MOTD
					+ "\n" + ChatColor.AQUA + ChatColor.UNDERLINE + numOnline + ChatColor.RESET + ChatColor.AQUA + " Currently online!");
		}
		else{
			e.setMotd(serverName + ChatColor.WHITE + " - " + ChatColor.WHITE + MOTD
					+ "   " + ChatColor.AQUA + ChatColor.UNDERLINE + numOnline + ChatColor.RESET + ChatColor.AQUA + " Currently online!");
		}
		
		String address = e.getAddress().toString();
		address = address.substring(1, address.length());
		//System.out.println("Ping from " + address);
	}
	
	@EventHandler
	public void onBlockBreakInLobby(BlockBreakEvent e){
		if (Core.gameStarted == false
				&& e.getPlayer().getGameMode() != GameMode.CREATIVE){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlaceInLobby(BlockPlaceEvent e){
		if (Core.gameStarted == false
				&& e.getPlayer().getGameMode() != GameMode.CREATIVE){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e){
		if (e.getEntity().getItemStack() != null
				&& e.getEntity().getItemStack().getType() == Material.SEEDS){
			e.setCancelled(true); // prevent seeds from being spawned/dropped in the game (from breaking tall grass)
		}
	}
	
	@EventHandler
	public void onPortalTravel(PlayerPortalEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerAchievement(PlayerAchievementAwardedEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onHungerDeplete(FoodLevelChangeEvent e){
		if (Core.gameStarted == false
				&& e.getEntity() instanceof Player){
			e.setCancelled(true); // cancel the regular health depleat so the following modifications take place
			Player player = (Player) e.getEntity();
			player.setFoodLevel(20); // set food level to full
			player.setSaturation(20); // set saturation to 20
		}
	}
	
	@EventHandler
	public void onSpectatorVoidDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof Player){
			Player player = (Player) e.getEntity();
			if (player.getGameMode() == GameMode.SPECTATOR){
				e.setCancelled(true); // cancel spectator damage by void
			}
		}
	}
	
	@EventHandler
	public void onSpectatorInteractWithBlock(PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& Core.gameStarted == true
				&& e.getPlayer().getGameMode() == GameMode.SPECTATOR
				&& e.getPlayer().isOp() == false){
			e.setCancelled(true); // prevent spectator from interacting with blocks like chests, furnaces, droppers, etc
			// only op'ed spectators can get access to chests
		}
	}
}