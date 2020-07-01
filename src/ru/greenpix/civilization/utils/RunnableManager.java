package ru.greenpix.civilization.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RunnableManager {

	private static Plugin plugin;
	
	public volatile static boolean ENABLED;
	
	public static void enable(Plugin plugin) {
		ENABLED = true;
		RunnableManager.plugin = plugin;
	}
	
	public static void runIf(Condition condition, Runnable runnable) {
		runIf(condition, runnable, 0, null);
	}
	
	public static void runIf(Condition condition, Runnable runnable, int timeout) {
		runIf(condition, runnable, timeout, null);
	}
	
	/**
	 * Выполнит синхронно задачу тогда, когда будет синхронно соблюдено условие
	 * @param condition - условие
	 * @param runnable - задача
	 * @param timeout - максимальное время ожидания
	 * @param timeoutRunnable - задача, которая выполнится, если время вышло
	 */
	
	public static void runIf(Condition condition, Runnable runnable, int timeout, Runnable timeoutRunnable) {
		BukkitRunnable bukkit = new BukkitRunnable() {
			int ticks = 0;
			@Override
			public void run() {
				if(condition.done()) {
					cancel();
					runnable.run();
					return;
				}
				if(timeout > 0) {
					ticks++;
					if(ticks >= timeout) {
						cancel();
						if(timeoutRunnable != null) timeoutRunnable.run();
					}
				}
			}
		};
		bukkit.runTaskTimer(plugin, 0, 1);
	}
	
	public static void runAll(Map<String, AsyncGetter<?>> map, SyncFuture<Map<String, Object>> done) {
		new Cloneable() {
			Map<String, Object> results = new HashMap<String, Object>();
			@Override
			public Object clone() {
				map.forEach((key, body) -> {
					run(body, e -> results.put(key, e));
				});
				runIf(() -> results.size() == map.size(), () -> done.run(results));
				return null;
			}
		}.clone();
	}
	
	@SuppressWarnings("serial")
	public static class AsyncTaskMap<T> extends HashMap<String, AsyncGetter<T>> {
		
		public AsyncTaskMap<T> add(String key, AsyncGetter<T> value) {
			super.put(key, value);
			return this;
		}
		
	}
	
	/**
	 * Выполнит синхронную задачу, когда выполнит асинхронную задачу
	 * @param waiter - асинхронная задача
	 * @param future - синхронная задача
	 */
	public static void run(Runnable waiter, Runnable future) {
		async(() -> {
			waiter.run();
			sync(() -> future.run());
		});
	}
	
	/**
	 * Выполнит синхронную задачу, когда получит результат асинхронно
	 * @param getter - асинхронная задача с результатом
	 * @param future - синхронная задача с использованием результата
	 */
	
	public static <T> void run(AsyncGetter<T> getter, SyncFuture<T> future) {
		async(() -> {
			T e = getter.get();
			sync(() -> future.run(e));
		});
	}
	
	/**
	 * Выполнить задачу в асинхронном Bukkit потоке
	 * @param runnable - задача, которую нужно выполнить асинхронно
	 */
	
	public static void async(Runnable runnable) {
		if(!ENABLED) sync(runnable);
		else Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
	}
	
	/**
	 * Выполнить задачу в синхронном Bukkit потоке
	 * @param runnable - задача, которую нужно выполнить синхронно
	 */
	
	public static void sync(Runnable runnable) {
		Bukkit.getScheduler().runTask(plugin, runnable);
	}
	
	public static <T> BukkitTask runGradually(Iterable<T> i, Consumer<T> action, long period, int speed) {
		return runGradually(i.iterator(), action, () -> {}, period, speed);
	}
	
	public static <T> BukkitTask runGradually(Iterable<T> i, Consumer<T> action, long delay, long period, int speed) {
		return runGradually(i.iterator(), action, () -> {}, delay, period, speed);
	}
	
	public static <T> BukkitTask runGradually(Iterator<T> i, Consumer<T> action, long period, int speed) {
		return runGradually(i, action, () -> {}, 0L, period, speed);
	}
	
	public static <T> BukkitTask runGradually(Iterator<T> i, Consumer<T> action, long delay, long period, int speed) {
		return runGradually(i, action, () -> {}, delay, period, speed);
	}
	
	public static <T> BukkitTask runGradually(Iterable<T> i, Consumer<T> action, Runnable end, long period, int speed) {
		return runGradually(i.iterator(), action, end, period, speed);
	}
	
	public static <T> BukkitTask runGradually(Iterable<T> i, Consumer<T> action, Runnable end, long delay, long period, int speed) {
		return runGradually(i.iterator(), action, end, delay, period, speed);
	}
	
	public static <T> BukkitTask runGradually(Iterator<T> i, Consumer<T> action, Runnable end, long period, int speed) {
		return runGradually(i, action, end, 0L, period, speed);
	}

	public static <T> BukkitTask runGradually(Iterator<T> iter, Consumer<T> action, Runnable end, long delay, long period, int speed) {
		BukkitTask task = new BukkitRunnable() {
	
			@Override
			public void run() {
				for(int e = 0; e < speed; e++) {
					if(!iter.hasNext()) {
						cancel();
						end.run();
						return;
					}
					action.accept(iter.next());
				}
			}
			
		}.runTaskTimer(plugin, delay, period);
		return task;
	}
	
	public interface Condition {
		
		public boolean done();
		
	}
	
	public interface AsyncGetter<T> {
		
		public T get();
		
	}
	
	public interface SyncFuture<T> {
		
		public void run(T e);
		
	}
}
