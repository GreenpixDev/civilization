package ru.greenpix.civilization.buildings;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.math.transform.Transform;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.clipboard.Clipboard;
import ru.greenpix.civilization.database.SafeId;
import ru.greenpix.civilization.database.Stored;
import ru.greenpix.civilization.database.Tables;
import ru.greenpix.civilization.database.Tables.TableBuildings;
import ru.greenpix.civilization.holograms.UpdateHologram;
import ru.greenpix.civilization.objects.Countable;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.utils.RunnableManager;
import ru.greenpix.civilization.utils.StringUtils;
import ru.greenpix.civilization.utils.WorldEditUtils;
import ru.greenpix.developer.Placeholder;
import ru.greenpix.developer.utils.protocol.sounds.FixedSound;
import ru.greenpix.developer.utils.protocol.title.Title;
import ru.greenpix.mysql.api.Result;
import ru.greenpix.mysql.elements.MysqlTable;
import ru.greenpix.mysql.nbt.MysqlFields;

public class Building implements Stored, Countable {

	private final static List<Building> buildings = new ArrayList<>();
	
	private final Map<String, Double> values = new HashMap<String, Double>();
	
	private final UpdateHologram hologram;
	
	private final int id;
	
	private int durability;
	
	private int rotation;
	
	private Town town;
	
	private Style style;
	
	private Location location;
	
	private Region region;
	
	private Date timestamp;
	
	public Building(Result result, Structure str) {
		 getBuildings().add(this);
		 this.id = result.getInt("id");
		 this.location = new Location(Bukkit.getWorld(result.getString(TableBuildings.WORLD)), 
				 result.getInt(TableBuildings.LOCATION_X),
				 result.getInt(TableBuildings.LOCATION_Y), 
				 result.getInt(TableBuildings.LOCATION_Z));
		 this.style = str.getStyle(result.getString(TableBuildings.STYLE));
		 this.town = Town.getById(result.getInt(TableBuildings.TOWN));
		 this.durability = result.getInt(TableBuildings.DURABILITY);
		 this.rotation = (int) result.getByte(TableBuildings.ROTATION);
		 this.timestamp = result.getDate(TableBuildings.TIME_CREATION);
		 this.region = new Region(getLocation(), getClipboard(), getRotation());
		 this.hologram = createHologram();
		 try {
			 town.getBuildings().add(this);
		 } catch (Throwable e) {
			 System.out.println("ID " + id);
			 e.printStackTrace();
		 }
		 if(this instanceof GuiseBuilding) hologram.setTouchHandler(p -> {
			 if(getTown().equals(CivPlayer.wrap(p).getTown())) {
				 ((GuiseBuilding) this).getGuise().open(p);
			 }
		 });
	}
	
	@SafeId
	public Building(Town town, Location location, Style style) {
		this.location = location;
		this.style = style;
		this.town = town;
		this.durability = style.getStructure().getDurability();
		this.rotation = Direction.getByLocation(location).getRotation();
		this.timestamp = new Date();
		this.region = new Region(getLocation(), getClipboard(), getRotation());
		this.hologram = createHologram();
		//
		writeSql();
		this.id = lastId();
		getBuildings().add(this);
		town.getBuildings().add(this);
		//
		if(this instanceof GuiseBuilding) hologram.setTouchHandler(p -> {
			if(getTown().equals(CivPlayer.wrap(p).getTown())) {
				((GuiseBuilding) this).getGuise().open(p);
			}
		});
	}
	
	private UpdateHologram createHologram() {
		return getStructure().createHologram(getHologramLocation(), ArrayUtils.addAll(getPlaceholders(), 
				new Placeholder("%structure%", getStructure().getDisplayName()), 
				new Placeholder("%durability%", () -> getDurability() + "/" + getMaxDurability())));
	}
	
	public String getDisplayName() {
		return getStructure().getDisplayName();
	}
	
