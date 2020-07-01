package ru.greenpix.civilization.buildings.wonders;

import org.bukkit.Location;

import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.mysql.api.Result;

public class TheHangingGarden extends Wonder {

	public TheHangingGarden(Result result, Structure str) {
		super(result, str);
	}
	
	public TheHangingGarden(Town town, Location location, Style style) {
		super(town, location, style);
	}
}
