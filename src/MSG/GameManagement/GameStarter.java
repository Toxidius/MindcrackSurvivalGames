package MSG.GameManagement;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import MSG.Main.Core;

public class GameStarter implements Runnable, Listener{
	
	private int calls;
	private int countdown;
	private boolean countingDown;
	private int numPlayersRequired;
	private int numOnline;
	private int numReady;
	private int id;
	private Set<Player> playersReady;
	
	public GameStarter(){
		Core.registerListener(this);
		
		calls = 0;
		countdown = 0;
		countingDown = false;
		numPlayersRequired = 4;
		playersReady = new HashSet<Player>();
	}
	
	public void reset(){
		calls = 0;
		countdown = 0;
		countingDown = false;
		playersReady = new HashSet<Player>();
	}
	
	public void start(){
		reset(); // make sure the values have been reset
		
		// give all players the not ready item
		ItemStack notReadyItem = new ItemStack(Material.REDSTONE, 1);
		ItemMeta meta = notReadyItem.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Currently Not Ready");
		notReadyItem.setItemMeta(meta);
		
		for (Player player : Bukkit.getOnlinePlayers()){
			player.getInventory().addItem(notReadyItem);
		}
		
		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 20L, 20L); // start this class as a runnable every 1 second
	}
	
	public void stop(){
		Bukkit.getScheduler().cancelTask(id);
		reset();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST) // highest priority so it gets called last (able to add not ready item after inventory clearing)
	public void onPlayerJoin(PlayerJoinEvent e){
		if (Core.gameStarted == false){
			// give all players the not ready item
			ItemStack notReadyItem = new ItemStack(Material.REDSTONE, 1);
			ItemMeta meta = notReadyItem.getItemMeta();
			meta.setDisplayName(ChatColor.RED + "Currently Not Ready");
			notReadyItem.setItemMeta(meta);
			e.getPlayer().getInventory().addItem(notReadyItem);
		}
	}
	
	@EventHandler
	public void onPlayerRightClickReady(PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if (e.getPlayer().getItemInHand().getType() == Material.EMERALD 
					&& e.getPlayer().getItemInHand().hasItemMeta()
					&& e.getPlayer().getItemInHand().getItemMeta().hasDisplayName()
					&& e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Currently Ready")){
				
				// player is becoming NOT ready
				e.setCancelled(true);
				playersReady.remove(e.getPlayer());
				
				// make it the NOT ready item
				e.getPlayer().getItemInHand().setType(Material.REDSTONE);
				ItemMeta meta = e.getPlayer().getItemInHand().getItemMeta();
				meta.setDisplayName(ChatColor.RED + "Currently Not Ready");
				e.getPlayer().getItemInHand().setItemMeta(meta);
				e.getPlayer().getItemInHand().setAmount(1);
				
				e.getPlayer().sendMessage(ChatColor.RED + "You're NOT ready to play. :(");
			}
			else if (e.getPlayer().getItemInHand().getType() == Material.REDSTONE 
					&& e.getPlayer().getItemInHand().hasItemMeta()
					&& e.getPlayer().getItemInHand().getItemMeta().hasDisplayName()
					&& e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Currently Not Ready")){
				
				// player is becoming ready
				e.setCancelled(true);
				playersReady.add(e.getPlayer());
				
				// make it the ready item
				e.getPlayer().getItemInHand().setType(Material.EMERALD);
				ItemMeta meta = e.getPlayer().getItemInHand().getItemMeta();
				meta.setDisplayName(ChatColor.GREEN + "Currently Ready");
				e.getPlayer().getItemInHand().setItemMeta(meta);
				e.getPlayer().getItemInHand().setAmount(1);
				
				e.getPlayer().sendMessage(ChatColor.GREEN + "You're ready to play!");
			}
		}
	}

	@Override
	public void run() {
		numOnline = Bukkit.getServer().getOnlinePlayers().size();
		numReady = 0;
		if (playersReady.size() > 0){
			for (Player player : playersReady){
				if (player != null && player.isOnline()){
					// player is online and ready
					numReady++;
				}
				else{
					// player no longer online
					playersReady.remove(player);
				}
			}
		}
		
		calls++;
		
		if (countingDown == true){
			// a countdown is currently in progress
			if (countdown == 0){
				// the game will now start if there is enough players
				if (numOnline < numPlayersRequired){
					Bukkit.getServer().broadcastMessage(ChatColor.RED + "Not enough players!");
					countingDown = false;
					countdown = 0;
				}
				else{
					Core.gameManager.startGame(null); // start game with random map
					countingDown = false;
					countdown = 0;
				}
			}
			else{
				// countdown message
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "Starting in " + countdown + " seconds!");
				countdown--;
			}
		}
		else{
			if (numOnline < numPlayersRequired){
				// wait message
				if ((calls%60 == 0) && (numOnline != 0)){
					Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "Requires atleast " + numPlayersRequired + " players to start");
				}
			}
			else if (numOnline >= numPlayersRequired && (numReady >= numOnline/2.0)){
				// start the countdown
				countdown = 15;
				countingDown = true;
			}
			else if (numOnline >= numPlayersRequired && calls%15 == 0){
				// numOnline >= numPlayersRequired and < 50% are ready
				String notReady = "";
				for (Player player : Bukkit.getOnlinePlayers()){
					if (playersReady.contains(player) == false){
						notReady += player.getName() + " ";
					}
				}
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "There are " + ChatColor.WHITE + numReady + ChatColor.GOLD + " of " + ChatColor.WHITE + numOnline + ChatColor.GOLD + " ready. Needs 50% ready to start.");
				Bukkit.getServer().broadcastMessage(ChatColor.RED + "Not ready: " + notReady);
			}
		}
		
	}

}