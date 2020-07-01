package ru.greenpix.civilization.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import ru.greenpix.developer.utils.items.Item;

public enum Tier {

	T1(ChatColor.GREEN, new Item(Material.ENCHANTED_BOOK, "&aУровень 1")),
	T2(ChatColor.GOLD, new Item(Material.ENCHANTED_BOOK, "&6Уровень 2")),
	T3(ChatColor.LIGHT_PURPLE, new Item(Material.ENCHANTED_BOOK, "&dУровень 3")),
	T4(ChatColor.RED, new Item(Material.ENCHANTED_BOOK, "&cУровень 4"));
	
	private final ChatColor color;
	private final Item icon;
	
	private Tier(ChatColor color, Item icon) {
		this.color = color;
		this.icon = icon;
	}

	public ChatColor color() {
		return color;
	}
	
	public Item icon() {
		return icon;
	}
}
