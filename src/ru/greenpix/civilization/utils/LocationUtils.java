package ru.greenpix.civilization.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocationUtils {

	public static Location adjustTurretLocation(final Location turretLoc, final Location playerLoc) {
	    final int diff = 2;
	    int xdiff = 0;
	    int zdiff = 0;
	    if (playerLoc.getBlockX() > turretLoc.getBlockX()) {
	        xdiff = diff;
	    }
	    else if (playerLoc.getBlockX() < turretLoc.getBlockX()) {
	        xdiff = -diff;
	    }
	    if (playerLoc.getBlockZ() > turretLoc.getBlockZ()) {
	        zdiff = diff;
	    }
	    else if (playerLoc.getBlockZ() < turretLoc.getBlockZ()) {
	        zdiff = -diff;
	    }
	    return turretLoc.getBlock().getRelative(xdiff, 0, zdiff).getLocation();
	}
	
	public static Location aling(Location l) {
		l.setX(l.getBlockX() + 0.5);
		l.setY(l.getBlockY());
		l.setZ(l.getBlockZ() + 0.5);
		return l;
	}
	
	public static Vector getVectorBetween(final Location to, final Location from) {
	    Vector dir = new Vector();
	    dir.setX(to.getX() - from.getX());
	    dir.setY(to.getY() - from.getY());
	    dir.setZ(to.getZ() - from.getZ());
	    return dir;
	}
	
	public static double distance2D(Location l1, Location l2) {
		l1 = l1.clone();
		l1.setY(l2.getY());
		return l1.distance(l2);
	}
	
	public static Location getDirection(Location start, Location end) {
		double xDiff = start.getX() - end.getX();
        double yDiff = start.getY() - end.getY();
        double zDiff = start.getZ() - end.getZ();
        double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
        double newYaw = Math.acos(xDiff / DistanceXZ) * 180 / Math.PI;
        double newPitch = Math.acos(yDiff / DistanceY) * 180 / Math.PI - 90;
        if (zDiff < 0.0)
            newYaw = newYaw + Math.abs(180 - newYaw) * 2;
        newYaw = (newYaw - 90);
        Location res = start.clone(); 
        res.setYaw((float) newYaw);
        res.setPitch((float) newPitch);
        return res;
	}
}
