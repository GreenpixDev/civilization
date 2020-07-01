package ru.greenpix.civilization.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.Prefixs;
import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.Direction;
import ru.greenpix.civilization.buildings.OwnerBuildings;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.buildings.common.Townhall;
import ru.greenpix.civilization.buildings.wonders.NotreDame;
import ru.greenpix.civilization.buildings.wonders.Wonder;
import ru.greenpix.civilization.database.Stored;
import ru.greenpix.civilization.database.Tables;
import ru.greenpix.civilization.database.Tables.TableTowns;
import ru.greenpix.civilization.groups.Group;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.player.Request;
import ru.greenpix.civilization.processes.BuildingProcess;
import ru.greenpix.civilization.technologies.Technology;
import ru.greenpix.civilization.trade.BiomeInfo;
import ru.greenpix.civilization.trade.CountMap;
import ru.greenpix.civilization.trade.TradeResource;
import ru.greenpix.civilization.utils.RunnableManager;
import ru.greenpix.mysql.api.Result;
import ru.greenpix.mysql.elements.MysqlTable;
import ru.greenpix.mysql.nbt.MysqlFields;

public class Town implements ICObject, OwnerBuildings {
	
	private final static List<Town> towns = new ArrayList<>();
	
	private final List<Request> requests = new ArrayList<>();
	
	private final List<Building> buildings = new ArrayList<>();
	
	private final List<TradeResource> resources = new ArrayList<>();
	
	private final List<Group> groups = new ArrayList<>();
	
	private final CountMap values = new CountMap();
	
	private Townhall townhall;
	
	private Civilization mother;
	
	private Civilization civilization;
	
	private final int id;
	
	private final Date timestamp;
	
	private String name;
	
	private String owner;
	
	private BuildingProcess process;
	
	private double balance;
	
	private int culture = 0;
	
	public Town(Result result) {
		getTowns().add(this);
		this.id = result.getInt("id");
		this.name = result.getString(TableTowns.TOWN_NAME);
		this.owner = result.getString(TableTowns.CREATOR);
		this.balance = result.getDouble(TableTowns.BALANCE);
		this.mother = Civilization.findById(result.getInt(TableTowns.MOTHER_CIVILIZATION));
		this.civilization = Civilization.findById(result.getInt(TableTowns.CURRENT_CIVILIZATION));
		this.civilization.getTowns().add(this);
		this.timestamp = result.getDate(TableTowns.TIME_CREATION);
		this.townhall = (Townhall) Building.findById(result.getInt(TableTowns.TOWNHALL));
		try {
			double progress = result.getDouble(TableTowns.BUILDING_PROGRESS);
			if(progress < 100) {
				int task = result.getInt(TableTowns.BUILDING_TASK);
				this.process = new BuildingProcess(this, Building.findById(task));
				this.process.setProgress(progress);
				process.start();
			}
		} catch (Throwable e) {
			System.out.println("TOWN BUILD PROCESS = " + getId());
			e.printStackTrace();
		}
		try {
			applyChunkBuffs();
		} catch (Throwable e) {
			System.out.println("APPLY BUFFS = " + getId());
			e.printStackTrace();
		}
	}
	
	public Town(Civilization civ, Player owner, Location location, String name) {
		getTowns().add(this);
		this.id = lastId() + 1;
		this.timestamp = new Date();
		this.owner = owner.getName();
		this.name = name;
		this.civilization = civ;
		this.civilization.getTowns().add(this);
		this.mother = civ;
		//
		new Group(this, Group.GROUP_OF_MEMBERS);
		new Group(this, Group.GROUP_OF_LEADERS);
		new Group(this, Group.GROUP_OF_CRIMINAL);
		this.townhall = buildTownhall(location);
		applyChunkBuffs();
	}
	
	public Townhall buildTownhall(Location l) {
		return (Townhall) build(l, Structure.getByName("townhall"));
	}
	
	public Building build(Location l, Structure structure) {
		return build(l, structure.getDefaultStyle());
	}
	
	public Building build(Location l, Style style) {
		Location loc = l.clone();
		Direction.getByLocation(loc).alignX(loc, style.getClipboard(), false).addZ(loc, 1).add(loc, style.getStructure().getOffset());
		Building b = Building.newInstance(style.getStructure().getBuildingClass(), this, loc, style);
		process = new BuildingProcess(this, b);
		process.prepareTerrain(() -> process.start());
		broadcast("Началось строительство постройки '" + b.getStructure().getDisplayName() + "'.");
		if(b instanceof Wonder) {
			CivCore.broadcast("&e&lГород " + getName() + " начал строительство чуда света '" + b.getStructure().getDisplayName() + "'.");
		}
		return b;
	}
	
