package ru.greenpix.civilization.guises;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import ru.greenpix.developer.utils.guises.Guise;
import ru.greenpix.developer.utils.guises.GuiseItem;
import ru.greenpix.developer.utils.items.Item;

public class GuiseContainerBuilder extends GuiseBuilder {

	private int x1 = 0;
	
	private int x2 = 0;
	
	private int y1 = 0;
	
	private int y2 = 0;
	
	private Map<Integer, GuiseItem> map = new HashMap<>();
	
	private List<GuiseItem> list = new ArrayList<>(); 
	
	public GuiseContainerBuilder() {
		super();
	}
	
	public GuiseContainerBuilder(int height) {
		super(height);
	}
	
	public GuiseContainerBuilder addItem(int x, int y, GuiseItem item) {
		map.put(Guise.getNumber(x, y), item);
		return this;
	}
	
	public GuiseContainerBuilder addItem(int n, GuiseItem item) {
		map.put(n, item);
		return this;
	}
	
	public GuiseContainerBuilder addItem(GuiseItem... items) {
		list.addAll(Arrays.asList(items));
		return this;
	}
	
	public GuiseContainerBuilder addItem(int x, int y, Item item) {
		return addItem(x, y, new GuiseItem(item));
	}
	
	public GuiseContainerBuilder addItem(int n, Item item) {
		return addItem(n, new GuiseItem(item));
	}
	
	public GuiseContainerBuilder addItem(int x, int y, Item item, ItemHandler click, ItemHandler load) {
		return addItem(x, y, new GuiseItem(item, (p,i) -> {if(click != null) click.run(i);}, (p,i) -> {if(load != null) load.run(i);}));
	}
	
	public GuiseContainerBuilder addItem(int n, Item item, ItemHandler click, ItemHandler load) {
		return addItem(n, new GuiseItem(item, (p,i) -> {if(click != null) click.run(i);}, (p,i) -> {if(load != null) load.run(i);}));
	}
	
	public GuiseContainerBuilder addItem(Item item, ItemHandler click, ItemHandler load) {
		return addItem(new GuiseItem(item, (p,i) -> {if(click != null) click.run(i);}, (p,i) -> {if(load != null) load.run(i);}));
	}
	
	public GuiseContainerBuilder addItem(int x, int y, Item item, ItemHandler all) {
		return addItem(x, y, new GuiseItem(item, (p,i) -> {if(all != null) all.run(i);}, (p,i) -> {if(all != null) all.run(i);}));
	}
	
	public GuiseContainerBuilder addItem(int n, Item item, ItemHandler all) {
		return addItem(n, new GuiseItem(item, (p,i) -> {if(all != null) all.run(i);}, (p,i) -> {if(all != null) all.run(i);}));
	}
	
	public GuiseContainerBuilder addItem(Item item, ItemHandler all) {
		return addItem(new GuiseItem(item, (p,i) -> {if(all != null) all.run(i);}, (p,i) -> {if(all != null) all.run(i);}));
	}
	
	public GuiseContainerBuilder addItem(Item... items) {
		list.addAll(Arrays.asList(items).stream().map(e -> new GuiseItem(e)).collect(Collectors.toList()));
		return this;
	}
	
	public GuiseContainerBuilder addItem(int x, int y, ItemStack item) {
		return addItem(x, y, new Item(item));
	}
	
	public GuiseContainerBuilder addItem(int n, ItemStack item) {
		return addItem(n, new Item(item));
	}
	
	public GuiseContainerBuilder addItem(ItemStack... items) {
		list.addAll(Arrays.asList(items).stream().map(e -> new GuiseItem(new Item(e))).collect(Collectors.toList()));
		return this;
	}
	
	public GuiseContainerBuilder addItem(int x, int y, Material item) {
		return addItem(x, y, new Item(item));
	}
	
	public GuiseContainerBuilder addItem(int n, Material item) {
		return addItem(n, new Item(item));
	}
	
	public GuiseContainerBuilder addItem(Material... items) {
		list.addAll(Arrays.asList(items).stream().map(e -> new GuiseItem(new Item(e))).collect(Collectors.toList()));
		return this;
	}
	
	public GuiseContainerBuilder interactionZone(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		return this;
	}
	
	@Deprecated
	public GuiseContainerBuilder size(int size) {
		super.size(size);
		return this;
	}
	
	public GuiseContainerBuilder height(int h) {
		super.height(h);
		return this;
	}
	
	public GuiseContainerBuilder itemFrame(Item item) {
		super.itemFrame(item);
		return this;
	}
	
	public GuiseContainerBuilder itemEmpty(Item item) {
		super.itemEmpty(item);
		return this;
	}
	
	public GuiseContainerBuilder defaultInteractionZone() {
		return interactionZone(1, 1, 7, (super.size / 9) - 2);
	}
	
	public GuiseContainerBuilder defaultFrame() {
		super.defaultFrame();
		return this;
	}
	
	public GuiseContainerBuilder defaultEmpty() {
		super.defaultEmpty();
		return this;
	}
	
	public GuiseContainerBuilder update(int interval) {
		super.update(interval);
		return this;
	}
	
	public GuiseContainerBuilder plugin(Plugin plugin) {
		super.plugin(plugin);
		return this;
	}
	
	public GuiseContainerBuilder title(String title) {
		super.title(title);
		return this;
	}
	
	@Deprecated
	public GuiseContainerBuilder name(String name) {
		super.name(name);
		return this;
	}
	
	public GuiseContainer build() {
		Guise image = super.build();
		map.forEach((n, i) -> image.addItem(n, i));
		list.forEach(i -> image.addItem(i));
		GuiseItem item;
		for(int x = x1; x <= x2; x++) {
			for(int y = y1; y <= y2; y++) {
				item = image.getItem(x, y);
				if(item == null) image.addItem(x, y, item = new GuiseItem(new Item(Material.AIR)));
				item.setCancelEvent(false);
			}
		}
		return new GuiseContainer(image);
	}
	
	public GuiseContainer build(String title) {
		GuiseContainer container = build();
		container.setTitle(title);
		return container;
	}
	
	@Override
	public GuiseContainerBuilder clone() {
		return (GuiseContainerBuilder) super.clone();
	}
	
	public static interface ItemHandler {
		
		public void run(Item item);
		
	}
}
