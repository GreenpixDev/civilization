package ru.greenpix.civilization.buildings.common;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.GuiseBuilding;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.buildings.wonders.NotreDame;
import ru.greenpix.civilization.guises.GuiseContainer;
import ru.greenpix.civilization.guises.GuiseContainerBuilder;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.developer.utils.guises.GuiseInterface;
import ru.greenpix.developer.utils.items.Item;
import ru.greenpix.mysql.api.Result;

public class Farm extends Building implements Runnable, GuiseBuilding {

	private final GuiseContainer container = new GuiseContainerBuilder(6)
			.title(getDisplayName())
			.defaultInteractionZone()
			.defaultFrame()
			.addItem(4, 5, new Item(Material.SEEDS), i -> {
				i.setDisplayName("&f&lПрирост: &e&l" + getTown().getGrowthPerMinute() + "/мин");
				i.setLore("&fЧем больше прироста, тем больше еды",
						"&aКликни, чтобы &lОБНОВИТЬ");
			})
			.build();
	
	public Farm(Town town, Location location, Style style) {
		super(town, location, style);
	}
	
	public Farm(Result result, Structure str) {
		super(result, str);
	}
	
	@Override
	public void damage(CivPlayer damager, Block block) {
		if(getTown().hasBuildings(NotreDame.class)) {
			damager.sendMessage("&cПрежде, чем разрушить эту постройку, тебе нужно разрушить 'Висячие Сады'");
		} else super.damage(damager, block);
	}

	int ticks = 0;
	
	@Override
	public void run() {
		ticks++;
		if(ticks != 10) return;
		ticks = 0;
		if(Math.random() > getTown().getGrowthPerMinute() / 1500D) return;
		double d = Math.random();
		container.getContainer().addItem(
				d > 0.6 ? new ItemStack(Material.WHEAT) : 
				d > 0.4 ? new ItemStack(Material.POTATO_ITEM) :
				d > 0.2 ? new ItemStack(Material.CARROT_ITEM) :
				d > 0.02 ? new ItemStack(Material.SUGAR_CANE) :
				d > 0.01 ? new ItemStack(Material.PUMPKIN) :
				new ItemStack(Material.MELON_BLOCK));
	}

	@Override
	public GuiseInterface getGuise() {
		return container;
	}
}
