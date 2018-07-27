package MSG.GameManagement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import MSG.Main.Core;
import MSG.Main.GameStates.GameState;

public class ScoreboardManager implements Runnable{
	
	private int id;
	public Scoreboard scoreboard;
	public Scoreboard emptyScoreboard;
	
	private Team team1;
	private Team team2;
	private Team team3;
	private Team team4;
	private Objective sidebarObjective;
	private Objective healthObjective;
	//private Objective killsObjective;
	private Score line1;
	private Score line2;
	private Score line3;
	private Score line4;
	
	public int calls = 0;
	
	public ScoreboardManager(){
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	}
	
	public void setupScoreboard(){
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		team1 = scoreboard.registerNewTeam("Team1");
		team1.setAllowFriendlyFire(false);
		team1.setPrefix(Core.team1Color + "");
		
		team2 = scoreboard.registerNewTeam("Team2");
		team2.setAllowFriendlyFire(false);
		team2.setPrefix(Core.team2Color + "");
		
		team3 = scoreboard.registerNewTeam("Team3");
		team3.setAllowFriendlyFire(false);
		team3.setPrefix(Core.team3Color + "");
		
		team4 = scoreboard.registerNewTeam("Team4");
		team4.setAllowFriendlyFire(false);
		team4.setPrefix(Core.team4Color + "");
		
		healthObjective = scoreboard.registerNewObjective("Health", "health");
		healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		
		//killsObjective = scoreboard.registerNewObjective("Kills", "playerKillCount");
		//killsObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		
		// reset the current time(calls) for scoreboard runnable
		calls = 0;
	}
	
	public void start(){
		// starts the running of the scoreboard
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.thisPlugin, this, 1L, 1L);
	}
	
	public void stop(){
		// ends the running of the scoreboard
		Bukkit.getScheduler().cancelTask(id);
	}
	
	public void addPlayerToTeam(String playerName, int team){
		if (team == 1){
			team1.addEntry(playerName);
		}
		else if (team == 2){
			team2.addEntry(playerName);
		}
	}
	
	public void removePlayerFromTeam(String playerName, int team){
		if (team == 1){
			team1.removeEntry(playerName);
		}
		else if (team == 2){
			team2.removeEntry(playerName);
		}
	}
	
	public void updateSidebar(String title, String line1, String line2, String line3, String line4){
		scoreboard.clearSlot(DisplaySlot.SIDEBAR);
		if (sidebarObjective != null){
			sidebarObjective.unregister(); // sidebar is already active, unregister the active one so it can be replaced
		}
		sidebarObjective = scoreboard.registerNewObjective("gameObjective", "dummy");
		sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		sidebarObjective.setDisplayName(title);
		
		this.line1 = sidebarObjective.getScore(line1);
		this.line1.setScore(4);
		this.line2 = sidebarObjective.getScore(line2);
		this.line2.setScore(3);
		if (line3.length() > 1){
			this.line3 = sidebarObjective.getScore(line3);
			this.line3.setScore(2);
		}
		if (line4.length() > 1){
			this.line4 = sidebarObjective.getScore(line4);
			this.line4.setScore(1);
		}
	}

	@Override
	public void run() {
		// this runnable is called every second and is in charge of updating the scoreboard and controling the flow of the game
		
		if (Core.gameState == GameState.Ending){
			return; // do nothing here the game is already ending
		}
		
		Core.gameManager.updatePlayersRemainingCount();
		
		if (calls%20 == 0){
			Core.gameManager.gameTime++;
			updateScoreboard(); // every second (20 ticks) update the scoreboards
		}
		
		// check if only one player or one team is remaining
		if (Core.gameManager.isTeamGame == true){
			// TODO
		}
		else{
			if (Core.gameManager.playersRemaining <= 1){
				//Core.gameManager.endGameInitiate(Core.gameManager.getLastPlayerAlive()); // TODO remove later on during testing
			}
		}
		
		calls++;
	}
	
	public void updateScoreboard(){
		String title, line1, line2, line3, line4;
		title = ChatColor.GOLD + "" + ChatColor.BOLD + "Mindcrack SG";
		
		String formattedTime = (int)Math.floor(Core.gameManager.gameTime/60.0) + ":" + (Core.gameManager.gameTime%60);
		
		line1 = ChatColor.YELLOW + "Time: " + ChatColor.WHITE + formattedTime;
		line2 = ChatColor.YELLOW + "Players Alive: " + ChatColor.WHITE + Core.gameManager.playersRemaining;
		line3 = "";
		line4 = "";
		
		updateSidebar(title, line1, line2, line3, line4);
	}
	
	public boolean adminOnline(){
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isOp()){
				return true;
			}
		}
		return false;
	}

}