package ru.greenpix.civilization.trade;

import java.util.HashMap;
import java.util.Map;

import ru.greenpix.civilization.objects.Countable;

public class TradeResource implements Countable {

	private final Map<String, Double> values = new HashMap<String, Double>();
	
	private final TradeResources type;
	
	private double used = 100;
	
	public TradeResource(TradeResources type) {
		this.type = type;
	}
	
	public Map<String, Double> getValues() {
		return values;
	}
	
	@Override
	public double getValue(String type) {
		return values.getOrDefault(type, 0D) * used / 100D;
	}

	public double getUsed() {
		return used;
	}

	public void setUsed(double used) {
		this.used = used;
	}

	public TradeResources getType() {
		return type;
	}
}