	public Group getGroupOfCriminals() {
		return getGroupByName(Group.GROUP_OF_CRIMINAL);
	}
	
	public List<Building> getBuildings() {
		return buildings;
	}
	
	public List<TradeResource> getResources() {
		return resources;
	}
	
	public boolean isBuilding(Building b) {
		return isProcessRunning() && getProcess().getBuilding().equals(b);
	}
	
	public BuildingProcess getProcess() {
		return process;
	}
	
	public boolean isProcessRunning() {
		return getProcess() != null && !getProcess().isCompleted() && getProcess().isRunning();
	}
	
	public boolean isCaptured() {
		return !getMother().equals(getCivilization());
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
		return Tables.getTable(TableTowns.class);
	}

	@Override
	public MysqlFields getSqlRecord() {
		return new MysqlFields().
				put(TableTowns.TOWN_NAME, getName()).
				put(TableTowns.CREATOR, getOwner()).
				put(TableTowns.BALANCE, getBalance()).
				put(TableTowns.MOTHER_CIVILIZATION, getMother().getId()).
				put(TableTowns.CURRENT_CIVILIZATION, getCivilization().getId()).
				put(TableTowns.TOWNHALL, getTownhall().getId()).
				put(TableTowns.BUILDING_TASK, getProcess() == null ? null : getProcess().getBuilding().getId()).
				put(TableTowns.BUILDING_PROGRESS, getProcess() == null ? 100 : getProcess().getProgress()).
				put(TableTowns.TIME_CREATION, getTimestamp());
	}
	
	public void saveBalance() {
		RunnableManager.async(() -> getSqlTable().set(new MysqlFields().put(TableTowns.BALANCE, getBalance()), getSqlWhere()));
	}

	public void capture(Civilization invader) {
		getCivilization().getTowns().remove(this);
		if(getCivilization().getTowns().size() == 0) {
			Civilization.getCivilizations().remove(getCivilization());
			// TODO цива захвачена
		} else if(getCivilization().getCapital().equals(this)) {
			// TODO столица захвачена - новая столица
			Town nc = getCivilization().getTowns().stream()
					.sorted(Comparator.comparing(Town::getGlobalScores))
					.findFirst().get();
			getCivilization().setCapital(nc);
		}
		setCivilization(invader);
		invader.getTowns().add(this);
		getGroupOfMembers().getOnline().forEach(e -> e.asyncUpdateTag());
		RunnableManager.async(() -> writeSql());
	}
	
	public void capitulate(Civilization invader) {
		getCivilization().getTowns().remove(this);
		if(getCivilization().getTowns().size() == 0) {
			Civilization.getCivilizations().remove(getCivilization());
			// TODO цива захвачена
		} else if(getCivilization().getCapital().equals(this)) {
			// TODO столица захвачена - новая столица
			Town nc = getCivilization().getTowns().stream()
					.sorted(Comparator.comparing(Town::getGlobalScores))
					.findFirst().get();
			getCivilization().setCapital(nc);
		}
		setCivilization(invader);
		invader.getTowns().add(this);
		getGroupOfMembers().getOnline().forEach(e -> e.asyncUpdateTag());
		RunnableManager.async(() -> writeSql());
	}
	
	public Townhall getTownhall() {
		return townhall;
	}
	
	public void applyChunkBuffs() {
		BiomeInfo i;
		for(int x = getCenter().getBlockX() - 32; x < getCenter().getBlockX() + 32; x += 4) {
			for(int z = getCenter().getBlockZ() - 32; z < getCenter().getBlockZ() + 32; z += 4) {
				i = BiomeInfo.getByBiome(getCenter().getWorld().getBiome(x, z));
				values.add(Countable.HAMMERS, i.hammers);
				values.add(Countable.GROWTH, i.growth);
				values.add(Countable.HAPPINESS, i.happiness);
				values.add(Countable.BEAKERS, i.beakers);
			}
		}
	}
	
	public CountMap getValues() {
		return values;
	}
	
