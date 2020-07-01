package ru.greenpix.civilization;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.plugin.java.JavaPlugin;

import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.buildings.common.Townhall;
import ru.greenpix.civilization.buildings.wonders.TheColossus;
import ru.greenpix.civilization.buildings.wonders.Wonder;
import ru.greenpix.civilization.commands.CommandAdmin;
import ru.greenpix.civilization.commands.CommandBuild;
import ru.greenpix.civilization.commands.CommandCivilization;
import ru.greenpix.civilization.commands.CommandDiplomacy;
import ru.greenpix.civilization.commands.CommandRecipes;
import ru.greenpix.civilization.commands.CommandTech;
import ru.greenpix.civilization.commands.CommandTown;
import ru.greenpix.civilization.database.Tables;
import ru.greenpix.civilization.database.Tables.TableTowns;
import ru.greenpix.civilization.diplomacy.Diplomacy;
import ru.greenpix.civilization.diplomacy.Relationship;
import ru.greenpix.civilization.diplomacy.Status;
import ru.greenpix.civilization.groups.Group;
import ru.greenpix.civilization.groups.Perms;
import ru.greenpix.civilization.holograms.HologramUtils;
import ru.greenpix.civilization.items.CustomItems;
import ru.greenpix.civilization.listeners.StructureListener;
import ru.greenpix.civilization.listeners.ChatListener;
import ru.greenpix.civilization.listeners.CraftListener;
import ru.greenpix.civilization.listeners.PlayerListener;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.player.Request;
import ru.greenpix.civilization.technologies.Technology;
import ru.greenpix.civilization.utils.LocationUtils;
import ru.greenpix.civilization.utils.RunnableManager;
import ru.greenpix.civilization.utils.StringUtils;
import ru.greenpix.developer.files.Config;
import ru.greenpix.developer.files.ConfigSection;
import ru.greenpix.developer.utils.CommandRegister;
import ru.greenpix.developer.utils.protocol.sounds.FixedSound;
import ru.greenpix.developer.utils.protocol.title.Title;
import ru.greenpix.mysql.api.MysqlAPI;
import ru.greenpix.mysql.elements.MysqlDatabase;
import ru.greenpix.mysql.elements.MysqlTable;

public class CivCore extends JavaPlugin {

	private static CivCore instance;
	
	private Config cfg;
	
	public static CivCore getInstance() {
		return instance;
	}
	
	public static Config getCfg() {
		return instance.cfg;
	}
	
	public static double getGameSpeed() {
		if(!getCfg().contains("gamespeed")) return 1;
		return getCfg().getInt("gamespeed");
	}
	
	public CivCore() {
		instance = this;
		RunnableManager.enable(this);
	}
	
	private boolean checkVersion(String plugin, String version) {
		String currVer = getServer().getPluginManager().getPlugin(plugin).getDescription().getVersion();
		if(StringUtils.getVersion(currVer) < StringUtils.getVersion(version)) {
			getServer().getConsoleSender().sendMessage("§c[" + getName() + "] Plugin requires '" + plugin + "' version " + version + " (You have version " + currVer + ")");
			getServer().getPluginManager().disablePlugin(this);
			return false;
		} 
		return true;
	}
	
	/*
	private boolean checkAccess() {
		String key = cfg.getString("key");
		try {
			URL url = new URL("http://parkourbeat.com/valid?key=" + key);
			InputStream in = new BufferedInputStream(url.openStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			return line != null && line.equalsIgnoreCase("done");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	*/
	
	Debugger debugger;
	
