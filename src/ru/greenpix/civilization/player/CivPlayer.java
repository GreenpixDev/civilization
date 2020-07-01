package ru.greenpix.civilization.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import ru.greenpix.civilization.Prefixs;
import ru.greenpix.civilization.diplomacy.Diplomacy;
import ru.greenpix.civilization.diplomacy.Status;
import ru.greenpix.civilization.groups.Group;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.objects.Economical;
import ru.greenpix.civilization.objects.Groupable;
import ru.greenpix.civilization.objects.Requester;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.utils.RunnableManager;
import ru.greenpix.developer.DeveloperAPI;
import ru.greenpix.developer.PluginPlayer;
import ru.greenpix.developer.Utils;

public class CivPlayer implements Requester, Economical {

	private static final List<CivPlayer> players = Collections.synchronizedList(new ArrayList<CivPlayer>());
	
	private final List<Request> requests = new ArrayList<Request>();
	
	private final Player player;

	private ChatExecutor chatExecutor = null;
	
	private Groupable home = null;
	
	private Scoreboard scoreboard;
	
	public Town townLocation = null;
	
	private final Objective health;
	
	public CivPlayer(Player player) {
		this.player = player;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.player.setScoreboard(scoreboard);
		this.home = Town.getTowns().parallelStream().filter(t -> t.getGroupOfMembers() != null && t.getGroupOfMembers().contains(getName())).findFirst().orElse(null);
		this.health = scoreboard.registerNewObjective("health", "health");
		this.health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		this.health.setDisplayName(Utils.color("&c&l❤"));
		addPlayer(this);
		RunnableManager.async(() -> setupTags());
	}
	
	public void setupTags() {
		updateTag(this);
		for(CivPlayer p : players) {
			if(equals(p)) return; 
			this.health.getScore(p.getName()).setScore((int) p.toBukkit().getHealth());
			p.health.getScore(this.getName()).setScore((int) this.toBukkit().getHealth());
			this.updateTag(p);
			p.updateTag(this);
		}
	}
	
	/**
	 * Асинхронно обновляет жизни игрока this для всех игроков
	 */
	
	public void asyncUpdateHealth() {
		RunnableManager.async(() -> players.forEach(p -> p.health.getScore(this.getName()).setScore((int) this.toBukkit().getHealth())));
	}
	
	/**
	 * Асинхронно обновляет тег игрока this для всех игроков
	 */
	
	public void asyncUpdateTag() {
		RunnableManager.async(() -> players.forEach(p -> p.updateTag(this)));
	}
	
	/**
	 * @param p - игрок, чей тег нужно обновить для игрока this
	 */
	
	public void updateTag(CivPlayer p) {
		try {
			String name = (p.getTown() == null ? "z" : (9 - p.getCivilization().getEra().index()) + p.getCivilization().getTag()) + getPluginPlayer().getId();
			Team team = scoreboard.getTeam(name);
			if(team == null) team = scoreboard.registerNewTeam(name);
			if(p.getTown() != null) {
				team.setPrefix(p.getCivilization().getDisplayTag() + (getTown() == null ? Status.NEUTRAL.color : Diplomacy.getStatus(p.getCivilization(), getCivilization()).color) + " ");
			} else {
				team.setPrefix("§7");
			}
			team.addEntry(p.getName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void remove() {
		removePlayer(this);
	}
	
	public Player toBukkit() {
		return player;
	}
	
	public String getName() {
		return player.getName();
	}
	
	public Location getLocation() {
		return player.getLocation();
	}
	
	public PluginPlayer getPluginPlayer() {
		return PluginPlayer.getPlayer(toBukkit());
	}
	
	public void teleport(Location location) {
		player.teleport(location);
	}
	
	public Groupable getHome() {
		return home;
	}
	
	public void setHome(Groupable home) {
		this.home = home;
	}
	
	public Town getTown() {
		return home instanceof Town ? (Town) home : null;
	}
	
	public boolean hasHome() {
		return getHome() != null;
	}
	
	public boolean hasTown() {
		return getTown() != null;
	}
	
	public Civilization getCivilization() {
		return hasTown() ? getTown().getCivilization() : null;
	}
	
	public ChatExecutor getChatExecutor() {
		return chatExecutor;
	}

	public void setChatExecutor(ChatExecutor chatExecutor) {
		this.chatExecutor = chatExecutor;
	}
	
	public static List<CivPlayer> getPlayers() {
		return players;
	}
	
	public static CivPlayer wrap(Object o) {
		if(o instanceof String) return getByName((String) o);
		else if(o instanceof Player) return getByPlayer((Player) o);
		else return null;
	}
	
	public void sendMessage(String msg) {
		toBukkit().sendMessage(Utils.color(msg));
	}
	
	public List<Group> getGroups(Groupable g) {
		return g.getGroups().stream().filter(e -> e.contains(getName())).collect(Collectors.toList());
	}
	
	public boolean hasPermission(Groupable g, String perm) {
		return g.getGroups().stream().anyMatch(e -> e.contains(getName()) && e.hasPermission(perm));
	}
	
	public void closeGuise() {
		toBukkit().closeInventory();
	}
	
	public List<Request> getRequestsAll() {
		List<Request> list = new ArrayList<>(requests);
		if(hasTown()) {
			list.addAll(getTown().getRequests());
			list.addAll(getCivilization().getRequests());
		}
		return list;
	}

	@Override
	public List<Request> getRequests() {
		return requests;
	}
	
	@Override
	public void broadcast(String msg, String... perms) {
		sendMessage(Prefixs.INFO + msg);
	}
	
	public static CivPlayer getByName(String name) {
		return players.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public static CivPlayer getByPlayer(Player player) {
		return players.stream().filter(p -> p.toBukkit().equals(player)).findFirst().orElse(null);
	}
	
	public static CivPlayer wrapSafely(Object o) {
		return wrap(o);
	}
	
	private static void addPlayer(CivPlayer player) {
		players.add(player);
	}
	
	private static void removePlayer(CivPlayer player) {
		players.remove(player);
	}
	
	public static boolean isOnline(String player) {
		return getByName(player) != null;
	}

	@Override
	public double getBalance() {
		return DeveloperAPI.getEconomy().getBalance(toBukkit());
	}

	@Override
	public void setBalance(double balance) {
		DeveloperAPI.getEconomy().setBalance(toBukkit(), balance);
	}
	
	@Override
	public void withdraw(double balance) {
		DeveloperAPI.getEconomy().withdraw(player, balance);
	}

	@Override
	public void deposit(double balance) {
		DeveloperAPI.getEconomy().deposit(player, balance);
	}
}
