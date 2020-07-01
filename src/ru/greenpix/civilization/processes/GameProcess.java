package ru.greenpix.civilization.processes;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.database.Stored;
import ru.greenpix.civilization.utils.RunnableManager;

public abstract class GameProcess implements Runnable, Stored {
	
	public static int delay = 2;
	
	private double current = 0;
	
	private BukkitTask task = null;
	
	public abstract boolean onUpdate();
	
	public abstract void onComplete();
	
	public abstract double perTick();
	
	public abstract double getMaxValue();
	
	int tick = 0;
	
	@Override
	public void run() {
		if(!onUpdate()) return;
		tick += delay;
		current += perTick() * (double) delay;
		if(current >= getMaxValue()) {
			onUpdate();
			onComplete();
			pause();
			return;
		} else if(tick % 2400 == 0) {
			RunnableManager.async(() -> writeSql());
		}
	}
	
	public void start() {
		if(isCompleted() || task != null) return;
		task = Bukkit.getScheduler().runTaskTimer(CivCore.getInstance(), () -> run(), delay, delay);
		RunnableManager.async(() -> writeSql());
	}
	
	public void pause() {
		if(task != null) {
			task.cancel();
			task = null;
			setProgress(100);
			RunnableManager.async(() -> writeSql());
		}
	}
	
	public void cancel() {
		setProgress(0);
		pause();
	}
	
	public boolean isRunning() {
		return task != null;
	}
	
	public boolean isCompleted() {
		return current >= getMaxValue();
	}
	
	public double getCurrentValue() {
		return current;
	}
	
	public double getProgress() {
		return getCurrentValue() * 100D / getMaxValue();
	}
	
	public double getTimeTotal() {
		return getMaxValue() / perTick();
	}
	
	public double getTimeLeft() {
		return (getMaxValue() - getCurrentValue()) / (perTick() * 20);
	}
	
	public void setProgress(double progress) {
		current = progress * getMaxValue() / 100D;
	}
	
}