	@Override
	public void onEnable() {
		if(!checkVersion("DeveloperAPI", "1.3.0")) return;
		if(!checkVersion("MysqlAPI", "1.2.2")) return;
		cfg = new Config(this, "config", new File(getDataFolder(), "config.yml"), "config.yml");
		getServer().getWorlds().forEach(w -> w.getEntitiesByClass(AreaEffectCloud.class).forEach(e -> e.remove()));
		CustomItems.initRecipes();
		CustomItems.initAnnotations();
		CommandRegister.register(this, new CommandAdmin(), new String[] {"cdev", "cadmin"});
		CommandRegister.register(this, new CommandCivilization(), new String[] {"civilization", "civ"});
		CommandRegister.register(this, new CommandTown(), new String[] {"town"});
		CommandRegister.register(this, new CommandDiplomacy(), new String[] {"dip", "diplomacy"});
		CommandRegister.register(this, new CommandBuild(), new String[] {"build"});
		CommandRegister.register(this, new CommandTech(), new String[] {"research", "tech"});
		CommandRegister.register(this, new CommandRecipes(), new String[] {"recipe", "rec", "craft", "crafts"});
		registerRequestCommands();
		loadTechnologies();
		loadStructures(new File(getDataFolder(), "structures"));
		loadDatabases();
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new StructureListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new CraftListener(), this);
		getServer().getWorlds().forEach(w -> {
			w.setGameRuleValue("doFireTick", "false");
			w.setGameRuleValue("mobGriefing", "false");
		});
		getServer().getOnlinePlayers().forEach(p -> new CivPlayer(p));
		getServer().getScheduler().runTaskTimer(this, () -> {
			Town.getOnlineTowns().forEach(town -> {
				town.getBuildings().forEach(b -> {
					if(b instanceof Runnable && b.isActive()) ((Runnable) b).run();
				});
			}); 
		}, 100, 1);
		getServer().getScheduler().runTaskTimer(this, () -> {
			Town.getOnlineTowns().forEach(town -> {
				CivPlayer.getPlayers().forEach(p -> {
					if(!p.hasHome()) return;
					if(town.equals(p.townLocation)) {
						if(LocationUtils.distance2D(town.getCenter(), p.getLocation()) > town.getBorder()) {
							p.townLocation = null;
							Title.sendTitle(p.toBukkit(), "&dВы покинули " + town.getCivilization().getName(), 10, 40, 10);
						}
					} else {
						if(LocationUtils.distance2D(town.getCenter(), p.getLocation()) < town.getBorder()) {
							p.townLocation = town;
							Status s = Diplomacy.getRelationship(town.getCivilization(), p.getCivilization()).getStatus();
							if(town.getGroupOfMembers().containsIgnoreCase(p.getName())) {
								Title.sendTitles(p.toBukkit(), "&dГраницы цивилизации " + town.getCivilization().getName(), "&aДобро пожаловать домой =)", 10, 40, 10);
							} else {
								Title.sendTitles(p.toBukkit(), "&dГраницы цивилизации " + town.getCivilization().getName(), "Статус: " + s.color + "&l" + s.getDisplayName().toUpperCase(), 10, 40, 10);
								if(p.toBukkit().getGameMode() == GameMode.SPECTATOR) {
									return;
								}
								town.getCivilization().broadcast("Игрок " + p.getCivilization().getDisplayTag() + s.color + " " + p.getName() + " &l(" + s.getDisplayName().toUpperCase() + ") &fпересек вашу границу!");
							}
						}
					}
				});
			}); 
		}, 20, 20);
		getServer().getScheduler().runTaskTimer(this, () -> {
			Town.getOnlineTowns().forEach(town -> {
				int in = town.getMoneyPerMinute();
				town.deposit(in);
				if(in < 0) {
					town.broadcast("Город задолжал &c" +  + in + " &fмонет за минуту.");	
					town.broadcast("Удалите постройки, у которых большой налог.");
				} else {
					town.broadcast("Город принес прибыль &a" +  + in + " &fмонет за минуту.");	
					town.broadcast("Взять деньги с казны: &e/town withdraw &a[сумма]", Perms.TREASURE_WITHDRAW);	
				}
			});
		}, 100, 60 * 20);
		getServer().getScheduler().runTaskTimer(this, () -> {
			TheColossus w = Wonder.getWonder(TheColossus.class);
			if(w != null) {
				int regen = w.getStructure().getInt("buffs.regen");
				if(w.getTown().getTownhall().regenIfAlive(regen)) {
					w.getTown().broadcast("&aКапитолий восстановил " + regen + " единицу прочности у контрольного блока.");
				}
			}
		}, 100, 60 * 20);
		HologramUtils.registerAllPlaceholders(this);
		try {
			debugger = new Debugger(this, new File(getDataFolder(), "stacktrace.log"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		RunnableManager.enable(this);
		System.gc();
	}
	
	@Override
	public void onDisable() {
		RunnableManager.ENABLED = false;
		Town.getTowns().stream().filter(Town::isProcessRunning).forEach(e -> e.getProcess().writeSql());
		Civilization.getCivilizations().stream().filter(Civilization::isProcessRunning).forEach(e -> e.writeSql());
		if(debugger != null) debugger.close();
	}
	
	public MysqlDatabase setupDatabase(ConfigSection section) throws SQLException {
		ConfigSection s = getCfg().getConfigSection("game_database");
		String type = s.contains("type") ? s.getString("type") : "SQLITE";
		if(type.equalsIgnoreCase("sqlite")) {
			File file = new File(getDataFolder(), s.getString("file"));
			return MysqlAPI.getAPI().connectToSqlite(file);
		} else {
			String host = s.getString("address");
			int port = s.getInt("port");
			String user = s.getString("user");
			String pass = s.getString("password");
			String database = s.getString("database");
			return MysqlAPI.getAPI().connectToDatabase(host, port, user, pass, database);
		}
	}
	
	public void loadDatabases() {
		try {
			Tables.database = setupDatabase(getCfg().getConfigSection("game_database"));
			MysqlAPI.RESULT_OLD_MODE = false;
			MysqlTable tableC = Tables.createTable(Tables.TableCivilizations.class);
			MysqlTable tableT = Tables.createTable(Tables.TableTowns.class);
			MysqlTable tableB = Tables.createTable(Tables.TableBuildings.class);
			MysqlTable tableG = Tables.createTable(Tables.TableGroups.class);
			Tables.createTable(Tables.TableGroupPermissions.class);
			Tables.createTable(Tables.TableGroupMembers.class);
			MysqlTable tableTE = Tables.createTable(Tables.TableTechnologies.class);
			MysqlTable tableD = Tables.createTable(Tables.TableDiplomacy.class);
			tableC.getAll().stream()
			.filter(e -> Civilization.getById(e.getInt("id")) == null)
			.forEach(result -> new Civilization(result));
			tableT.getAll().stream()
			.filter(e -> e.getString(TableTowns.TOWN_NAME) != null && Town.getById(e.getInt("id")) == null)
			.forEach(result -> new Town(result));
			tableB.getAll().stream()
			.filter(e -> Building.getById(e.getInt("id")) == null)
			.forEach(result -> Building.newInstance(result));
			tableG.getAll()
			.forEach(result -> new Group(result).load());
			tableD.getAll()
			.forEach(result -> Diplomacy.getGlobal().add(new Relationship(result)));
			tableTE.getAll()
			.forEach(result -> 
					Civilization.getById(result.getInt(Tables.TableTechnologies.CIVILIZATION))
					.getTechnologies().add(Technology.getByName(
							result.getString(Tables.TableTechnologies.TECHNOLOGY))
					)
			);
			new ArrayList<>(Civilization.getCivilizations()).stream().filter(Civilization::isDestroyed).forEach(e -> Civilization.getCivilizations().remove(e));
			MysqlAPI.RESULT_OLD_MODE = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		/**
		 *  Последовательность загрузки данных из БД
		 *  1) Загрузка цивилизаций (civilizations)
		 *  2) Загрузка городов (towns - post @1)
		 *  3) Загрузка построек (buildings - post @2)
		 *  4) Загрузка столиц (civilizations - post @1 & @2)
		 *  5) Загрузка групп (groups - post @1 & @2)
		 *  6) Загрузка прав групп (group_permissions - post @5)
		 *  7) Загрузка жителей групп (group_members - post @5)
		 *  8) Загрузка технологий (technology - post @1)
		 *  Загрузка во время игрового процесса
		 *  - Дипломатия (diplomacy)
		 *  - 
		 */
	}
	
	public void loadTechnologies() {
		File file = new File(getDataFolder(), "technologies.yml");
		Config cfg = new Config(this, file.getName(), file, "technologies.yml");
		cfg.getKeys().forEach(key -> new Technology(cfg.getSection(key)));
	}
	
	public void loadStructures(File file) {
		Structure structure;
		for(File str : file.listFiles(File::isDirectory)) {
			File cfg = new File(str, "structure.yml");
			if(!cfg.exists()) {
				cfg = new File(str, "options.yml");
				if(!cfg.exists()) {
					loadStructures(str);
					continue;
				}
			}
			structure = new Structure(str.getName(), cfg);
			if(structure.getName().equalsIgnoreCase("townhall")) {
				structure.set("class", Townhall.class.getName());
				structure.set("durability", Math.pow(2, 24) - 1);
			}
			for(File f : str.listFiles(File::isFile)) {
				if(f.getName().endsWith(".schematic") || f.getName().endsWith(".dat")) {
					try {
						new Style(FilenameUtils.removeExtension("default"), structure, f);
						break;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			File schs = new File(str, "schematics");
			if(!schs.exists()) schs = new File(str, "styles");
			if(schs.exists()) {
				for(File sch : schs.listFiles(File::isFile)) {
					try {
						new Style(FilenameUtils.removeExtension(sch.getName()), structure, sch);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			getLogger().info("Structure '" + structure.getName() + "' loaded (" + structure.getStyles().size() + " styles).");
		}
	}
	
	private void registerRequestCommands() {
		CommandRegister.register(this, (s,c,l,a) -> {
			CivPlayer p = CivPlayer.wrap(s);
			if(p == null) return true;
			List<Request> list = p.getRequestsAll();
			if(list.size() == 0) {
				p.sendMessage("&7На данный момент у вас нет входящих запросов.");
			} else if(list.size() == 1) {
				list.get(0).accept();
			} else if(a.length > 0) {
				Request r = list.stream().filter(e -> e.getSender().getName().equalsIgnoreCase(a[0])).findFirst().orElse(null);
				if(r != null) r.accept();
				else {
					p.sendMessage("&cУ вас нет запросов от " + a[0] + "!");
					p.sendMessage("&6Ваши запросы от: " + list.stream().map(e -> "&e" + e.getSender().getName() + "&6").collect(Collectors.toList()));
				}
			} else {
				p.sendMessage("&7У вас несколько входящих запросов! Используйте: &6/accept &e[от кого]");
			}
			return true;
		}, new String[] {"accept"});
		CommandRegister.register(this, (s,c,l,a) -> {
			CivPlayer p = CivPlayer.wrap(s);
			if(p == null) return true;
			List<Request> list = p.getRequestsAll();
			if(list.size() == 0) {
				p.sendMessage("&7На данный момент у вас нет входящих запросов.");
			} else if(list.size() == 1) {
				list.get(0).deny();
			} else if(a.length > 0) {
				Request r = list.stream().filter(e -> e.getSender().getName().equalsIgnoreCase(a[0])).findFirst().orElse(null);
				if(r != null) r.deny();
				else {
					p.sendMessage("&cУ вас нет запросов от " + a[0] + "!");
					p.sendMessage("&cВаши запросы от: " + list.stream().map(e -> "&6" + e.getSender().getName() + "&6").collect(Collectors.toList()));
				}
			} else {
				p.sendMessage("&7У вас несколько входящих запросов! Используйте: &6/deny &e[от кого]");
			}
			return true;
		}, new String[] {"deny"});
	}
	
	public static void broadcastTitle(String title, String sub, int in, int stay, int out) {
		Bukkit.getOnlinePlayers().forEach(p -> Title.sendTitles(p, title, sub, in, stay, out));
	}
	
	public static void broadcastSound(FixedSound sound, float pitch) {
		Bukkit.getOnlinePlayers().forEach(p -> sound.playSound(p, 1f, pitch));
	}
	
	public static void broadcast(String msg) {
		CivPlayer.getPlayers().forEach(p -> p.sendMessage(Prefixs.GLOBAL + msg));
	}
}