package ru.greenpix.civilization.diplomacy;

import java.util.stream.Stream;

import org.bukkit.ChatColor;

import ru.greenpix.civilization.groups.Perms;
import ru.greenpix.developer.utils.protocol.sounds.FixedSound;

public enum Status {
	
	WAR(-2, ChatColor.RED, "война", Perms.DECLARE_WAR, FixedSound.ENDERDRAGON_GROWL),
	HOSTILE(-1, ChatColor.GOLD, "вражда", Perms.DECLARE_HOSTILE, FixedSound.WOLF_GROWL),
	NEUTRAL(0, ChatColor.WHITE, "нейтралитет", Perms.REQUEST_NEUTRAL, FixedSound.PIG_IDLE),
	PEACE(1, ChatColor.AQUA, "мир", Perms.REQUEST_PEACE, FixedSound.VILLAGER_YES),
	ALLY(2, ChatColor.GREEN, "союз", Perms.REQUEST_ALLY, FixedSound.CAT_MEOW);
	
	public final int id;

	public final ChatColor color;
	
	private final String displayName;
	
	private final String permission;
	
	private final FixedSound sound;
	
	private Status(int id, ChatColor color, String displayName, String perm, FixedSound sound) {
		this.id = id;
		this.color = color;
		this.displayName = displayName;
		this.permission = perm;
		this.sound = sound;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public static Status getById(int id) {
		return Stream.of(values()).filter(e -> e.id == id).findFirst().orElse(null);
	}
	
	public static Status getByName(String name) {
		return Stream.of(values()).filter(e -> e.name().equalsIgnoreCase(name) || e.getDisplayName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public String getPermission() {
		return permission;
	}

	public FixedSound getSound() {
		return sound;
	}
	
}
