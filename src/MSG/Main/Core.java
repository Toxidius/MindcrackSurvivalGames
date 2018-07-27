package MSG.Main;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import MSG.GameManagement.GameManager;
import MSG.GameMechanics.BlockBreak;
import MSG.GameMechanics.DisabledEvents;
import MSG.GameMechanics.PlayerChat;
import MSG.GameMechanics.PlayerDeath;
import MSG.GameMechanics.PlayerInteractWithBlock;
import MSG.GameMechanics.PlayerJoin;
import MSG.Main.GameStates.GameState;

public class Core extends JavaPlugin{

	public static JavaPlugin thisPlugin;
	public static Random r;
	public static boolean gameStarted;
	public static GameState gameState;
	public static int gameID;
	public static String MOTD;
	
	public static World lobbyWorld;
	public static World gameWorld;
	
	public static Location lobbySpawn;
	public static Location gameCenter;
	public static Location team1Spawn;
	public static Location team2Spawn;
	public static Location team3Spawn;
	public static Location team4Spawn;
	public static int lobbyYaw;
	
	public static ChatColor team1Color;
	public static ChatColor team2Color;
	public static ChatColor team3Color;
	public static ChatColor team4Color;
	
	// global objects
	public static GameManager gameManager;
	public static PluginManager pluginMan;
	
	@SuppressWarnings("unused")
	@Override
	public void onEnable(){
		thisPlugin = this;
		r = new Random();
		gameStarted = false;
		gameState = GameState.NotStarted;
		gameID = 1;
		MOTD = "MSG Baby!";
		pluginMan = Bukkit.getPluginManager();
		
		lobbyWorld = Bukkit.getWorld("world");
		gameWorld = null;
		lobbyYaw = 180;
		lobbySpawn = lobbyWorld.getSpawnLocation();
		lobbySpawn.setYaw(lobbyYaw);
		gameCenter = lobbySpawn.clone();
		team1Spawn = lobbySpawn.clone();
		team2Spawn = lobbySpawn.clone();
		team3Spawn = lobbySpawn.clone();
		team4Spawn = lobbySpawn.clone();
		
		team1Color = ChatColor.RED;
		team2Color = ChatColor.GREEN;
		team2Color = ChatColor.BLUE;
		team2Color = ChatColor.YELLOW;
		
		// non-global objects
		PlayerJoin playerJoin = new PlayerJoin();
		PlayerChat playerChat = new PlayerChat();
		PlayerDeath playerDeath = new PlayerDeath();
		DisabledEvents disabledEvents = new DisabledEvents();
		BlockBreak blockBreak = new BlockBreak();
		PlayerInteractWithBlock playerInteractWithBlock = new PlayerInteractWithBlock();
		
		// initialize objects
		gameManager = new GameManager();
		
		// register listeners
		// no longer needed -- each listener class is responsible for registering as a listener by calling registerListener()
	}
	
	@Override
	public void onDisable(){
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("start")){
			if (!sender.isOp()){
				sender.sendMessage("Must be OP to use this command.");
				return true;
			}
			
			// force start game with random world
			gameManager.startGame(null);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("end")){
			if (!sender.isOp()){
				sender.sendMessage("Must be OP to use this command.");
				return true;
			}
			
			// force end game
			gameManager.endGameInitiate(null); // end with no team/player winning
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("warps")){
			if ( !(sender instanceof Player)){
				sender.sendMessage("Must be player to use this command.");
				return true;
			}
			if (!sender.isOp()){
				sender.sendMessage("Must be OP to use this command.");
				return true;
			}
			sender.sendMessage(ChatColor.GRAY + "Currently loaded worlds: ");
			for (World world : Bukkit.getWorlds()){
				sender.sendMessage(ChatColor.GRAY + world.getName());
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("warp")){
			if ( !(sender instanceof Player)){
				sender.sendMessage("Must be player to use this command.");
				return true;
			}
			if (!sender.isOp()){
				sender.sendMessage("Must be OP to use this command.");
				return true;
			}
			if (args.length < 1){
				sender.sendMessage("Must specify a world to warp to. Ex: /warp world");
				return true;
			}
			if (gameManager.worldManager.worldTools.checkWorldExists(args[0]) == false){
				sender.sendMessage("The world " + args[0] + " doesn't exist!");
				return true;
			}
			// load the world if it isn't already loaded
			gameManager.worldManager.worldTools.loadWorld(args[0]);
			Player player = (Player) sender;
			World world = Bukkit.getWorld(args[0]);
			Location spawnLocation = world.getSpawnLocation();
			player.teleport(spawnLocation);
			return true;
		}
		return false;
	}
	
	public static void registerListener(Listener listener){
		pluginMan.registerEvents(listener, thisPlugin);
	}
	
	
}