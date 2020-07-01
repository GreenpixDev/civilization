package ru.greenpix.civilization.buildings.wonders;

import org.bukkit.Location;

import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.mysql.api.Result;

public class TheGreatLibrary extends Wonder {

	public TheGreatLibrary(Result result, Structure str) {
		super(result, str);
	}
	
	public TheGreatLibrary(Town town, Location location, Style style) {
		super(town, location, style);
	}
}
