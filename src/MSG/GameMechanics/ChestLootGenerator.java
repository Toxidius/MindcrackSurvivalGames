package MSG.GameMechanics;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class ChestLootGenerator {

	private Random r;
	private ArrayList<Material> weaponMaterials;
	private ArrayList<Material> armorMaterials;
	private ArrayList<Material> foodMaterials;
	private ArrayList<Material> miscMaterials;
	private ArrayList<Material> rareMaterials;
	private ArrayList<Enchantment> bookEnchantments;
	
	public ChestLootGenerator() {
		r = new Random();
		weaponMaterials = new ArrayList<>();
		weaponMaterials.add(Material.WOOD_SWORD);
		weaponMaterials.add(Material.STONE_SWORD);
		weaponMaterials.add(Material.BOW);
		
		armorMaterials = new ArrayList<>();
		armorMaterials.add(Material.GOLD_BOOTS);
		armorMaterials.add(Material.GOLD_LEGGINGS);
		armorMaterials.add(Material.GOLD_CHESTPLATE);
		armorMaterials.add(Material.GOLD_HELMET);
		armorMaterials.add(Material.CHAINMAIL_BOOTS);
		armorMaterials.add(Material.CHAINMAIL_LEGGINGS);
		armorMaterials.add(Material.CHAINMAIL_CHESTPLATE);
		armorMaterials.add(Material.CHAINMAIL_HELMET);
		armorMaterials.add(Material.IRON_BOOTS);
		armorMaterials.add(Material.IRON_LEGGINGS);
		armorMaterials.add(Material.IRON_CHESTPLATE);
		armorMaterials.add(Material.IRON_HELMET);
		
		foodMaterials = new ArrayList<>();
		foodMaterials.add(Material.GRILLED_PORK);
		foodMaterials.add(Material.ROTTEN_FLESH);
		foodMaterials.add(Material.COOKED_BEEF);
		foodMaterials.add(Material.BAKED_POTATO);
		foodMaterials.add(Material.COOKED_CHICKEN);
		
		miscMaterials = new ArrayList<>();
		miscMaterials.add(Material.GOLD_INGOT);
		miscMaterials.add(Material.IRON_INGOT);
		miscMaterials.add(Material.EXP_BOTTLE);
		miscMaterials.add(Material.EXP_BOTTLE);
		miscMaterials.add(Material.ARROW);
		miscMaterials.add(Material.MILK_BUCKET);
		miscMaterials.add(Material.POTION);
		miscMaterials.add(Material.SPIDER_EYE);
		
		rareMaterials = new ArrayList<>();
		rareMaterials.add(Material.APPLE);
		rareMaterials.add(Material.DIAMOND);
		rareMaterials.add(Material.ENCHANTED_BOOK);
		rareMaterials.add(Material.ENCHANTED_BOOK);
		rareMaterials.add(Material.ENCHANTED_BOOK);
		rareMaterials.add(Material.ENDER_PEARL);
		rareMaterials.add(Material.GOLD_INGOT);
		rareMaterials.add(Material.IRON_INGOT);
		
		bookEnchantments = new ArrayList<>();
		bookEnchantments.add(Enchantment.ARROW_DAMAGE);
		bookEnchantments.add(Enchantment.DAMAGE_ALL);
		bookEnchantments.add(Enchantment.KNOCKBACK);
		bookEnchantments.add(Enchantment.PROTECTION_ENVIRONMENTAL);
		bookEnchantments.add(Enchantment.PROTECTION_PROJECTILE);
	}
	
	public void generateChestLoot(Inventory chestInventory){
		int numItemsToAdd = r.nextInt(6)+4; // 4 to 9 items to add
		for (int i = 0; i < numItemsToAdd; i++){
			chestInventory.addItem(getRandomItem());
		}
		chestInventory.addItem(getWeaponItem());
		chestInventory.addItem(getFoodItem());
	}
	
	public ItemStack getWeaponItem(){
		Material material = weaponMaterials.get(r.nextInt(weaponMaterials.size()));
		return new ItemStack(material, 1);
	}
	
	public ItemStack getArmorItem(){
		Material material = armorMaterials.get(r.nextInt(armorMaterials.size()));
		return new ItemStack(material, 1);
	}
	
	public ItemStack getFoodItem(){
		Material material = foodMaterials.get(r.nextInt(foodMaterials.size()));
		int amount = r.nextInt(3)+1; // 1 to 3
		return new ItemStack(material, amount);
	}
	
	public ItemStack getMiscItem(){
		Material material = miscMaterials.get(r.nextInt(miscMaterials.size()));
		int amount = 1;
		if (material == Material.GOLD_INGOT
				|| material == Material.IRON_INGOT
				|| material == Material.MILK_BUCKET
				|| material == Material.POTION
				|| material == Material.SPIDER_EYE){
			amount = 1;
		}
		else if (material == Material.EXP_BOTTLE){
			amount = r.nextInt(4)+1; // 1 to 4
		}
		else if (material == Material.ARROW){
			amount = r.nextInt(6)+5; // 5 to 10
		}
		
		if (material == Material.POTION){
			if (r.nextInt(2) == 0){
				return new ItemStack(material, amount, (short)16421); // healing 2 splash pot
			}
			else{
				return new ItemStack(material, amount, (short)16386); // speed 1 splash pot
			}
		}
		
		return new ItemStack(material, amount);
	}
	
	public ItemStack getRareItem(){
		Material material = rareMaterials.get(r.nextInt(rareMaterials.size()));
		int amount = 1;
		if (material == Material.APPLE
				|| material == Material.DIAMOND
				|| material == Material.ENCHANTED_BOOK
				|| material == Material.ENDER_PEARL){
			amount = 1;
		}
		else if (material == Material.GOLD_INGOT){
			amount = r.nextInt(4)+3; // 3 to 6
		}
		else if (material == Material.IRON_INGOT){
			amount = r.nextInt(2)+2; // 2 to 3
		}
		
		if (material == Material.ENCHANTED_BOOK){
			return getEnchantedBookItem();
		}
		
		return new ItemStack(material, amount);
	}
	
	public ItemStack getRandomItem(){
		int random = r.nextInt(100);
		if (random <= 20){
			return getFoodItem();
		}
		else if (random <= 30){
			return getWeaponItem();
		}
		else if (random <= 50){
			return getArmorItem();
		}
		else if (random <= 80){
			return getMiscItem();
		}
		else if (random <= 100){
			return getRareItem();
		}
		else{
			// should never occur
			return getFoodItem();
		}
	}
	
	public ItemStack getEnchantedBookItem(){
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
		Enchantment enchantment = bookEnchantments.get(r.nextInt(bookEnchantments.size()));
		
		int level = 1;
		if (enchantment.equals(Enchantment.KNOCKBACK)){
			level = 3;
		}
		
		meta.addStoredEnchant(enchantment, level, true);
		book.setItemMeta(meta);
		return book;
	}
}
