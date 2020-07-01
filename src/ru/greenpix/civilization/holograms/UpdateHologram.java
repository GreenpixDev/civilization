package ru.greenpix.civilization.holograms;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.developer.Placeholder;
import ru.greenpix.developer.Utils;

public class UpdateHologram {
	
	private final List<Placeholder> placeholders;
	
	private final List<String> lines;
	
	private final Hologram handle;
	
	private BukkitTask task;
	
	public UpdateHologram(Location location, int update, List<String> lines, Placeholder... phs) {
		this.lines = lines;
		this.placeholders = Arrays.asList(phs);
		this.handle = HologramsAPI.createHologram(CivCore.getInstance(), location);
		update();
		if(update > 0) {
			this.task = Bukkit.getScheduler().runTaskTimer(CivCore.getInstance(), () -> update(), update, update);
		}
	}
	
	public void update() {
		int i = 0;
		for(String s : lines) {
			s = Utils.color(Placeholder.replace(s, placeholders.stream().toArray(Placeholder[]::new)));
			if(i < handle.size() && handle.getLine(i) instanceof TextLine) {
				TextLine line = (TextLine) handle.getLine(i);
				line.setText(s);
			} else handle.appendTextLine(s);
			i++;
		}
	}
	
	public void setTouchHandler(TouchHandler handler) {
		for(int i = 0; i < handle.size(); i++) {
			if(handle.getLine(i) instanceof TouchableLine) {
				TouchableLine line = ((TouchableLine) handle.getLine(i));
				line.setTouchHandler(handler);
			}
		}
	}
	
	public void remove() {
		if(task != null) task.cancel();
		if(handle.isDeleted()) return;
		this.handle.delete();
	}
	
	public List<Placeholder> getPlaceholders() {
		return placeholders;
	}
	
	public List<String> getLines() {
		return lines;
	}
	
	public Hologram getHandle() {
		return handle;
	}
}
