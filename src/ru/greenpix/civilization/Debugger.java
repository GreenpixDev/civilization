package ru.greenpix.civilization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.google.common.base.Preconditions;

import ru.greenpix.civilization.utils.RunnableManager;

public class Debugger {

	public volatile int min_millis = 20;
	
	private final Thread main;
	
	private final Plugin plugin;
	
	private final PrintWriter logWriter;
	
	private Thread lagometer;
	
	StackTraceElement[] stack;
	
	int millis = 0;
	
	public Debugger(Plugin plugin, File log) throws IOException {
		Preconditions.checkArgument(Bukkit.isPrimaryThread());
		this.main = Thread.currentThread();
		this.plugin = plugin;
		FileWriter fw;
		fw = new FileWriter(log, true);
		logWriter = new PrintWriter(fw);
		lagometerStart();
	}
	
	public void close() {
		logWriter.close();
		lagometer.interrupt();
	}
	
	public Thread getMainBukkitThread() {
		return main;
	}
	
	public Thread lagometerStart() {
		(lagometer = new Thread(() -> {
			while (RunnableManager.ENABLED) {
				try {
					Thread.sleep(1);
					StackTraceElement[] stackN = getMainBukkitThread().getStackTrace();
					if(Arrays.equals(stack, stackN)) {
						if(!stack[0].getMethodName().equals("sleep")) {
							final int ms = millis;
							if(millis == 100) {
								Bukkit.getScheduler().runTask(plugin, () -> plugin.getServer().getConsoleSender().sendMessage("§6HUGE STACK TRACE DELAY: " + ms + " ms"));
								logWriter.println("[HUGE] STACK TRACE DELAY: " + ms + " ms");
								for(StackTraceElement e : stack) {
									System.out.println(e);
									logWriter.println(e);
								}
								logWriter.flush();
							} else if(millis == 1000) {
								Bukkit.getScheduler().runTask(plugin, () -> plugin.getServer().getConsoleSender().sendMessage("§cVERY HUGE STACK TRACE DELAY: " + ms + " ms"));
								logWriter.println("[FATAL] STACK TRACE DELAY: " + ms + " ms");
								for(StackTraceElement e : stack) {
									System.out.println(e);
									logWriter.println(e);
								}
								logWriter.flush();
							} 
						}
						millis++;
					} else if(millis >= min_millis) {
						final int ms = millis;
						if(!stack[0].getMethodName().equals("sleep")) {
							Bukkit.getScheduler().runTask(plugin, () -> plugin.getLogger().warning("STACK TRACE DELAY: " + ms + " ms"));
							logWriter.println("[NORMAL] STACK TRACE DELAY: " + millis + " ms");
							for(StackTraceElement e : stack) {
								logWriter.println(e);
							}
							logWriter.flush();
						}
						millis = 0;
					} else {
						millis = 0;
					}
					stack = stackN;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		})).start();
		return lagometer;
	}
	
}
