package ru.greenpix.civilization.buildings;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;

import ru.greenpix.civilization.clipboard.Clipboard;

public class Region implements Iterable<Block>, Cuboid {
	
	private final Location pos1;
	private final Location pos2;
	
	public Region(Location loc, Clipboard clipboard, int rotate) {
		this(loc, clipboard.getWidth(), clipboard.getHeight(), clipboard.getLength(), rotate);
	}
	
	public Region(Location pos1, Location pos2) {
		this(pos1.getWorld(), pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ(), pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ());
	}
	
	public Region(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.pos1 = new Location(world, Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
		this.pos2 = new Location(world, Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
	}
	
	public Region(Location loc, int width, int height, int length, int rotate) {
		loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		switch (rotate) {
		case 0:
			pos1 = loc;
			pos2 = pos1.clone().add(width - 1, height - 1, length - 1);
			break;
		case 1:
			pos1 = loc.clone().add(-length + 1, 0, 0);
			pos2 = pos1.clone().add(length - 1, height - 1, width - 1);
			break;
		case 2:
			pos1 = loc.clone().add(-width + 1, 0, -length + 1);
			pos2 = pos1.clone().add(width - 1, height - 1, length - 1);
			break;
		case 3:
			pos1 = loc.clone().add(0, 0, -width + 1);
			pos2 = pos1.clone().add(length - 1, height - 1, width - 1);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public int size() {
		return getHeight() * getLength() * getWidth();
	}
	
	public World getWorld() {
		return pos1.getWorld();
	}
	
	public Location getPos1() {
		return pos1;
	}
	
	public Location getPos2() {
		return pos2;
	}
	
	public boolean contains2D(Location l) {
		return getWorld().equals(l.getWorld()) &&
				l.getBlockX() >= getPos1().getBlockX() &&
				l.getBlockZ() >= getPos1().getBlockZ() &&
				l.getBlockX() <= getPos2().getBlockX() &&
				l.getBlockZ() <= getPos2().getBlockZ();
	}
	
	public boolean contains2D(Block b) {
		return contains2D(b.getLocation());
	}
	
	public boolean contains(Location l) {
		return getWorld().equals(l.getWorld()) &&
				l.getBlockX() >= getPos1().getBlockX() &&
				l.getBlockY() >= getPos1().getBlockY() &&
				l.getBlockZ() >= getPos1().getBlockZ() &&
				l.getBlockX() <= getPos2().getBlockX() &&
				l.getBlockY() <= getPos2().getBlockY() &&
				l.getBlockZ() <= getPos2().getBlockZ();
	}
	
	public boolean contains(Block b) {
		return contains(b.getLocation());
	}
	
	public boolean contains(Region region) {
		return contains(region.getPos1()) && contains(region.getPos2());
	}
	
	public boolean concerns(Region region) {
		Location a1 = getPos1();
		Location a2 = getPos2();
		Location b1 = region.getPos1();
		Location b2 = region.getPos2();
		return region.getWorld().equals(getWorld()) && (
				((b1.getBlockX() >= a1.getBlockX() && b1.getBlockX() <= a2.getBlockX()) || 
						(b2.getBlockX() >= a1.getBlockX() && b2.getBlockX() <= a2.getBlockX())) &&
				((b1.getBlockY() >= a1.getBlockY() && b1.getBlockY() <= a2.getBlockY()) || 
						(b2.getBlockY() >= a1.getBlockY() && b2.getBlockY() <= a2.getBlockY())) &&
				((b1.getBlockZ() >= a1.getBlockZ() && b1.getBlockZ() <= a2.getBlockZ()) || 
						(b2.getBlockZ() >= a1.getBlockZ() && b2.getBlockZ() <= a2.getBlockZ()))
		) || (
				((a1.getBlockX() >= b1.getBlockX() && a1.getBlockX() <= b2.getBlockX()) || 
						(a2.getBlockX() >= b1.getBlockX() && a2.getBlockX() <= b2.getBlockX())) &&
				((a1.getBlockY() >= b1.getBlockY() && a1.getBlockY() <= b2.getBlockY()) || 
						(a2.getBlockY() >= b1.getBlockY() && a2.getBlockY() <= b2.getBlockY())) &&
				((a1.getBlockZ() >= b1.getBlockZ() && a1.getBlockZ() <= b2.getBlockZ()) || 
						(a2.getBlockZ() >= b1.getBlockZ() && a2.getBlockZ() <= b2.getBlockZ()))
		);		
	}
	
	public Region to2D() {
		return new Region(getWorld(), getPos1().getBlockX(), 0,  getPos1().getBlockZ(), getPos2().getBlockX(), 0, getPos1().getBlockZ());
	}

	public boolean contains2D(Region region) {
		return to2D().contains(region.to2D());
	}
	
	public boolean concerns2D(Region region) {
		Location a1 = getPos1();
		Location a2 = getPos2();
		Location b1 = region.getPos1();
		Location b2 = region.getPos2();
		return region.getWorld().equals(getWorld()) && (
				((b1.getBlockX() >= a1.getBlockX() && b1.getBlockX() <= a2.getBlockX()) || 
						(b2.getBlockX() >= a1.getBlockX() && b2.getBlockX() <= a2.getBlockX())) &&
				((b1.getBlockZ() >= a1.getBlockZ() && b1.getBlockZ() <= a2.getBlockZ()) || 
						(b2.getBlockZ() >= a1.getBlockZ() && b2.getBlockZ() <= a2.getBlockZ()))
		) || (
				((a1.getBlockX() >= b1.getBlockX() && a1.getBlockX() <= b2.getBlockX()) || 
						(a2.getBlockX() >= b1.getBlockX() && a2.getBlockX() <= b2.getBlockX())) &&
				((a1.getBlockZ() >= b1.getBlockZ() && a1.getBlockZ() <= b2.getBlockZ()) || 
						(a2.getBlockZ() >= b1.getBlockZ() && a2.getBlockZ() <= b2.getBlockZ()))
		);
	}

	public int getWidth() {
		return getPos2().getBlockX() - getPos1().getBlockX() + 1;
	}

	public int getHeight() {
		return getPos2().getBlockY() - getPos1().getBlockY() + 1;
	}

	public int getLength() {
		return getPos2().getBlockZ() - getPos1().getBlockZ() + 1;
	}
	
	public RegionIterator iterator() {
		return new RegionIterator();
	}
	
	class RegionIterator implements Iterator<Block> {

		int x = getPos1().getBlockX();
		int y = getPos1().getBlockY();
		int z = getPos1().getBlockZ();
		
		@Override
		public boolean hasNext() {
			return z <= getPos2().getBlockZ();
		}

		@Override
		public Block next() {
			Block e = new Location(getWorld(), x, y, z).getBlock();
			x++;
			if(x == getPos2().getBlockX() + 1) {
				x = getPos1().getBlockX();
				y++;
				if(y == getPos2().getBlockY() + 1) {
					y = getPos1().getBlockY();
					z++;
				}
			}
			return e;
		}
	}
	
	public static Region getWorldBorder(World world) {
		WorldBorder wb = world.getWorldBorder();
		double s = wb.getSize() / 2D;
		Location pos1 = wb.getCenter().clone().add(s, 255, s);
		Location pos2 = wb.getCenter().clone().add(-s, 0, -s);
		return new Region(pos1, pos2);
	}
}
