package ru.greenpix.civilization.processes;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;

import com.boydti.fawe.object.FaweQueue.ProgressType;
import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.Region;
import ru.greenpix.civilization.buildings.wonders.Wonder;
import ru.greenpix.civilization.clipboard.BaseBlock;
import ru.greenpix.civilization.clipboard.CachedClipboard;
import ru.greenpix.civilization.clipboard.Clipboard;
import ru.greenpix.civilization.database.Tables.TableTowns;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.utils.FaweUtils;
import ru.greenpix.civilization.utils.RunnableManager;
import ru.greenpix.developer.utils.protocol.sounds.FixedSound;
import ru.greenpix.mysql.elements.MysqlTable;
import ru.greenpix.mysql.nbt.MysqlFields;

public class BuildingProcess extends GameProcess {

	private final static int[] UNSTABLE_BLOCKS = new int[]{
			26, 50, 65, 75, 76, 63, 64, 68, 69, 71, 77, 90, 96, 106, 127, 131, 143
	};
	
	private int blocks = 0;
	
	private final Town town;
	
	private final Building building;
	
	public BuildingProcess(Town town, Building building) {
		this.town = town;
		this.building = building;
	}
	
	@Override
	public boolean onUpdate() {
		if(getClipboard() instanceof CachedClipboard && !((CachedClipboard) getClipboard()).isCached()) {
			((CachedClipboard) getClipboard()).cache();
			return false;
		}
		int past = blocks;
		blocks = (int) (getCurrentValue() * getTotalBlocks() / getMaxValue());
		boolean played = false;
		Location l;
		BaseBlock b;
		for(int i = past; i < Math.min(blocks, getTotalBlocks()); i++) {
			b = getClipboard().rotateBlock(getClipboard().getBlock(getClipboard().vectorOf(i)), building.getRotation());
			if(isUnstableBlock(b.getId())) continue;
			l = getRegion().getPos1().clone().add(getClipboard().rotateVector(b.getVector(), building.getRotation()));
			b.apply(l);
			if(!played && b.getId() != 0) {
				played = true;
				l.getWorld().playEffect(l, Effect.STEP_SOUND, b.getId());
			}
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void prepareTerrain(Runnable done) {
		if(CivCore.getCfg().getBoolean("terrain_save")) {
			try {
				File file = new File(CivCore.getInstance().getDataFolder() + "/terrain", getBuilding().getId() + ".schematic");
				com.sk89q.worldedit.regions.Region region = new CuboidSelection(getRegion().getWorld(), getRegion().getPos1(), getRegion().getPos2()).getRegionSelector().getRegion();
				Schematic schem = new Schematic(region);
				schem.save(file, ClipboardFormat.SCHEMATIC);
			} catch (IncompleteRegionException | IOException e) {
				e.printStackTrace();
			}
		}
		EditSession session = new EditSessionBuilder(getRegion().getWorld().getName()).build();
		RunnableManager.async(() -> {
			for(int x = getRegion().getPos1().getBlockX(); x <= getRegion().getPos2().getBlockX(); x++) {
				for(int y = getRegion().getPos1().getBlockY(); y <= getRegion().getPos2().getBlockY(); y++) {
					for(int z = getRegion().getPos1().getBlockZ(); z <= getRegion().getPos2().getBlockZ(); z++) {
						try {
							if(y == getRegion().getPos1().getBlockY()) {
								session.setBlock(new Vector(x, y, z), new com.sk89q.worldedit.blocks.BaseBlock(7));
							} else if(y == getRegion().getPos2().getBlockY()) {
								if(x == getRegion().getPos1().getBlockX() || z == getRegion().getPos1().getBlockZ() ||
										x == getRegion().getPos2().getBlockX() || z == getRegion().getPos2().getBlockZ()) {
									session.setBlock(new Vector(x, y, z), new com.sk89q.worldedit.blocks.BaseBlock(7));
								} else {
									session.setBlock(new Vector(x, y, z), new com.sk89q.worldedit.blocks.BaseBlock(0));
								}
							} else {
								if((x == getRegion().getPos1().getBlockX() && z == getRegion().getPos1().getBlockZ()) ||
										(x == getRegion().getPos1().getBlockX() && z == getRegion().getPos2().getBlockZ()) ||
										(x == getRegion().getPos2().getBlockX() && z == getRegion().getPos1().getBlockZ()) ||
										(x == getRegion().getPos2().getBlockX() && z == getRegion().getPos2().getBlockZ())) {
									session.setBlock(new Vector(x, y, z), new com.sk89q.worldedit.blocks.BaseBlock(7));
								} else {
									session.setBlock(new Vector(x, y, z), new com.sk89q.worldedit.blocks.BaseBlock(0));
								}
							}
						} catch (WorldEditException e) {
							e.printStackTrace();
						}
					}
				}
			}
			RunnableManager.sync(() -> {
				session.flushQueue();
				FaweUtils.setProgressTracker(session.getQueue(), (type, i) -> {
					if(type == ProgressType.DONE) {
						FaweUtils.setProgressTracker(session.getQueue(), (type2, i2) -> {});
						done.run();
					}
				});
			});
		});
	}

	@Override
	public void onComplete() {
		RunnableManager.runGradually(getClipboard().iterator(), b -> {
			if(!isUnstableBlock(b.getId())) return;
			getClipboard().rotateBlock(b, building.getRotation()).apply(getRegion().getPos1().clone().add(getClipboard().rotateVector(b.getVector(), building.getRotation())));
		}, 1, 1024);
		CivCore.broadcast("Город " + getTown().getName() + " завершил строительство постройки '" + getBuilding().getStructure().getDisplayName() + "'.");
		getTown().broadcast("&aСтроительство постройки &2'" + getBuilding().getStructure().getDisplayName() + "'&a завершено на 100%!");
		if(getBuilding() instanceof Wonder) {
			CivCore.broadcastTitle("&l'" + getBuilding().getDisplayName() + " построен!", "В городе: &e" + getTown().getName(), 10, 80, 10);
			Bukkit.getOnlinePlayers().forEach(p -> FixedSound.ENDERDRAGON_DEATH.playSound(p, 5f, 2f));
		}
	}

	@Override
	public double perTick() {
		return town.getHammersPerMinute() / 1200D;
	}

	@Override
	public double getMaxValue() {
		return building.getStructure().getHammers();
	}
	
	@Override
	public void setProgress(double progress) {
		super.setProgress(progress);
		if(isCompleted()) this.blocks = getTotalBlocks();
		else this.blocks = Math.max(0, (int) (progress * getTotalBlocks() / 100D - perTick() * (20 / delay)));
	}
	
	public static boolean isUnstableBlock(int id) {
		for(int i : UNSTABLE_BLOCKS) {
			if(id == i) return true;
		}
		return false;
	}

	public Town getTown() {
		return town;
	}

	public Building getBuilding() {
		return building;
	}
	
	public Clipboard getClipboard() {
		return building.getClipboard();
	}
	
	public Region getRegion() {
		return building.getRegion();
	}
	
	public int getAddedBlocks() {
		return blocks;
	}
	
	public int getTotalBlocks() {
		return building.getClipboard().size();
	}

	@Override
	public int getId() {
		return getTown().getId();
	}

	@Override
	public MysqlTable getSqlTable() {
		return getTown().getSqlTable();
	}

	@Override
	public MysqlFields getSqlRecord() {
		return new MysqlFields()
				.put(TableTowns.BUILDING_PROGRESS, isCompleted() ? 100 : getProgress())
				.put(TableTowns.BUILDING_TASK, isCompleted() ? null : getBuilding().getId());
	}
}
