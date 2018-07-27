package MSG.GameMechanics;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import MSG.Main.Core;

/**
 * Represents a watcher class which watches a chest's inventory until it is empty
 * Once it's empty, it removes the chest block
 */
public class ChestLootWatcher implements Runnable{

	private Block chestBlock;
	private Inventory chestInventory;
	private int id;
	
	public ChestLootWatcher(Block chestBlock) {
		this.chestBlock = chestBlock;
		chestInventory = ((Chest)chestBlock.getState()).getBlockInventory();
		
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 60L, 60L); // every 3 seconds
	}
	
	public void end(){
		Bukkit.getScheduler().cancelTask(id);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if (chestInventory == null
				|| chestInventory.getContents() == null
				|| doesChestContainItems(chestInventory) == false){
			// this chest is now empty, remove the block and end watcher
			chestBlock.getWorld().playEffect(chestBlock.getLocation(), Effect.STEP_SOUND, chestBlock.getTypeId(), 10); // block break effect
			chestBlock.setType(Material.AIR);
			end();
		}
	}
	
	public boolean doesChestContainItems(Inventory inventory){
		for (ItemStack stack : inventory.getContents()){
			if (stack != null
					&& stack.getType() != Material.AIR){
				return true;
			}
		}
		return false;
	}
}
