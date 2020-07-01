package ru.greenpix.civilization.clipboard;

import java.util.function.Consumer;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.clipboard.ArrayClipboard.ClipboardIterator;
import ru.greenpix.civilization.clipboard.ArrayClipboard.RotatedClipboardIterator;
import ru.greenpix.civilization.utils.RunnableManager;
import ru.greenpix.civilization.utils.RunnableManager.AsyncGetter;

public class CachedClipboard implements Clipboard {

	private ArrayClipboard clipboard = null;

	private BukkitRunnable runnable = null;
	
	private boolean loading = false;
	
	private final AsyncGetter<ArrayClipboard> getter;
	private final int cacheTime;
	private final int width;
	private final int height;
	private final int length;
	
	public CachedClipboard(int width, int height, int length, int cacheTime, AsyncGetter<ArrayClipboard> getter) {
		this.width = width;
		this.height = height;
		this.length = length;
		this.getter = getter;
		this.cacheTime = cacheTime;
	}
	
	public CachedClipboard(ArrayClipboard clipboard, int cacheTime, AsyncGetter<ArrayClipboard> getter) {
		this.width = clipboard.getWidth();
		this.height = clipboard.getHeight();
		this.length = clipboard.getLength();
		this.getter = getter;
		this.clipboard = clipboard;
		this.cacheTime = cacheTime;
		(this.runnable = new BukkitRunnable() {
			@Override
			public void run() {
				CachedClipboard.this.clipboard = null;
			}
		}).runTaskLater(CivCore.getInstance(), cacheTime <= 0 ? Long.MAX_VALUE : cacheTime * 20L);
	}
	
	public void cache() {
		cache(e -> {});
	}
	
	public void cache(Consumer<Boolean> exception) {
		if(loading) return;
		if(clipboard != null) {
			runnable.cancel();
		} else {
			loading = true;
			RunnableManager.run(getter, b -> {
				clipboard = b; 
				loading = false;
				exception.accept(clipboard != null);
			});
		}
		(this.runnable = new BukkitRunnable() {
			@Override
			public void run() {
				CachedClipboard.this.clipboard = null;
			}
		}).runTaskLater(CivCore.getInstance(), cacheTime <= 0 ? Long.MAX_VALUE : cacheTime * 20L);
	}
	
	public boolean isCached() {
		return clipboard != null;
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
	
	public Vector rotateVector(double x, double y, double z, int rotate) {
		return Clipboard.rotateVector(getWidth(), getLength(), x, y, z, rotate);
	}
	
	public Vector rotateVector(Vector v, int rotate) {
		return rotateVector(v.getX(), v.getY(), v.getZ(), rotate);
	}
	
	@Override
	public Vector vectorOf(int index) {
		int y = index / area(); 
		int z = (index - y * area()) / getWidth();
		int x =	index - y * area() - z * getWidth();
		return new Vector(x, y, z);
	}

	@Override
	public int getBlockId(int x, int y, int z) {
		cache();
		if(!isCached()) {
			return 0;
		}
		else return clipboard.getBlockId(x, y, z);
	}

	@Override
	public byte getBlockData(int x, int y, int z) {
		cache();
		if(!isCached()) {
			return 0;
		}
		else return clipboard.getBlockData(x, y, z);
	}

	@Override
	public BaseBlock getBlock(int x, int y, int z) {
		cache();
		if(!isCached()) {
			return new BaseBlock(0, (byte) 0, new Vector(x, y, z));
		}
		else return clipboard.getBlock(x, y, z);
	}

	@Override
	public void setBlock(int x, int y, int z, int id, byte data) {
		cache();
		if(isCached()) clipboard.setBlock(x, y, z, id, data);
	}

	@Override
	public boolean hasBlock(int x, int y, int z) {
		cache();
		if(!isCached()) {
			return false;
		}
		else return clipboard.hasBlock(x, y, z);
	}

	@Override
	public boolean hasBlock(int x, int y, int z, int rotate) {
		cache();
		if(!isCached()) {
			return false;
		}
		else return clipboard.hasBlock(x, y, z, rotate);
	}

	@Override
	public int getBlockId(Vector v) {
		cache();
		if(!isCached()) {
			return 0;
		}
		else return clipboard.getBlockId(v);
	}

	@Override
	public byte getBlockData(Vector v) {
		cache();
		if(!isCached()) {
			return 0;
		}
		else return clipboard.getBlockData(v);
	}

	@Override
	public BaseBlock getBlock(Vector v) {
		cache();
		if(!isCached()) {
			return new BaseBlock(0, (byte) 0, v);
		}
		else return clipboard.getBlock(v);
	}

	@Override
	public void setBlock(Vector v, int id, byte data) {
		cache();
		if(isCached()) clipboard.setBlock(v, id, data);
	}

	@Override
	public boolean hasBlock(Vector v) {
		cache();
		if(!isCached()) {
			return false;
		}
		else return clipboard.hasBlock(v);
	}

	@Override
	public boolean hasBlock(Vector v, int rotate) {
		cache();
		if(!isCached()) {
			return false;
		}
		else return clipboard.hasBlock(v, rotate);
	}

	@Override
	public BaseBlock rotateBlock(BaseBlock b, int rotate) {
		cache();
		if(!isCached()) {
			return b;
		}
		else return clipboard.rotateBlock(b, rotate);
	}

	@Override
	public ClipboardIterator iterator() {
		cache();
		if(!isCached()) {
			return null;
		}
		else return clipboard.iterator();
	}

	@Override
	public RotatedClipboardIterator iterator(int rotate) {
		cache();
		if(!isCached()) {
			return null;
		}
		else return clipboard.iterator(rotate);
	}
}
