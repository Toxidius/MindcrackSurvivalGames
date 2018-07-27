package MSG.GameMechanics;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import MSG.Main.Core;

public class BlockBreak implements Listener{

	private ArrayList<Material> breakableMaterials;
	
	public BlockBreak() {
		breakableMaterials = new ArrayList<>();
		breakableMaterials.add(Material.LONG_GRASS);
		breakableMaterials.add(Material.GRASS);
		breakableMaterials.add(Material.DIRT);
		breakableMaterials.add(Material.LEAVES);
		
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent e){
		if (Core.gameStarted == true
				&& e.getPlayer().getGameMode() == GameMode.SURVIVAL){
			if (breakableMaterials.contains(e.getBlock().getType()) == true){
				e.setCancelled(false); // allow players to break these blocks in game
			}
			else{
				e.setCancelled(true);
			}
		}
	}
}
