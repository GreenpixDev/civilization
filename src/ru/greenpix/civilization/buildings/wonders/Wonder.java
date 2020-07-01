package ru.greenpix.civilization.buildings.wonders;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.developer.utils.protocol.sounds.FixedSound;
import ru.greenpix.mysql.api.Result;

public class Wonder extends Building {

	private static final List<Wonder> wonders = new ArrayList<Wonder>();
	
	public Wonder(Result result, Structure str) {
		super(result, str);
		wonders.add(this);
	}
	
	public Wonder(Town town, Location location, Style style) {
		super(town, location, style);
		wonders.add(this);
		CivCore.broadcastTitle("", "&e&lНачинается строительство '" + getDisplayName() + "' в городе: " + getTown().getName(), 10, 80, 10);
		Bukkit.getOnlinePlayers().forEach(p -> FixedSound.WITHER_IDLE.playSound(p, 5f, 2f));
	}
	
	/*
	public static boolean existsWonder(Structure s) {
		return getWonders().stream()
				.filter(w -> w.getStructure().equals(s))
				.count() > 0;
	}
	*/
	
	public static boolean existsAliveWonder(Structure s) {
		return getWonders().stream()
				.filter(w -> w.getStructure().equals(s) && !w.isDestroyed())
				.count() > 0;
	}
	
	public static Wonder getWonder(Structure s) {
		return getWonders().stream()
				.filter(w -> w.getStructure().equals(s))
				.findFirst().orElse(null);
	}
	
	public static <T extends Wonder> T getWonder(Class<T> clazz) {
		return getWonders().stream()
				.filter(w -> clazz.isInstance(w))
				.map(w -> clazz.cast(w))
				.findFirst().orElse(null);
	}
	
	public static List<Wonder> getWonders() {
		return wonders;
	}
	
	@Override
	public void remove() {
		wonders.remove(this);
		super.remove();
	}
	
	/*
	@Override
	public void delete() {
		super.delete();
		wonders.remove(this);
	}
	*/
	
	@Override
	public void destroy() {
		super.destroy();
		CivCore.broadcastTitle("&e&l'" + getDisplayName() + " уничтожен!", "В городе: &e" + getTown().getName(), 10, 80, 10);
		Bukkit.getOnlinePlayers().forEach(p -> FixedSound.ENDERDRAGON_GROWL.playSound(p, 5f, 2f));
		getTown().getCivilization().breakWonder = System.currentTimeMillis();
	}
}
