package ru.greenpix.civilization.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import org.bukkit.Bukkit;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.mysql.api.Result;
import ru.greenpix.mysql.elements.MysqlTable;
import ru.greenpix.mysql.nbt.MysqlFields;

@Deprecated
public class SafetyManager {

	private static ArrayList<Runnable> queue = new ArrayList<Runnable>();
	
	/**
	 * Безопасно выполнить метод, использующий асинхронность.
	 * Полезно для сохранения полного массива данных, если сохранение
	 * разбито на несколько действий в асинхронной цепочке.
	 * Без применение этого метода такое сохранение не сохранит все
	 * при внезапном отключении сервера. Не поможет, если сервер упал.
	 */
	
	public static void safelyRun() {
		
	}
	
	/**
	 * Безопасно записать запись и получить её id.
	 * Не будет работать, если с таблицой работает другая программа.
	 */
	public static void putAndGetId(MysqlTable table, MysqlFields fields, CallbackResult callback) {
		queue.add(() -> {
			table.add(fields);
			final Result result = table.getMax("id");
			RunnableManager.sync(() -> callback.run(result));
		});
	}
	
	public static void runThread() {
		Bukkit.getScheduler().runTaskTimer(CivCore.getInstance(), () -> {
			Queue<Runnable> q = new ArrayDeque<Runnable>(queue);
			queue.clear();
			RunnableManager.async(() -> {
				while(!q.isEmpty()) q.poll().run();
			});
		}, 1L, 1L);
	}
	
	public static interface CallbackResult {
		
		void run(Result result);
		
	}
}
