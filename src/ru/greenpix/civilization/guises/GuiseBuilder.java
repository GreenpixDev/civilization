package ru.greenpix.civilization.guises;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.developer.utils.guises.Guise;
import ru.greenpix.developer.utils.guises.GuiseBook;
import ru.greenpix.developer.utils.items.Item;

public class GuiseBuilder implements Cloneable {

	public final static Item FRAME = new Item(Material.STAINED_GLASS_PANE, 15);
	
	protected int size = 27;
	
	protected int update = 0;
	
	protected Item itemFrame = null;
	
	protected Item itemEmpty = null;
	
	protected String title = null;
	
	protected String name = null;
	
	protected Plugin plugin = CivCore.getInstance();
	
	public GuiseBuilder() {
		
	}
	
	public GuiseBuilder(int height) {
		height(height);
	}
	
	@Deprecated
	public GuiseBuilder size(int size) {
		this.size = size;
		return this;
	}
	
	public GuiseBuilder height(int h) {
		this.size = h * 9; 
		return this;
	}
	
	public GuiseBuilder itemFrame(Item item) {
		this.itemFrame = item;
		return this;
	}
	
	public GuiseBuilder itemEmpty(Item item) {
		this.itemEmpty = item;
		return this;
	}
	
	public GuiseBuilder defaultFrame() {
		this.itemFrame = FRAME;
		return this;
	}
	
	public GuiseBuilder defaultEmpty() {
		this.itemEmpty = FRAME;
		return this;
	}
	
	public GuiseBuilder update(int interval) {
		this.update = interval;
		return this;
	}
	
	public GuiseBuilder plugin(Plugin plugin) {
		this.plugin = plugin;
		return this;
	}
	
	public GuiseBuilder title(String title) {
		this.title = title;
		return this;
	}
	
	@Deprecated
	public GuiseBuilder name(String name) {
		this.name = name;
		return this;
	}
	
	public Guise build() {
		Guise guise = new Guise(plugin, size, title, name);
		guise.setItemFrame(itemFrame);
		guise.setItemEmpty(itemEmpty);
		guise.setUpdateInterval(update);
		return guise;
	}
	
	public Guise build(String title) {
		Guise guise = new Guise(plugin, size, title, name);
		guise.setItemFrame(itemFrame);
		guise.setItemEmpty(itemEmpty);
		guise.setUpdateInterval(update);
		return guise;
	}
	
	public GuiseBook buildBook() {
		return new GuiseBook(build());
	}
	
	public GuiseBook buildBook(String title) {
		return new GuiseBook(build(title));
	}
	
	@Override
	public GuiseBuilder clone() {
		try {
			return (GuiseBuilder) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
