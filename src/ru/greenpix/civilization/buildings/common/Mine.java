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
import ru.greenpix.civilization.items.CustomItems;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.developer.utils.guises.GuiseInterface;
import ru.greenpix.developer.utils.items.Item;
import ru.greenpix.mysql.api.Result;

public class Mine extends Building implements Runnable, GuiseBuilding {

	private final GuiseContainer container = new GuiseContainerBuilder(6)
			.title(getDisplayName())
			.defaultInteractionZone()
			.defaultFrame()
			.addItem(4, 5, new Item(Material.ANVIL), i -> {
				i.setDisplayName("&f&lМолоточков: &e&l" + getTown().getHammersPerMinute() + "/мин");
				i.setLore("&fЧем больше молоточков, тем больше добычи",
						"&aКликни, чтобы &lОБНОВИТЬ");
			})
			.build();
	
	public Mine(Town town, Location location, Style style) {
		super(town, location, style);
	}
	
	public Mine(Result result, Structure str) {
		super(result, str);
	}

	int ticks = 0;
	
	@Override
	public void run() {
		ticks++;
		if(ticks != 20) return;
		ticks = 0;
		if(Math.random() > getTown().getHammersPerMinute() / 2500D) return;
		double d = Math.random();
		container.getContainer().addItem(
				d > 0.7 ? new ItemStack(Material.COBBLESTONE) : 
				d > 0.55 ? new ItemStack(Material.COAL) :
				d > 0.4 ? CustomItems.COPPER_ORE.getCopy() :
				d > 0.3 ? new ItemStack(Material.REDSTONE) :
				d > 0.2 ? new ItemStack(Material.INK_SACK, 0, (byte) 4) :
				d > 0.05 ? new ItemStack(Material.IRON_ORE) :
				d > 0.025 ? new ItemStack(Material.DIAMOND) :
				CustomItems.TUNGSTEN_ORE.getCopy());
	}

	@Override
	public GuiseInterface getGuise() {
		return container;
	}
}
