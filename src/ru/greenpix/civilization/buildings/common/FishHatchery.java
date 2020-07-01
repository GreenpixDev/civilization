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

public class FishHatchery extends Building implements Runnable, GuiseBuilding {

	private final GuiseContainer container = new GuiseContainerBuilder(6)
			.title(getDisplayName())
			.defaultInteractionZone()
			.defaultFrame()
			.addItem(4, 5, new Item(Material.FISHING_ROD), i -> {
				i.setDisplayName("&f&lРыба: &e&l" + 60 + "/мин");
				i.setLore("&fСтабильная добыча рыбы",
						"&fНе зависит от прироста");
			})
			.build();
	
	public FishHatchery(Town town, Location location, Style style) {
		super(town, location, style);
	}
	
	public FishHatchery(Result result, Structure str) {
		super(result, str);
	}

	int ticks = 0;
	
	@Override
	public void run() {
		ticks++;
		if(ticks != 20) return;
		ticks = 0;
		double d = Math.random();
		container.getContainer().addItem(
				d > 0.7 ? new ItemStack(Material.RAW_FISH) : 
				d > 0.4 ? new ItemStack(Material.RAW_FISH, 1, (short) 1) :
				d > 0.1 ? new ItemStack(Material.RAW_FISH, 1, (short) 2) :
				new ItemStack(Material.RAW_FISH, 1, (short) 3));
	}

	@Override
	public GuiseInterface getGuise() {
		return container;
	}
}
