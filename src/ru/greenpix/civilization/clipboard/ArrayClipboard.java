package ru.greenpix.civilization.clipboard;

import java.util.Iterator;

import org.bukkit.util.Vector;

public class ArrayClipboard implements Clipboard {

	private final short[] blocks;
	
	private final int width;
	
	private final int height;
	
	private final int length;
	
	public ArrayClipboard(int width, int height, int length) {
		this.width = width;
		this.height = height;
		this.length = length;
		this.blocks = new short[size()];
	}
	
	public int size() {
		return width * height * length;
	}
	
	public int area() {
		return width * length;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getLength() {
		return length;
	}
	
	public Vector vectorOf(int index) {
		int y = index / area(); 
		int z = (index - y * area()) / getWidth();
		int x =	index - y * area() - z * getWidth();
		return new Vector(x, y, z);
	}
	
	public int getBlockId(int x, int y, int z) {
		return blocks[area() * y + getWidth() * z + x] >> 4;
	}
	
	public byte getBlockData(int x, int y, int z) {
		return (byte) (blocks[area() * y + getWidth() * z + x] & 15);
	}
	
	public BaseBlock getBlock(int x, int y, int z) {
		return new BaseBlock(getBlockId(x, y, z), getBlockData(x, y, z), new Vector(x, y, z));
	}
	
	public void setBlock(int x, int y, int z, int id, byte data) {
		blocks[area() * y + getWidth() * z + x] = (short) (((id << 4) + (data & 15)) & 65535);
	}
	
	public boolean hasBlock(int x, int y, int z) {
		return getBlockId(x, y, z) != 0;
	}
	
	public boolean hasBlock(int x, int y, int z, int rotate) {
		return getBlockId(rotateVector(x, y, z, rotate)) != 0;
	}
	
	public Vector rotateVector(double x, double y, double z, int rotate) {
		return Clipboard.rotateVector(getWidth(), getLength(), x, y, z, rotate);
	}
	
	public int getBlockId(Vector v) {
		return getBlockId(v.getBlockX(), v.getBlockY(), v.getBlockZ());
	}
	
	public byte getBlockData(Vector v) {
		return getBlockData(v.getBlockX(), v.getBlockY(), v.getBlockZ());
	}
	
	public BaseBlock getBlock(Vector v) {
		return new BaseBlock(getBlockId(v), getBlockData(v), v);
	}
	
	public void setBlock(Vector v, int id, byte data) {
		setBlock(v.getBlockX(), v.getBlockY(), v.getBlockZ(), id, data);
	}
	
	public boolean hasBlock(Vector v) {
		return getBlockId(v) != 0;
	}
	
	public boolean hasBlock(Vector v, int rotate) {
		return getBlockId(rotateVector(v, rotate)) != 0;
	}
	
	public Vector rotateVector(Vector v, int rotate) {
		return rotateVector(v.getX(), v.getY(), v.getZ(), rotate);
	}
	
	public BaseBlock rotateBlock(BaseBlock b, int rotate) {
		for(int i = 0; i < rotate; i++) rotateBlock(b);
		return b;
	}
	
	private BaseBlock rotateBlock(BaseBlock b) {
		return b.rotateRight();
	}
	
	public ClipboardIterator iterator() {
		return new ClipboardIterator();
	}
	
	public RotatedClipboardIterator iterator(int rotate) {
		return new RotatedClipboardIterator(rotate);
	}
	
	class ClipboardIterator implements Iterator<BaseBlock> {
		
		int x = 0;
		int y = 0;
		int z = 0;
		
		@Override
		public boolean hasNext() {
			return y < getHeight();
		}

		@Override
		public BaseBlock next() {
			BaseBlock e = getBlock(x, y, z);
			x++;
			if(x == getWidth()) {
				x = 0;
				z++;
				if(z == getLength()) {
					z = 0;
					y++;
				}
			}
			return e;
		}
	}
	
	class RotatedClipboardIterator implements Iterator<BaseBlock> {
		
		int rotate;
		int x = 0;
		int y = 0;
		int z = 0;
		
		public RotatedClipboardIterator(int rotate) {
			this.rotate = rotate;
		}
		
		@Override
		public boolean hasNext() {
			return z < getLength();
		}

		@Override
		public BaseBlock next() {
			BaseBlock e = rotateBlock(getBlock(rotateVector(x, y, z, rotate)), rotate);
			x++;
			if(x == getWidth()) {
				x = 0;
				z++;
				if(z == getLength()) {
					z = 0;
					y++;
				}
			}
			return e;
		}
	}
}
