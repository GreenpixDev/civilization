package ru.greenpix.civilization.utils;

import org.bukkit.Location;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;

import ru.greenpix.civilization.buildings.Region;

public class WorldEditUtils {

	public static CuboidRegion toRegion(Region region) {
		return new CuboidRegion(toVector(region.getPos1()), toVector(region.getPos2()));
	}
	
	public static Vector toVector(org.bukkit.util.Vector vector) {
		return new Vector(vector.getX(), vector.getY(), vector.getZ());
	}
	
	public static Vector toVector(Location location) {
		return new Vector(location.getX(), location.getY(), location.getZ());
	}
	
	public static World toWorld(org.bukkit.World world) {
		return FaweAPI.getWorld(world.getName());
	}
	
}
