package MSG.GameMechanics;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import MSG.Main.Core;

public class PlayerInteractWithBlock implements Listener{

	private ArrayList<Material> disabledMaterials;
	
	public PlayerInteractWithBlock() {
		disabledMaterials = new ArrayList<>();
		disabledMaterials.add(Material.TRAPPED_CHEST);
		disabledMaterials.add(Material.FURNACE);
		disabledMaterials.add(Material.BURNING_FURNACE);
		disabledMaterials.add(Material.DISPENSER);
		disabledMaterials.add(Material.DROPPER);
		disabledMaterials.add(Material.BEACON);
		disabledMaterials.add(Material.BREWING_STAND);
		disabledMaterials.add(Material.STONE_BUTTON);
		disabledMaterials.add(Material.WOOD_BUTTON);
		disabledMaterials.add(Material.LEVER);
		disabledMaterials.add(Material.STONE_PLATE);
		disabledMaterials.add(Material.WOOD_PLATE);
		disabledMaterials.add(Material.IRON_PLATE);
		disabledMaterials.add(Material.GOLD_PLATE);
		disabledMaterials.add(Material.BED);
		
		Core.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerInteractWithBlock(PlayerInteractEvent e){
		if (Core.gameStarted == false
				&& e.getPlayer().getGameMode() == GameMode.SURVIVAL
				&& (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) ){
			e.setCancelled(true);
		}
		else if (Core.gameStarted == true
				&& e.getPlayer().getGameMode() == GameMode.SURVIVAL
				&& (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) ){
			if (e.getClickedBlock() != null
					&& disabledMaterials.contains(e.getClickedBlock().getType()) == true){
				e.setCancelled(true); // prevent interacting with this block
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractWithChest(PlayerInteractEvent e){
		if (Core.gameStarted == true
				&& e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& e.getClickedBlock().getType() == Material.CHEST
				&& isDoubleChest(e.getClickedBlock()) == false
				&& e.getClickedBlock().hasMetadata("alreadyGeneratedLoot") == false){
			// this chest is a fresh chest that hasn't been given loot yet, give it some and set it's metadata flag
			generateChestLoot(e.getClickedBlock());
			e.getClickedBlock().setMetadata("alreadyGeneratedLoot", new FixedMetadataValue(Core.thisPlugin, true));
			
			// start the watcher that watches this chest until it is emptied
			@SuppressWarnings("unused")
			ChestLootWatcher watcher = new ChestLootWatcher(e.getClickedBlock());
		}
	}
	
	public void generateChestLoot(Block block){
		if (block.getType() != Material.CHEST){
			return;
		}
		Chest chest = (Chest) block.getState();
		chest.getBlockInventory().clear(); // clear out any existing inventory contents (should not have any)
		Core.gameManager.generateChestLoot(chest.getBlockInventory()); // generate the chest inventory
	}
	
	public boolean isDoubleChest(Block block){
		if (block.getRelative(BlockFace.NORTH).getType() == Material.CHEST){
			return true;
		}
		else if (block.getRelative(BlockFace.EAST).getType() == Material.CHEST){
			return true;
		}
		else if (block.getRelative(BlockFace.SOUTH).getType() == Material.CHEST){
			return true;
		}
		else if (block.getRelative(BlockFace.WEST).getType() == Material.CHEST){
			return true;
		}
		return false;
	}
}
