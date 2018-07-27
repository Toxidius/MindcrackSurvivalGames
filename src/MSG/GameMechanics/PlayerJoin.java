package MSG.GameMechanics;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import MSG.Main.Core;

public class PlayerJoin implements Listener{
	
	public PlayerJoin(){
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		if (Core.gameStarted == false){
			player.teleport(Core.lobbySpawn);
			player.setGameMode(GameMode.SURVIVAL);
			player.setScoreboard(Core.gameManager.scoreboardManager.emptyScoreboard);
			Core.gameManager.clearInventory(player);
		}
		else{
			// game in progress (even if the player relogged, they will not be able to rejoin the gameplay)
			player.setGameMode(GameMode.SPECTATOR);
			player.teleport(Core.gameCenter);
			player.setScoreboard(Core.gameManager.scoreboardManager.scoreboard);
			Core.gameManager.clearInventory(player);
		}
	}
}