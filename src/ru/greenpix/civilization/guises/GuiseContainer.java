package ru.greenpix.civilization.guises;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import ru.greenpix.developer.DeveloperAPI;
import ru.greenpix.developer.PluginPlayer;
import ru.greenpix.developer.utils.guises.Guise;
import ru.greenpix.developer.utils.guises.GuiseItem;

public class GuiseContainer extends Guise {

	private final Inventory container;
	
	public GuiseContainer(Guise guise) {
		super(guise);
		container = super.createInventory(null);
		if(isUpdatable()) {
			Bukkit.getScheduler().runTaskTimer(DeveloperAPI.getInstance(), () -> update(), getUpdateInterval(), getUpdateInterval());
		}
	}
	
	/*
	public GuiseContainer(Plugin plugin, int size) {
		super(plugin, size);
		container = super.createInventory(null);
	}
	
	public GuiseContainer(Plugin plugin, int size, String titlePath) {
		super(plugin, size, titlePath);
		container = super.createInventory(null);
	}

	public GuiseContainer(Plugin plugin, int size, String title, String name) {
		super(plugin, size, title, name);
		container = super.createInventory(null);
	}
	*/
	
	@Override
	public Inventory createInventory(PluginPlayer player) {
		return container;
	}
	
	@Override
	public Guise clone() {
		return new GuiseContainer(this);
	}
	
	/**
	 * Добавляет СТАТИЧЕСКИЙ предмет! Не в контейнер!
	 */

	@Override
	public void addItem(int x, int y, GuiseItem item) {
		if(item == null) super.addItem(x, y, null);
		else super.addItem(x, y, item.clone());
	}
	
	/**
	 * Добавляет СТАТИЧЕСКИЙ предмет! Не в контейнер!
	 */

	@Override
	public void addItem(int number, GuiseItem item) {
		if(item == null) super.addItem(number, null);
		else super.addItem(number, item.clone());
	}
	
	/**
	 * Добавляет СТАТИЧЕСКИЙ предмет! Не в контейнер!
	 */
	
	@Override
	public int addItem(GuiseItem item) {
		if(item == null) return super.addItem(null);
		else return super.addItem(item.clone());
	}
	
	@Override
	public void open(PluginPlayer player) {
		Inventory inv = createInventory(player);
		player.getPlayer().openInventory(inv);
		player.setOpenGuise(this);
		this.onOpen(inv, player);
	}
	
	/**
	 *  Нужно использовать после addItem
	 */
	
	public void update() {
		fillInventory(null, container, true);
	}
	
	public Inventory getContainer() {
		return container;
	}
}
