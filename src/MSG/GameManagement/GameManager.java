package MSG.GameManagement;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import MSG.GameMechanics.ChestLootGenerator;
import MSG.Main.Core;
import MSG.Main.GameStates.GameState;

public class GameManager {
	
	private Random r;
	public WorldManager worldManager;
	public ScoreboardManager scoreboardManager;
	public GameStarter gameStarter;
	
	public boolean isTeamGame;
	public int playersRemaining;
	public int gameTime;
	public int numLootChests = 300;
	
	public ChestLootGenerator chestLootGenerator;
	
	/*
	public WoolCountUpdater woolCountUpdater;
	public WoolClimbing woolClimbing;
	public SquidCheckerRunnable squidCheckerRunnable;
	public SplatRollerRunnable splatRollerRunnable;
	public SplatChargerRunnable splatChargerRunnable;
	public SplatterShotRunnable splatterShotRunnable;
	public KitSelectionStands kitSelectionStands;
	*/
	
	public GameManager(){
		r = new Random();
		worldManager = new WorldManager();
		scoreboardManager = new ScoreboardManager();
		chestLootGenerator = new ChestLootGenerator();
		
		gameStarter = new GameStarter();
		gameStarter.start();
	}
	
	@SuppressWarnings("deprecation")
	public boolean startGame(String worldName){
		// returns whether or not the game started successfully
		
		// check if a game can start
		if (Core.gameStarted == true){
			return false; // game is already in progress!
		}
		
		// reset some values
		gameStarter.stop();
		scoreboardManager = new ScoreboardManager();
		gameTime = 0;
		
		// create the game world and load the config values from it (determines if the game is a team game or not)
		//Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "Loading game world...");
		boolean output = createGameWorld(worldName);
		if (output == false){
			return false; // game could not be started because the world couldn't be loaded
		}
		//Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "Done loading game world.");
		
		// update world locations
		Core.gameCenter = Core.gameWorld.getSpawnLocation().clone();
		Core.team1Spawn.setWorld(Core.gameWorld);
		Core.team2Spawn.setWorld(Core.gameWorld);
		Core.team3Spawn.setWorld(Core.gameWorld);
		Core.team4Spawn.setWorld(Core.gameWorld);
		
		// setup scoreboards
		scoreboardManager.setupScoreboard();
		scoreboardManager.updateScoreboard(); // update the scoreboard with the default values
		
		// generate loot chests
		Location chestLocation;
		for (int i = 0; i < numLootChests; i++){
			while (true){
				chestLocation = getSpawnLocation();
				if (chestLocation.getBlock().getRelative(BlockFace.DOWN).getType() == Material.STATIONARY_WATER){
					continue; // this location is not suitable for a chest (it's over water). try for another one
				}
				chestLocation.getBlock().setType(Material.CHEST);
				chestLocation.getBlock().setData( (byte)(r.nextInt(4)+1) ); // random direction (2, 3, 4, or 5)
				break; // we found a suitable location and placed a chest there
			}
		}
		
		// generate the player teams
		if (isTeamGame == true){
			generateTeams();
		}
		
		// finish up the player and teleport into game
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOnline()){
				player.setScoreboard(scoreboardManager.scoreboard);
				player.setGameMode(GameMode.SURVIVAL);
				player.setFallDistance(0); // so they don't die if falling
				
				player.setHealth(18); // near full health
				//regen 5 for 1 second -- makes the scoreboard health value for the players automatically update
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 4));
				
				player.setFoodLevel(20); // set food level to full
				player.setSaturation(40); // set saturation to 40
				clearInventory(player);
			}
		}
		
		// teleporting to team spawn if team game, otherwise a random location
		if (isTeamGame == true){
			int team = 0;
			for (Player player : Bukkit.getOnlinePlayers()){
				if (player.isOnline()){
					team = getPlayerTeam(player);
				}
				if (team == 1){
					player.teleport(Core.team1Spawn);
				}
				else if (team == 2){
					player.teleport(Core.team2Spawn);
				}
				else if (team == 3){
					player.teleport(Core.team3Spawn);
				}
				else if (team == 4){
					player.teleport(Core.team4Spawn);
				}
			}
		}
		else{
			// teleport players to random spawn locations
			for (Player player : Bukkit.getOnlinePlayers()){
				if (player.isOnline()){
					player.teleport(getSpawnLocation().add(0.5, 0.0, 0.5));
				}
			}
		}
		
		// game start messages
		if (isTeamGame == true){
			Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "All chat is team unless prefaced with a \"!\" for global message");
			Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "Ex: \"!Hi Everybody\" for a global message");
		}
		else{
			Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "All chat is global for this Free For All game!");
		}
		
		// start scheduled events
		// scoreboard updater
		scoreboardManager.start();
		// set some final values
		Core.gameStarted = true;
		Core.gameState = GameState.Running;
		
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void endGameInitiate(Player winningPlayer){
		// initiates the game end sequence
		int seconds = 10;
		if (winningPlayer == null){
			seconds = 2; // no player won, game terminated
		}
		
		// end all scheduled events
		scoreboardManager.stop();
		Bukkit.getScheduler().cancelTasks(Core.thisPlugin);
		Core.gameState = GameState.Ending;
		
		String winningMessage = "";
		
		if (isTeamGame == true){
			// TODO get the winning team and their name
		}
		else{
			if (winningPlayer == null){
				winningMessage = ChatColor.DARK_RED + "" + ChatColor.BOLD + "The game was terminated!";
			}
			else{
				String winningPlayerName = winningPlayer.getName();
				winningMessage = ChatColor.BOLD + winningPlayerName + " won the game!";
			}
		}
		
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "-----------------------------------");
		Bukkit.getServer().broadcastMessage(winningMessage);
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "-----------------------------------");
		
		for (Player player : Bukkit.getOnlinePlayers()){
			player.sendTitle("", winningMessage);
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Core.thisPlugin, new Runnable(){
			@Override
			public void run() {
				Core.gameManager.endGame();
			}
		}, seconds*20L); // 10 second delay
	}
	
	public boolean endGame(){
		// return whether or not the game ended successfully
		
		// teleport all players to lobby and reset their inventory and scoreboard
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOnline()){
				if (player.hasMetadata("game" + Core.gameID + "team")){
					player.removeMetadata("game" + Core.gameID + "team", Core.thisPlugin);
				}
				if (player.hasMetadata("game" + Core.gameID + "kit")){
					player.removeMetadata("game" + Core.gameID + "kit", Core.thisPlugin);
				}
				player.setScoreboard(scoreboardManager.emptyScoreboard);
				player.setGameMode(GameMode.SURVIVAL);
				player.setFallDistance(0); // so they don't die if falling
				player.setHealth(20); // full health
				player.setFoodLevel(20); // set food level to full
				player.setSaturation(40); // set saturation to 40
				player.setWalkSpeed(0.2F); // default walk speed
				for (PotionEffect effect : player.getActivePotionEffects()){ // remove all potion effects
					player.removePotionEffect(effect.getType());
				}
				clearInventory(player);
				player.teleport(Core.lobbySpawn);
			}
		}
		
		// reset some values
		Core.gameState = GameState.NotStarted;
		
		// startup game starter
		gameStarter.start();
		
		// delete the game world
		Bukkit.getScheduler().scheduleSyncDelayedTask(Core.thisPlugin, new Runnable(){
			@Override
			public void run() {
				worldManager.deleteGameWorld();
				
				Core.gameStarted = false; // update this so the next game can start
			}
		}, 40L);
		
		return true;
	}
	
	public boolean createGameWorld(String worldName){
		return worldManager.createGameWorld(worldName);
	}
	
	public void generateTeams(){
		// give all players a random number and team of -1
		for (Player player : Bukkit.getOnlinePlayers()){
			player.setMetadata("game" + Core.gameID + "team", new FixedMetadataValue(Core.thisPlugin, new Integer(-1)));
			player.setMetadata( "randomNumber", new FixedMetadataValue(Core.thisPlugin, new Integer(r.nextInt(1000))) );
		}
		
		// loop through the players finding the next player (without team) with the smallest random number and place them on placedTeam
		int teamToBe = 1;
		while (true){
			int currentLowest = 1000000; // arbitrary value (greater than the maximum random) to start off with
			Player lowestPlayer = null;
			int random;
			int team;
			for (Player player : Bukkit.getOnlinePlayers()){
				random = player.getMetadata("randomNumber").get(0).asInt();
				team = player.getMetadata("game" + Core.gameID + "team").get(0).asInt();
				
				if ( (random < currentLowest) && (team == -1) ){
					currentLowest = random;
					lowestPlayer = player;
				}
			}
			
			if (lowestPlayer == null){
				// no player was choosen (all are on teams)
				// done looping through the array. all players should be on teams now...
				return;
			}
			
			lowestPlayer.removeMetadata("game" + Core.gameID + "team", Core.thisPlugin);
			lowestPlayer.setMetadata("game" + Core.gameID + "team", new FixedMetadataValue(Core.thisPlugin, new Integer(teamToBe)));
			
			if (teamToBe == 1){
				scoreboardManager.addPlayerToTeam(lowestPlayer.getName(), 1);
			}
			else if (teamToBe == 2){
				scoreboardManager.addPlayerToTeam(lowestPlayer.getName(), 2);
			}
			
			// update teamToBe for next player
			teamToBe++;
			if (teamToBe > 2){
				teamToBe = 1;
			}
		}
	}
	
	public void setPlayerTeam(Player player, int team, boolean teleport){
		// set meta
		player.removeMetadata("game" + Core.gameID + "team", Core.thisPlugin);
		player.setMetadata("game" + Core.gameID + "team", new FixedMetadataValue(Core.thisPlugin, new Integer(team)));
		
		// set scoreboard
		scoreboardManager.removePlayerFromTeam(player.getName(), team);
		scoreboardManager.addPlayerToTeam(player.getName(), team);
		
		// teleport to new team spawn
		if (teleport == true){
			if (team == 1){
				player.teleport(Core.team1Spawn);
			}
			else if (team == 2){
				player.teleport(Core.team2Spawn);
			}
		}
		
	}
	
	public int getPlayerTeam(String playerName){
		Player player = Bukkit.getPlayer(playerName);
		if (player == null){
			return -1; // no player with this name online
		}
		return getPlayerTeam(player);
	}
	
	public int getPlayerTeam(Player player){
		if (player.hasMetadata("game" + Core.gameID + "team") == true){
			return player.getMetadata("game" + Core.gameID + "team").get(0).asInt();
		}
		else{
			return -1; // no team
		}
	}
	
	public int getAmountInPlayerInventory(Player player, Material material){
		// gets the amount of a specific material in the players inventory
		int amount = 0;
		for (ItemStack stack : player.getInventory().getContents()){
			if (stack == null
					|| stack.getType() == Material.AIR){
				continue; // skip
			}
			if (stack.getType() == material){
				amount += stack.getAmount();
			}
		}
		return amount;
	}
	
	public void teamMessage(int team, String message){
		int playerTeam;
		for (Player player : Bukkit.getOnlinePlayers()){
			playerTeam = getPlayerTeam(player);
			if (playerTeam == team){
				player.sendMessage(message);
			}
		}
	}
	
	public void updatePlayersRemainingCount(){
		int numAlive = 0;
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player != null
					&& player.isOnline()
					&& player.getGameMode() == GameMode.SURVIVAL){
				numAlive++;
			}
		}
		playersRemaining = numAlive;
	}
	
	public Player getLastPlayerAlive(){
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player != null
					&& player.isOnline()
					&& player.getGameMode() == GameMode.SURVIVAL){
				return player;
			}
		}
		return null;
	}
	
	public Location getSpawnLocation(){
		// generates a random location within 275 blocks from the center of the world/map
		int random1 = r.nextInt(550)-275; // -275 to 275
		int random2 = r.nextInt(550)-275; // -275 to 275
		Block highestBlock = Core.gameWorld.getHighestBlockAt(Core.gameCenter.getBlockX()+random1, Core.gameCenter.getBlockZ()+random2);
		
		//Bukkit.getServer().broadcastMessage("type: " + highestBlock.getType() + " x:" + highestBlock.getX() + " y:" + highestBlock.getY() + " z:" + highestBlock.getZ());
		
		return highestBlock.getLocation();
	}
	
	public void generateChestLoot(Inventory chestInventory){
		chestLootGenerator.generateChestLoot(chestInventory);
	}
	
	public void clearInventory(Player player){
		player.setExp(0F);
		player.setLevel(0);
		
		// clears the player's usable inventory
		player.getInventory().clear();
		
		// remove armor slot contents as getInventory().clear doesn't clear this
		PlayerInventory playerInvenotory = player.getInventory();
		ItemStack air = new ItemStack(Material.AIR);
		playerInvenotory.setHelmet(air);
		playerInvenotory.setChestplate(air);
		playerInvenotory.setLeggings(air);
		playerInvenotory.setBoots(air);
		
		// remove the item player has on their cursor
		player.setItemOnCursor(air);
		
		// remove any items in crafting window
		Inventory craftingInventory = player.getOpenInventory().getTopInventory();
		craftingInventory.setItem(1, air);
		craftingInventory.setItem(2, air);
		craftingInventory.setItem(3, air);
		craftingInventory.setItem(4, air);
		
	}

}