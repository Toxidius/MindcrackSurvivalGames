package MSG.GameManagement;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import MSG.Main.Core;

public class WorldManager {
	
	public WorldTools worldTools;
	private ConfigManager configManager;
	private File worldsDir;
	private File chosenWorldConfig;
	private ArrayList<File> worlds;
	private String pathSeparator = File.separator;
	
	public WorldManager(){
		worldTools = new WorldTools();
		
		worldsDir = new File("MSGGameWorlds");
		if (worldsDir.exists() == false){
			worldsDir.mkdir();
		}
		
		worlds = new ArrayList<>();
		for (File file : worldsDir.listFiles()){
			if (file.isDirectory()){
				// is a world directory
				worlds.add(file);
			}
		}
	}
	
	public boolean createGameWorld(String worldName){
		if (worlds == null
				|| worlds.isEmpty()
				|| worlds.size() < 1){
			Bukkit.getServer().broadcastMessage("No game worlds able to be loaded!");
			return false;
		}
		
		// delete game world if already exists
		if (worldTools.checkWorldExists("world_game") == true){
			for (Player player : Bukkit.getOnlinePlayers()){
				player.teleport(Core.lobbySpawn);
			}
			deleteGameWorld();
		}
		
		// choose the game world
		File chosenGameWorld = null;
		if (worldName == null){
			// choose random game world
			chosenGameWorld = worlds.get(Core.r.nextInt(worlds.size()));
		}
		else{
			// worldName was specified, get it's directory
			for (File file : worlds){
				if (file.getName().contains(worldName)){
					chosenGameWorld = file;
					break;
				}
			}
			if (chosenGameWorld == null){
				// specified world could not be found, choose the first one in the list
				chosenGameWorld = worlds.get(0);
			}
		}
		
		// determine the config file for this world
		chosenWorldConfig = new File(worldsDir.getPath() + pathSeparator + chosenGameWorld.getName() + ".yml");
		if (chosenWorldConfig.exists() == false){
			Bukkit.getServer().broadcastMessage("The world config file doesn't exist!");
			return false;
		}
		
		// copy game world
		try{
			worldTools.copyWorld(chosenGameWorld.getPath(), "world_game");
		}
		catch (Exception e){
			System.out.println("----- Error while copying the new game world! -----");
			System.out.println("Error: " + e.getMessage());
			System.out.println("-------------------------------------");
		}
		
		// load game world
		worldTools.loadWorld("world_game");
		Core.gameWorld = Bukkit.getWorld("world_game");
		
		// set some world values
		Core.gameWorld.setAutoSave(false);
		Core.gameWorld.setDifficulty(Difficulty.HARD);
		
		// load config values
		loadConfig();
		return true;
	}
	
	public void deleteGameWorld(){
		// unload and delete old game world
		try{
			worldTools.unloadWorld("world_game");
			worldTools.deleteWorld("world_game");
		}
		catch (Exception e){
			System.out.println("----- Error while unloading and deleting the game world! -----");
			System.out.println("Error: " + e.getMessage());
			System.out.println("-------------------------------------");
		}
	}
	
	public void loadConfig(){
		// loads in all values from the config, and sets up the necessary values in the main class	
		double x, y, z;
		World world = Core.lobbyWorld;
		configManager = new ConfigManager(chosenWorldConfig);
		
		if (configManager.getBoolean("is-team-game") == true){
			// this map is a team blood bath map, get the spawn locations
			Core.gameManager.isTeamGame = true;
			
			x = configManager.getDouble("team1.spawn-x");
			y = configManager.getDouble("team1.spawn-y");
			z = configManager.getDouble("team1.spawn-z");
			Core.team1Spawn = new Location(world, x, y, z);
			
			x = configManager.getDouble("team2.spawn-x");
			y = configManager.getDouble("team2.spawn-y");
			z = configManager.getDouble("team2.spawn-z");
			Core.team2Spawn = new Location(world, x, y, z);
			
			x = configManager.getDouble("team3.spawn-x");
			y = configManager.getDouble("team3.spawn-y");
			z = configManager.getDouble("team3.spawn-z");
			Core.team3Spawn = new Location(world, x, y, z);
			
			x = configManager.getDouble("team4.spawn-x");
			y = configManager.getDouble("team4.spawn-y");
			z = configManager.getDouble("team4.spawn-z");
			Core.team4Spawn = new Location(world, x, y, z);
		}
		else{
			// this map is a Free For All map
			Core.gameManager.isTeamGame = false;
		}
	}
}