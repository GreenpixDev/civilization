package ru.greenpix.civilization.guises;

import java.util.stream.Collectors;

import ru.greenpix.civilization.items.CustomItems;
import ru.greenpix.civilization.items.Tier;
import ru.greenpix.civilization.items.crafts.CustomRecipe;
import ru.greenpix.developer.utils.guises.Guise;
import ru.greenpix.developer.utils.guises.GuiseItem;
import ru.greenpix.developer.utils.items.Item;

public class GuiseRecipes extends Guise {

	public static final Item BACK = Item.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0=");
	
	public GuiseRecipes(boolean onlyView) {
		super(new GuiseBuilder(3).title("Рецепты").defaultEmpty().build());
		for(Tier tier : Tier.values()) {
			GuiseList<CustomRecipe> guise = new GuiseList<>(onlyView ? "view" : "admin", "Рецепты " + tier, 
					CustomItems.getItems().entrySet().stream()
					.filter(e -> e.getValue() != null && e.getValue().tier() == tier)
					.map(e -> CustomRecipe.getRecipes().stream()
							.filter(r -> r.getResult().equals(e.getKey()))
							.findFirst().orElse(null))	
					.filter(e -> e != null)
					.collect(Collectors.toList()));
			guise.addItem(0, 5, new GuiseItem(GuiseRecipes.BACK, (p,i) -> this.open(p), null));
			addItem(new GuiseItem(tier.icon(), (p,i) -> guise.open(p), null));
		}
	}
	
	public GuiseRecipes() {
		this(true);
	}
}
