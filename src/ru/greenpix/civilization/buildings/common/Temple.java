package ru.greenpix.civilization.buildings.common;

import org.bukkit.Location;
import org.bukkit.block.Block;

import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.buildings.wonders.NotreDame;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.mysql.api.Result;

public class Temple extends Building {

	public Temple(Town town, Location location, Style style) {
		super(town, location, style);
	}
	
	public Temple(Result result, Structure str) {
		super(result, str);
	}
	
	@Override
	public void damage(CivPlayer damager, Block block) {
		if(getTown().hasBuildings(NotreDame.class)) {
			damager.sendMessage("&cПрежде, чем разрушить эту постройку, тебе нужно разрушить 'Нотр Дам'");
		} else super.damage(damager, block);
	}
	
}
