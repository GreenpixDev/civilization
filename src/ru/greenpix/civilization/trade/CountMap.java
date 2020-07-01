package ru.greenpix.civilization.trade;

import java.util.HashMap;

@SuppressWarnings("serial")
public class CountMap extends HashMap<String, Double> {
	
	public void set(String key, double b) {
		put(key, b);
	}
	
	public void add(String key, double b) {
		put(key, get(key) + b);
	}
	
	public void substract(String key, double b) {
		put(key, get(key) - b);
	}
	
	@Override
	public Double get(Object key) {
		return getOrDefault(key, 0D);
	}
}
