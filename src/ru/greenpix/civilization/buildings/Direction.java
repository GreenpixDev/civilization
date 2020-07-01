package ru.greenpix.civilization.buildings;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public enum Direction {
	SOUTH(0) {
		@Override
		public Direction addX(Location loc, double add) {
			loc.add(add, 0, 0);
			return this;
		}
		@Override
		public Direction addZ(Location loc, double add) {
			loc.add(0, 0, add);
			return this;
		}
	},
	NORTH(2) {
		@Override
		public Direction addX(Location loc, double add) {
			loc.add(-add, 0, 0);
			return this;
		}
		@Override
		public Direction addZ(Location loc, double add) {
			loc.add(0, 0, -add);
			return this;
		}
	},
	EAST(3) {
		@Override
		public Direction addX(Location loc, double add) {
			loc.add(0, 0, -add);
			return this;
		}
		@Override
		public Direction addZ(Location loc, double add) {
			loc.add(add, 0, 0);
			return this;
		}
	},
	WEST(1) {
		@Override
		public Direction addX(Location loc, double add) {
			loc.add(0, 0, add);
			return this;
		}
		@Override
		public Direction addZ(Location loc, double add) {
			loc.add(-add, 0, 0);
			return this;
		}
	};

	private final int rotation;
	
	private Direction(int rotation) {
		this.rotation = rotation;
	}
	
	public static Direction getById(int id) {
		if(id > 3 || id < 0) return null;
		for(Direction d : values()) {
			if(d.getRotation() == id) return d;
		}
		return null;
	}
	
	public static Direction getByLocation(Location loc) {
		float yaw = toDegree(loc.getYaw());
		if(yaw > 315 || yaw < 45) return SOUTH;
		else if(yaw > 45 && yaw < 135) return WEST;
		else if(yaw > 135 && yaw < 225) return NORTH;
		else return EAST;
	}
	
	public int getRotation() {
		return rotation;
	}
	
	public abstract Direction addX(Location loc, double add);
	
	public abstract Direction addZ(Location loc, double add);
	
	public Direction add(Location loc, double x, double y, double z) {
		loc.add(0, y, 0);
		return addX(loc, x).addZ(loc, z);
	}
	
	public Direction add(Location loc, Vector v) {
		return add(loc, v.getX(), v.getY(), v.getZ());
	}
	
	public Direction alignX(Location loc, Cuboid cuboid, boolean increase) {
		if(this == SOUTH || this == NORTH) {
			addX(loc, cuboid.getWidth() / (increase ? 2 : -2));
		} else {
			addX(loc, cuboid.getLength() / (increase ? 2 : -2));
		}
		return this;
	}
	
	public Direction alignZ(Location loc, Cuboid cuboid, boolean increase) {
		if(this == SOUTH || this == NORTH) {
			addZ(loc, cuboid.getLength() / (increase ? 2 : -2));
		} else {
			addZ(loc, cuboid.getWidth() / (increase ? 2 : -2));
		}
		return this;
	}
	
	public Direction inversion() {
		return getById(getRotationInversion());
	}
	
	public Direction getRight() {
		return getById(getRotationRight());
	}
	
	public Direction getLeft() {
		return getById(getRotationLeft());
	}
	
	public byte getStairData() {
		if(this == WEST) return 1;
		if(this == SOUTH) return 2;
		if(this == NORTH) return 3;
		return 0;
	}
	
	public int getRotationInversion() {
		int i = getRotation() + 2;
		if(i > 3) {
			i -= 4;
		}
		return i;
	}
	
	public int getRotationRight() {
		int i = getRotation() + 1;
		if(i > 3) {
			i -= 4;
		}
		return i;
	}
	
	public int getRotationLeft() {
		int i = getRotation() + 3;
		if(i > 3) {
			i -= 4;
		}
		return i;
	}
	
	private static float toDegree(float y){
		if(y <= 360 && y >= 0) return y;
		while(!(y <= 360) || !(y >= 0)){
			if(y > 360){
				y = y - 360;
			}
			if(y < 0){
				y = y + 360;
			}
		}
		return y;
	}	
}