	public void remove() {
		if(isBuilding()) getTown().getProcess().cancel();
		buildings.remove(this);
		getTown().getBuildings().remove(this);
		getHologram().remove();
		RunnableManager.async(() -> deleteSql());
		File file = new File(CivCore.getInstance().getDataFolder() + "/terrain", getId() + ".schematic");
		if(file.exists() && CivCore.getCfg().getBoolean("terrain_save")) {
			try {
				com.sk89q.worldedit.world.World world = WorldEditUtils.toWorld(getWorld());		
				com.sk89q.worldedit.Vector pos = WorldEditUtils.toVector(getRegion().getPos1());
				ClipboardFormats.findByFile(file).load(file).paste(world, pos, false, true, (Transform) null);
				file.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				com.sk89q.worldedit.regions.Region region = new CuboidSelection(getWorld(), getRegion().getPos1(), getRegion().getPos2()).getRegionSelector().getRegion();
				region.getWorld().regenerate(region, WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1));
			} catch (IncompleteRegionException e) {
				e.printStackTrace();
			}
		}
	}
	
	public World getWorld() {
		return getRegion().getWorld();
	}
	
	public boolean isBuilding() {
		return getTown().isBuilding(this);
	}
	
	public boolean isActive() {
		return !isDestroyed() && !isBuilding();
	}
	
	public Map<String, Double> getValues() {
		return values;
	}
	
	@Override
	public double getValue(String type) {
		return isActive() ? values.getOrDefault(type, 0D) + getStructure().getValue(type) : 0;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public MysqlTable getSqlTable() {
		return Tables.getTable(TableBuildings.class);
	}

	@Override
	public MysqlFields getSqlRecord() {
		return new MysqlFields()
				.put(TableBuildings.STRUCTURE, getStyle().getStructure().getName())
				.put(TableBuildings.STYLE, getStyle().getName())
				.put(TableBuildings.TOWN, getTown().getId())
				.put(TableBuildings.LOCATION_X, getLocation().getBlockX())
				.put(TableBuildings.LOCATION_Y, getLocation().getBlockY())
				.put(TableBuildings.LOCATION_Z, getLocation().getBlockZ())
				.put(TableBuildings.ROTATION, getRotation())
				.put(TableBuildings.DURABILITY, getDurability())
				.put(TableBuildings.WORLD, getRegion().getWorld().getName())
				.put(TableBuildings.TIME_CREATION, getTimestamp());
	}
	
	public Location getHologramLocation() {
		if(!getStructure().contains("hologram.offsetX") && !getStructure().contains("hologram.offsetY") && !getStructure().contains("hologram.offsetZ")) {
			return getCenter().add(0, 3, 0);
		}
		return getRegion().getPos1().clone().add(0.5, 0, 0.5).add(getClipboard().rotateVector(getStructure().getHologramOffset(), getRotation()));
	}
	
	public Placeholder[] getPlaceholders() {
		return new Placeholder[0];
	}
	
	public int getMaxDurability() {
		return getStructure().getDurability();
	}
	
	public int getDurability() {
		return durability;
	}
	
	public void setDurability(int d) {
		this.durability = d;
		RunnableManager.async(() -> getSqlTable().set(new MysqlFields().put(TableBuildings.DURABILITY, getDurability()), getSqlWhere()));
	}
	
	public Location getLocation(Vector offset) {
		return getRegion().getPos1().clone().add(getClipboard().rotateVector(offset, getRotation()));
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isRegion(Building b) {
		return getRegion().concerns(b.getRegion());
	}
	
	public boolean isRegion(Location l) {
		return getRegion().contains(l);
	}
	
	public boolean isRegion2D(Building b) {
		return getRegion().concerns2D(b.getRegion());
	}
	
	public boolean isRegion2D(Location l) {
		return getRegion().contains2D(l);
	}
	
	public boolean inhereBuilding(Location l) {
		Vector vector = l.subtract(getRegion().getPos1()).toVector();
		vector = Clipboard.rotateVector(getRegion().getWidth(), getRegion().getLength(), vector, -getRotation());
		return getClipboard().hasBlock(vector);
	}

	public Clipboard getClipboard() {
		return style.getClipboard();
	}

	public Structure getStructure() {
		return style.getStructure();
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}
	
	public Location getCenter() {
		Location center = getLocation().clone();
		getDirection().alignX(center, getRegion(), true).alignZ(center, getRegion(), true);
		return center;
	}
	
	public static <T extends Building> T newInstance(Class<T> clazz, Town town, Location location, Style style) {
		try {
			return clazz.getConstructor(Town.class, Location.class, Style.class).newInstance(town, location, style);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Building newInstance(Result result) {
		Structure str = Structure.getByName(result.getString("structure"));
		try {
			return str.getBuildingClass().getConstructor(Result.class, Structure.class).newInstance(result, str);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void damage(CivPlayer damager, Block block) {
		durability--;
		damager.sendMessage("&7" + getDurability() + "/" + getMaxDurability() + " прочности");
		RunnableManager.async(() -> getSqlTable().set(new MysqlFields().put(TableBuildings.DURABILITY, durability), getSqlWhere()));
		if(durability == 0) {
			CivCore.broadcast("Постройка '" + getStructure().getDisplayName() + "' была разрушена в городе &b" + getTown().getName() + "&f!");
			FixedSound.EXPLODE.playSound(block.getLocation(), 2f, .5f);
			FixedSound.EXPLODE.playSound(block.getLocation(), 2f, 1f);
			getWorld().playEffect(block.getLocation(), Effect.EXPLOSION_HUGE, 100);
			destroy();
			getTown().getGroupOfMembers().getOnline().forEach(e -> {
				Title.sendTitle(e.toBukkit(), "&cВашу постройку '" + getDisplayName() + "' разрушили!", 10, 60, 10);
				e.asyncUpdateTag();
			});
		} else {
			getWorld().playEffect(block.getLocation(), Effect.FLAME, 10);
			FixedSound.ANVIL_USE.playSound(block.getLocation(), 2f, 1.2f);
			getTown().broadcast("&6&lВашу постройку '" + getDisplayName() + "' &c&lАТТАКУЮТ&6&l!\nКоординаты: &c&l" + StringUtils.formatLocation(block.getLocation()));
			damager.toBukkit().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 1));
		}
	}
	
	public void destroy() {
		durability = 0;
		if(isBuilding()) getTown().getProcess().cancel();
		RunnableManager.runGradually(getRegion().iterator(), b -> {
			Material type = b.getType();
			if(type == Material.AIR) return;
			if(type == Material.BEDROCK) {
				b.setType(Material.AIR);
				return;
			}
			double r = Math.random();
			if(r > 0.5) {
				b.setType(r > 0.75 ? Material.GRAVEL : Material.FIRE);
			}
		}, 1L, 1024);
	}
	
	public boolean isDestroyed() {
		return durability == 0;
	}

	public int getRotation() {
		return rotation;
	}
	
	public Direction getDirection() {
		return Direction.getById(rotation);
	}

	public Region getRegion() {
		return region;
	}

	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}

	public static List<Building> getBuildings() {
		return buildings;
	}
	
	public static Building getById(int id) {
		return getBuildings().stream().filter(b -> b.getId() == id).findFirst().orElse(null);
	}
	
	public static Building findById(int id) {
		return Stored.findById(id, getBuildings(), Tables.getTable(TableBuildings.class), r -> newInstance(r));
	}

	public UpdateHologram getHologram() {
		return hologram;
	}
}
