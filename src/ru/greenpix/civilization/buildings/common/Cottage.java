package ru.greenpix.civilization.buildings.common;

import org.bukkit.Location;

import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.mysql.api.Result;

public class Cottage extends Building {

	public Cottage(Town town, Location location, Style style) {
		super(town, location, style);
	}
	
	public Cottage(Result result, Structure str) {
		super(result, str);
	}
	
	@Override
	public double getValue(String type) {
		if(type == MONEY) {
			int c = getTown().getBuildings(getStructure()).size();
			double f1 = (double) Math.min(getTown().getBuildings(Farm.class).size(), c) / (double) c;
			double f2 = (double) Math.min(getTown().getBuildings(FishHatchery.class).size(), c) / (double) c;
			return getStructure().getInt("buffs.income") * f1 
					+ getStructure().getInt("buffs.income2") * f2 
					- getStructure().getTax();
		}
		return super.getValue(type);
	}
	
}
