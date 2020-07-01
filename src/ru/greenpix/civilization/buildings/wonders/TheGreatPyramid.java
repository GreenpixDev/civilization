package ru.greenpix.civilization.buildings.wonders;

import org.bukkit.Location;

import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.buildings.common.Cottage;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.mysql.api.Result;

public class TheGreatPyramid extends Wonder {

	public TheGreatPyramid(Result result, Structure str) {
		super(result, str);
	}
	
	public TheGreatPyramid(Town town, Location location, Style style) {
		super(town, location, style);
	}
	
	@Override
	public double getValue(String type) {
		if(type == MONEY) {
			return getStructure().getInt("buffs.income-cottage") * getTown().getBuildings(Cottage.class).size()
					- getStructure().getTax();
		}
		return super.getValue(type);
	}
}
