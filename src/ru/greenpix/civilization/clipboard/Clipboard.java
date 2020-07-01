package ru.greenpix.civilization.clipboard;

import java.util.Iterator;

import org.bukkit.util.Vector;

import ru.greenpix.civilization.buildings.Cuboid;

public interface Clipboard extends Iterable<BaseBlock>, Cuboid {

	public int size();
	
	public int area();
	
	public int getWidth();
	
	public int getHeight();
	
	public int getLength();
	
	public int getBlockId(int x, int y, int z);
	
	public byte getBlockData(int x, int y, int z);
	
	public BaseBlock getBlock(int x, int y, int z);
	
	public void setBlock(int x, int y, int z, int id, byte data);
	
	public boolean hasBlock(int x, int y, int z);
	
	public boolean hasBlock(int x, int y, int z, int rotate);
	
	public Vector rotateVector(double x, double y, double z, int rotate);
	
	public int getBlockId(Vector v);
	
	public byte getBlockData(Vector v);
	
	public BaseBlock getBlock(Vector v);
	
	public void setBlock(Vector v, int id, byte data);
	
	public boolean hasBlock(Vector v);
	
	public boolean hasBlock(Vector v, int rotate);
	
	public Vector rotateVector(Vector v, int rotate);
	
	public BaseBlock rotateBlock(BaseBlock b, int rotate);
	
	public Iterator<BaseBlock> iterator();
	
	public Iterator<BaseBlock> iterator(int rotate);
	
	public Vector vectorOf(int index);
	
	public static Vector rotateVector(int width, int length, double x, double y, double z, int rotate) {
		if(rotate > 3 || rotate < -3) rotate -= (rotate / 4 * 4);
		if(rotate < 0) rotate += 4;
		switch (rotate) {
		case 1:
			return new Vector(length - 1 - z, y, x);
		case 2:
			return new Vector(width - 1 - x, y, length - 1 - z);
		case 3:
			return new Vector(z, y, width - 1 - x);
		default:
			return new Vector(x, y, z); 
		}
	}
	
	public static Vector rotateVector(int width, int length, Vector vector, int rotate) {
		return rotateVector(width, length, vector.getX(), vector.getY(), vector.getZ(), rotate);
	}
}
