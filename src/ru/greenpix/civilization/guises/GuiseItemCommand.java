package ru.greenpix.civilization.guises;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ru.greenpix.developer.utils.guises.GuiseItem;
import ru.greenpix.developer.utils.items.Item;

public class GuiseItemCommand extends GuiseItem {

	final String cmd;
	
	public GuiseItemCommand(Item item, String cmd) {
		super(item);
		this.cmd = cmd;
	} 
	
	public GuiseItemCommand(Item item, String cmd, GuiseItemAction load) {
		super(item, null, load);
		this.cmd = cmd;
	} 
	
	@Override
	public void onClick(Player p, Item i) {
		Bukkit.dispatchCommand(p, cmd);
	}
}
