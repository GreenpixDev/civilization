package ru.greenpix.civilization.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import ru.greenpix.civilization.Prefixs;
import ru.greenpix.civilization.database.SafeId;
import ru.greenpix.civilization.database.Stored;
import ru.greenpix.civilization.database.Tables;
import ru.greenpix.civilization.database.Tables.TableCivilizations;
import ru.greenpix.civilization.groups.Group;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.player.Request;
import ru.greenpix.civilization.processes.ResearchProcess;
import ru.greenpix.civilization.technologies.Era;
import ru.greenpix.civilization.technologies.Technology;
import ru.greenpix.civilization.utils.RunnableManager;
import ru.greenpix.mysql.api.Result;
import ru.greenpix.mysql.elements.MysqlTable;
import ru.greenpix.mysql.nbt.MysqlFields;

public class Civilization implements ICObject {

	private final static List<Civilization> civilizations = new ArrayList<>();
	
	private final List<Request> requests = new ArrayList<>();
	
	private final Map<String, Double> values = new HashMap<>();
	
	private final List<Town> towns = new ArrayList<>();
	
	private final List<Technology> technologies = new ArrayList<>();
	
	private final List<Group> groups = new ArrayList<>();
	
	private final int id;
	
	private final Date timestamp;
	
	private String name;
	
	private String owner;
	
	private String tag = "000";
	
	private double balance;
	
	private ResearchProcess process;
	
	private Town capital;
	
	public long breakWonder = 0;
	
	public Civilization(Result result) {
		getCivilizations().add(this);
		this.id = result.getInt("id");
		this.name = result.getString(TableCivilizations.CIVILIZATION_NAME);
		this.owner = result.getString(TableCivilizations.CREATOR);
		this.tag = result.getString(TableCivilizations.CIVILIZATION_TAG);
		this.balance = result.getDouble(TableCivilizations.BALANCE);
		this.timestamp = result.getDate(TableCivilizations.TIME_CREATION);
		this.capital = Town.findById(result.getInt(TableCivilizations.CAPITAL));
		double progress = result.getDouble(TableCivilizations.TECHNOLOGY_PROGRESS);
		String task = result.getString(TableCivilizations.TECHNOLOGY_TASK);
		if(progress < 100 && task != null) {
			this.process = new ResearchProcess(this, Technology.getByName(task));
			this.process.setProgress(progress);
			process.start();
		}
	}
	
	@SafeId
	public Civilization(Player owner, String name, String tag) {
		getCivilizations().add(this);
		this.id = lastId() + 1;
		this.timestamp = new Date();
		this.owner = owner.getName();
		this.name = name;
		this.tag = tag;
		//
		new Group(this, Group.GROUP_OF_MEMBERS);
		new Group(this, Group.GROUP_OF_LEADERS);
	}
	
	public List<Town> getTowns() {
		return towns;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public MysqlTable getSqlTable() {
		return Tables.getTable(TableCivilizations.class);
	}

	@Override
	public MysqlFields getSqlRecord() {
		return new MysqlFields().
				put(TableCivilizations.CIVILIZATION_NAME, getName()).
				put(TableCivilizations.CIVILIZATION_TAG, getTag()).
				put(TableCivilizations.CREATOR, getOwner()).
				put(TableCivilizations.BALANCE, getBalance()).
				put(TableCivilizations.CAPITAL, getCapital().getId()).
				put(TableCivilizations.TECHNOLOGY_PROGRESS, getProcess() == null ? 100 : getProcess().getProgress()).
				put(TableCivilizations.TECHNOLOGY_TASK, getProcess() == null ? null : getProcess().getTechnology().getName()).
				put(TableCivilizations.TIME_CREATION, getTimestamp());
				
	}
	
	public Town getCapital() {
		return capital;
	}
	
	public boolean isDestroyed() {
		return getTowns().size() == 0;
	}
	
	public void saveBalance() {
		RunnableManager.async(() -> getSqlTable().set(new MysqlFields().put(TableCivilizations.BALANCE, getBalance()), getSqlWhere()));
	}
	
	public ResearchProcess getProcess() {
		return process;
	}
	
	public boolean isProcessRunning() {
		return getProcess() != null && !getProcess().isCompleted() && getProcess().isRunning();
	}

	public Map<String, Double> getValues() {
		return values;
	}
	
	@Override
	public double getValue(String type) {
		double v;
		switch (type) {
		case Countable.SCORES:
			v = technologies.stream().mapToDouble(e -> e.getValue(type)).sum();
			break;
		default:
			v = 0;
			break;
		}
		return v + values.getOrDefault(type, 0D) +
				towns.stream().mapToDouble(e -> e.getValue(type)).sum();
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public List<Technology> getTechnologies() {
		return technologies;
	}
	
	public Technology getTechnology(String name) {
		return technologies.stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public boolean hasTechnology(Technology tech) {
		return technologies.stream().anyMatch(t -> t.equals(tech));
	}
	
	public boolean hasTechnology(String name) {
		return getTechnology(name) != null;
	}
	
	public ResearchProcess research(Technology tech) {
		this.process = new ResearchProcess(this, tech);
		this.process.start();
		broadcast("&fНачалось изучение технологии '" + tech.getDisplayName() + "'.");
		return this.process;
	}
	
	public boolean canResearch(Technology tech) {
		return tech.getParents().stream().allMatch(t -> hasTechnology(t));
	}

	@Override
	public List<Group> getGroups() {
		return groups;
	}
	
	public void broadcast(String msg) {
		getGroupOfMembers().getOnline().forEach(p -> p.sendMessage(Prefixs.CIV + msg));
	}

	@Override
	public void broadcast(String msg, String... perms) {
		getPlayersWithPerms(Arrays.asList(perms)).forEach(p -> p.sendMessage(Prefixs.CIV + msg));
	}

	@Override
	public List<Request> getRequests() {
		return requests;
	}

	@Override
	public double getBalance() {
		return balance;
	}

	@Override
	public void setBalance(double balance) {
		this.balance = balance;
		saveBalance();
	}
	
	@Override
	public void withdraw(double balance) {
		this.balance -= balance;
		saveBalance();
	}

	@Override
	public void deposit(double balance) {
		this.balance += balance;
		saveBalance();
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public Era getEra() {
		return getTechnologies().stream()
				.map(Technology::getEra)
				.max(Comparator.comparing(Era::index)).orElse(Era.ANCIENT);
	}
	
	public String getDisplayTag() {
		return getEra().getColor() + "§l" + getTag();
	}

	public static List<Civilization> getCivilizations() {
		return civilizations;
	}
	
	public static Civilization getByName(String name) {
		return getCivilizations().stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public static Civilization getById(int id) {
		return getCivilizations().stream().filter(c -> c.getId() == id).findFirst().orElse(null);
	}
	
	public static Civilization findById(int id) {
		return Stored.findById(id, getCivilizations(), Tables.getTable(TableCivilizations.class), r -> new Civilization(r));
	}
	
	public static Set<Civilization> getOnlineTowns() {
		return CivPlayer.getPlayers().stream().filter(CivPlayer::hasHome).map(CivPlayer::getCivilization).collect(Collectors.toSet());
	}

	public void setCapital(Town capital) {
		this.capital = capital;
	}
}