	@Override
	public double getValue(String type) {
		double add;
		switch (type) {
		case UNHAPPINESS:
			add = 0;
			break;
		default:
			add = 0;
			break;
		}
		return add + values.getOrDefault(type, 0D) +
				buildings.stream().mapToDouble(e -> e.getValue(type)).sum() +
				resources.stream().mapToDouble(e -> e.getValue(type)).sum();
	}
	
	public double getTaxRate() {
		return values.getOrDefault(Economical.TAXRATE, 50D);
	}
	
	public double getHappinessLevel() {
		return 100D * getHappiness() / (getHappiness() + getUnhappiness());
	}
	
	public int getCulture() {
		return culture;
	}
	
	public int getCultureLevel() {
		return Math.max(1, Math.min(10, (int) Math.pow(getCulture(), 1/6)));
	}

	public Civilization getMother() {
		return mother;
	}

	public void setMother(Civilization mother) {
		this.mother = mother;
	}

	public Civilization getCivilization() {
		return civilization;
	}

	public void setCivilization(Civilization civilization) {
		this.civilization = civilization;
	}
	
	public boolean isCapital() {
		return Civilization.getCivilizations().parallelStream().anyMatch(c -> c.getCapital().equals(this));
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}
	
	@Override
	public List<Group> getGroups() {
		return groups;
	}
	
	public void broadcast(String msg) {
		getGroupOfMembers().getOnline().forEach(p -> p.sendMessage(Prefixs.TOWN + msg));
	}

	@Override
	public void broadcast(String msg, String... perms) {
		getPlayersWithPerms(Arrays.asList(perms)).forEach(p -> p.sendMessage(Prefixs.TOWN + msg));
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
	
	public int getBorder() {
		return 100 + (hasBuildings(NotreDame.class) ? Structure.getByClass(NotreDame.class).getInt("buffs.border") : 0);
	}
	
	public boolean hasDebt() {
		return getBalance() < 0;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public static List<Town> getTowns() {
		return towns;
	}
	
	public List<Building> getBuildings(Structure str) {
		return getBuildings().stream().filter(b -> b.getStructure().equals(str)).collect(Collectors.toList());
	}
	
	public List<Building> getBuildings(Class<? extends Building> clazz) {
		return getBuildings().stream().filter(b -> clazz.isInstance(b)).collect(Collectors.toList());
	}
	
	public boolean hasBuildings(Structure str) {
		return getBuildings().stream().anyMatch(b -> b.getStructure().equals(str));
	}
	
	public boolean hasBuildings(Class<? extends Building> clazz) {
		return getBuildings().stream().anyMatch(b -> clazz.isInstance(b));
	}
	
	public Location getCenter() {
		return getTownhall().getCenter();
	}
	
	public Location getHome() {
		return getCenter().add(1, 1, 0);
	}
	
	public void addMember(CivPlayer member) {
		getGroupOfMembers().add(member);
		getCivilization().getGroupOfMembers().add(member);
		member.setHome(this);
		member.asyncUpdateTag();
	}
	
	public void removeMember(String member) {
		getGroupOfMembers().remove(member);
		getCivilization().getGroupOfMembers().remove(member);
		CivPlayer player = CivPlayer.getByName(member);
		if(player != null) {
			player.setHome(null);
			player.asyncUpdateTag();
		}
	}
	
	public DenyReason getReasonBuildDenied(Structure structure) {
		for(Technology tech : structure.getTechnologies()) {
			if(!getCivilization().getTechnologies().contains(tech)) return DenyReason.TECHNOLOGY;
		}
		for(Structure parent : structure.getParents()) {
			if(getBuildings(parent).size() <= 0) return DenyReason.PARENT_BUILDINGS;
		}
		if(structure.isWonder() && Wonder.existsAliveWonder(structure)) return DenyReason.WONDER_LIMIT;
		if(!structure.hasLimit()) return null;
		if(getBuildings(structure).size() >= structure.getLimit()) return DenyReason.TOWN_LIMIT;
		return null;
	}
	
	public static Town getByName(String name) {
		return getTowns().stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public static Town getById(int id) {
		return getTowns().stream().filter(t -> t.getId() == id).findFirst().orElse(null);
	}
	
	public static Town findById(int id) {
		return Stored.findById(id, getTowns(), Tables.getTable(TableTowns.class), r -> new Town(r));
	}
	
	public static Set<Town> getOnlineTowns() {
		return CivPlayer.getPlayers().stream().filter(CivPlayer::hasHome).map(CivPlayer::getTown).collect(Collectors.toSet());
	}
}
