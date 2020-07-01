package ru.greenpix.civilization.clipboard;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BaseBlock {

	private final int x;
	private final int y;
	private final int z;

	private int id;
	private byte data;
	
	public BaseBlock(int id, byte data, Vector v) {
		this.id = id;
		this.data = data;
		this.x = v.getBlockX();
		this.y = v.getBlockY();
		this.z = v.getBlockZ();
	}
	
	@SuppressWarnings("deprecation")
	public Material getType() {
		return Material.getMaterial(id);
	}
	
	public void setId(int id) {
		this.id = id;
		this.data = 0;
	}
	
	public void setData(byte data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public byte getData() {
		return data;
	}
	
	public boolean isLog() {
		return id == 17 || id == 162;
	}
	
	public boolean isStair() {
		return id == 53 || id == 67 || id == 108 || id == 109 || id == 114 || id == 128 || id == 134 ||
				id == 135 || id == 136 || id == 156 || id == 163 || id == 164 || id == 180;
	}
	
	public boolean isTorch() {
		return id == 50 || id == 75 || id == 76;
	}

	public boolean isButton() {
		return id == 143 || id == 77;
	}
	
	public boolean isFenceGate() {
		return id == 107 || id == 183 || id == 184 || id == 185 || id == 186 || id == 187; 
	}
	
	public boolean isDoor() {
		return id == 64 || id == 71 || id == 193 || id == 194 || id == 195 || id == 196 || id == 197;
	}
	
	public boolean isHanging() {
		return isButton() || isTorch() || id == 69; 
	}
	
	public boolean isExtendedRotatable() {
		return id == 33 || id == 29 || id == 158 || id == 23;
	}
	
	public boolean isOldRotatable() {
		return id == 54 || id == 61 || id == 130 || id == 146 || id == 68 || id == 65 || id == 106;
	}
	
	public boolean isTrapDoor() {
		return id == 96 || id == 167;
	}
	
	public boolean isSignPost() {
		return id == 93;
	}
	
	public boolean isRail() {
		return id == 66 || id == 28 || id == 27 || id == 157;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
	
	public Vector getVector() {
		return new Vector(x, y, z);
	}
	
	public BaseBlock rotateRight() {
		if(isStair()) {
			if(data < 4) data = (data == 2 ? (byte) 1 : (data == 1 ? (byte) 3 : (data == 3 ? (byte) 0 : (byte) 2)));
			else data = (data == 6 ? (byte) 5 : (data == 5 ? (byte) 7 : (data == 7 ? (byte) 4 : (byte) 6)));
		}
		else if(isFenceGate()) {
			if(data < 4) data = (data == 3 ? (byte) 0 : (byte) (data + 1)); 
			else data = (data == 7 ? (byte) 4 : (byte) (data + 1)); 
		}
		else if(isHanging()) {
			if(data >= 1 && data <= 4) data = (data == 4 ? (byte) 1 : (data == 1 ? (byte) 3 : (data == 3 ? (byte) 2 : (byte) 4)));
		}
		else if(isOldRotatable()) {
			if(data >= 0 && data <= 5) data = (data == 0 || data == 1 || data == 2 ? (byte) 5 : (data == 5 ? (byte) 3 : (data == 3 ? (byte) 4 : (byte) 2)));
		}
		else if(isExtendedRotatable()) {
			if(data >= 2 && data <= 5) data = (data == 2 ? (byte) 5 : (data == 5 ? (byte) 3 : (data == 3 ? (byte) 4 : (byte) 2)));
		}
		else if(isSignPost()) {
			data += 4;
			if(data > 15) data = (byte) (data - 16);
		}
		else if(isTrapDoor()) {
			if(data > 11) data = (data == 12 ? (byte) 15 : (data == 15 ? (byte) 13 : (data == 13 ? (byte) 14 : (byte) 12)));
			else if(data > 7) data = (data == 8 ? (byte) 11 : (data == 11 ? (byte) 9 : (data == 9 ? (byte) 10 : (byte) 8)));
			else if(data > 3) data = (data == 4 ? (byte) 7 : (data == 7 ? (byte) 5 : (data == 5 ? (byte) 6 : (byte) 4)));
			else data = (data == 0 ? (byte) 3 : (data == 3 ? (byte) 1 : (data == 1 ? (byte) 2 : (byte) 0)));
		}
		else if(isDoor()) {
			if(data > 11) data = (data == 12 ? (byte) 13 : (data == 13 ? (byte) 14 : (data == 14 ? (byte) 15 : (byte) 12)));
			else if(data > 7) data = (data == 8 ? (byte) 9 : (data == 9 ? (byte) 10 : (data == 10 ? (byte) 11 : (byte) 8)));
			else if(data > 3) data = (data == 4 ? (byte) 5 : (data == 5 ? (byte) 6 : (data == 6 ? (byte) 7 : (byte) 4)));
			else data = (data == 0 ? (byte) 1 : (data == 1 ? (byte) 2 : (data == 2 ? (byte) 3 : (byte) 0)));
		}
		else if(isRail()) {
			if(data == 0) data = 1;
			else if(data == 1) data = 0;
			else if(data < 6) data = (data == 5 ? (byte) 3 : (data == 3 ? (byte) 4 : (data == 4 ? (byte) 2 : (byte) 5)));
			else if(data < 10) data = (data == 6 ? (byte) 7 : (data == 7 ? (byte) 8 : (data == 8  ? (byte) 9 : (byte) 6)));
		}
		return this;
	}
	
	@SuppressWarnings("deprecation")
	public void apply(Location l) {
		l.getBlock().setTypeId(id);
		l.getBlock().setData(data);
	}
	
	@SuppressWarnings("deprecation")
	public void send(Player p, Location l) {
		p.sendBlockChange(l, id, data);
	}
}
