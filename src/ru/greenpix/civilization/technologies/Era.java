package ru.greenpix.civilization.technologies;

import org.bukkit.ChatColor;

public enum Era {

	ANCIENT(1, ChatColor.GRAY, "Древнейшая Эра"),
	CLASSICAL(2, ChatColor.GOLD, "Античная Эра"),
	MEDIEVAL(3, ChatColor.YELLOW, "Средневековая Эра"),
	RENAISSANCE(4, ChatColor.LIGHT_PURPLE, "Эпоха Возрождения"),
	INDUSTRIAL(5, ChatColor.RED, "Индустриальная Эра"),
	MODERN(6, ChatColor.DARK_AQUA, "Новая Эра"),
	ATOMIC(7, ChatColor.DARK_GREEN, "Атомная Эра"),
	INFORMATION(8, ChatColor.BLUE, "Информационная Эра");
	//FURURE(9, ChatColor.DARK_PURPLE, "Будущая Эра");
	
	private final int index;
	private final ChatColor color;
	private final String name;
	
	private Era(int index, ChatColor color, String name) {
		this.index = index;
		this.color = color;
		this.name = name;
	}
	
	public int index() {
		return index;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public String getDisplayName() {
		return name;
	}
}
