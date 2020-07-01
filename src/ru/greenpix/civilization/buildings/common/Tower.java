package ru.greenpix.civilization.buildings.common;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.diplomacy.Diplomacy;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.utils.LocationUtils;
import ru.greenpix.mysql.api.Result;

public class Tower extends Building {

	public static final Vector[] CONTROL_BLOCKS = new Vector[8];
	
	{
		CONTROL_BLOCKS[0] = new Vector(5, 30, 1);
		CONTROL_BLOCKS[1] = new Vector(5, 30, 10);
		CONTROL_BLOCKS[2] = new Vector(6, 30, 1);
		CONTROL_BLOCKS[3] = new Vector(6, 30, 10);
		CONTROL_BLOCKS[4] = new Vector(1, 30, 5);
		CONTROL_BLOCKS[5] = new Vector(10, 30, 5);
		CONTROL_BLOCKS[6] = new Vector(1, 30, 6);
		CONTROL_BLOCKS[7] = new Vector(10, 30, 6);
	}
	
	private BukkitTask task;
	
	public Tower(Town town, Location location, Style style) {
		super(town, location, style);
		task = Bukkit.getScheduler().runTaskTimer(CivCore.getInstance(), () -> run(), 100, 20);
	}

	public Tower(Result result, Structure str) {
		super(result, str);
		task = Bukkit.getScheduler().runTaskTimer(CivCore.getInstance(), () -> run(), 100, 20);
	}
	
	public BukkitTask getTask() {
		return task;
	}
	
	public Block[] getBlocks() {
		Block[] blocks = new Block[6];
		for(int i = 0; i < 6; i++) {
			blocks[i] = getLocation(CONTROL_BLOCKS[i]).getBlock();
		}
		return blocks;
	}
	
	public void run() {
		if(isActive()) {
			CivPlayer.getPlayers().forEach(p -> {
				if((p.toBukkit().getGameMode() == GameMode.SURVIVAL || p.toBukkit().getGameMode() == GameMode.ADVENTURE) && 
						p.getLocation().getWorld().equals(getWorld()) && LocationUtils.distance2D(p.getLocation(), getCenter()) < 75) {
					Civilization civ = getTown().getCivilization();
					if(p.getCivilization() != null && Diplomacy.getRelationship(getTown().getCivilization(), p.getCivilization()).isAggressive()) {
						for(Block b : getBlocks()) {
							Location playerLoc = p.getLocation();
					        playerLoc.setY(playerLoc.getY() + 1.0);
					        Location turretLoc = LocationUtils.adjustTurretLocation(b.getLocation(), playerLoc);
					        Vector dir = LocationUtils.getVectorBetween(playerLoc, turretLoc).normalize();
					        float power = 5F;
					        Arrow arrow = b.getWorld().spawnArrow(turretLoc, dir, power, 0.0F);
					        arrow.setMetadata("Civ", new FixedMetadataValue(CivCore.getInstance(), civ.getName()));
					        arrow.setVelocity(dir.multiply(power));
						}
					}
				}
			});
		}
	}
}
