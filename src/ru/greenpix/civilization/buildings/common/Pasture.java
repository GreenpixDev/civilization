package ru.greenpix.civilization.buildings.common;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.GuiseBuilding;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.guises.GuiseContainer;
import ru.greenpix.civilization.guises.GuiseContainerBuilder;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.developer.utils.guises.GuiseInterface;
import ru.greenpix.developer.utils.items.Item;
import ru.greenpix.mysql.api.Result;

public class Pasture extends Building implements Runnable, GuiseBuilding {

	private final GuiseContainer container = new GuiseContainerBuilder(6)
			.title(getDisplayName())
			.defaultInteractionZone()
			.defaultFrame()
			.addItem(4, 5, new Item(Material.SEEDS), i -> {
				i.setDisplayName("&f&lПрирост: &e&l" + getTown().getGrowthPerMinute() + "/мин");
				i.setLore("&fЧем больше прироста, тем больше добычи",
						"&aКликни, чтобы &lОБНОВИТЬ");
			})
			.build();
	
	public Pasture(Town town, Location location, Style style) {
		super(town, location, style);
	}
	
	public Pasture(Result result, Structure str) {
		super(result, str);
	}

	int ticks = 0;
	
	@Override
	public void run() {
		ticks++;
		if(ticks != 20) return;
		ticks = 0;
		if(Math.random() > getTown().getGrowthPerMinute() / 1500D) return;
		double d = Math.random();
		container.getContainer().addItem(
				d > 0.8 ? new ItemStack(Material.RAW_CHICKEN) : 
				d > 0.6 ? new ItemStack(Material.MUTTON) :
				d > 0.4 ? new ItemStack(Material.FEATHER) :
				d > 0.2 ? new ItemStack(Material.STRING) :
				new ItemStack(Material.LEATHER));
	}

	@Override
	public GuiseInterface getGuise() {
		return container;
	}
}
