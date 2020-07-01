package ru.greenpix.civilization.items.crafts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.guises.GuiseElement;
import ru.greenpix.civilization.guises.GuiseRecipes;
import ru.greenpix.developer.PluginPlayer;
import ru.greenpix.developer.utils.guises.Guise;
import ru.greenpix.developer.utils.guises.GuiseInterface;
import ru.greenpix.developer.utils.guises.GuiseItem;
import ru.greenpix.developer.utils.guises.GuiseItem.GuiseItemAction;
import ru.greenpix.developer.utils.items.Item;

public class CustomRecipe implements GuiseElement {
	
	private static final List<CustomRecipe> recipes = new ArrayList<CustomRecipe>();
	
	private Map<Material, Integer> cache = null;
	
	public final int width;
	
	public final int height;
	
	private final Object[] ingredients;
	
	private final Item result;
	
	private final String name;
	
	public CustomRecipe(String name, Item item, int width, int height) {
		this.name = name;
		this.result = item;
		this.width = width;
		this.height = height;
		this.ingredients = new Item[width * height];
		recipes.add(this);
	}
	
	public static List<CustomRecipe> getRecipes() {
		return recipes;
	}
	
	public static CustomRecipe getByName(String name) {
		return recipes.stream()
			.filter(r -> r.getName().equals(name))
			.findFirst().orElse(null);
	}

	public String getName() {
		return name;
	}

	public Item getResult() {
		return result;
	}
	
	public Object getIngredient(int x, int y) {
		return ingredients[y * width + x];
	}
	
	public void setIngredient(int x, int y, Material ingredient) {
		ingredients[y * width + x] = ingredient;
		cache = null;
	}
	
	public void setIngredient(int x, int y, Item ingredient) {
		ingredients[y * width + x] = ingredient;
		cache = null;
	}
	
	public Map<Material, Integer> getIngredientMap() {
		if(cache != null) return cache;
		Map<Material, Integer> map = new HashMap<>();
		Material m;
		for(Object i : this.ingredients) {
			if(i != null) {
				m = i instanceof Item ? ((Item) i).getType() : (Material) i;
				if(!map.containsKey(m)) map.put(m, 1);
				else map.put(m, map.get(m) + 1);
			}
		}
		return map;
	}
	
	public boolean equalsIngredientMap(Map<Material, Integer> map) {
		Map<Material, Integer> m = getIngredientMap();
		if(m.size() != map.size()) return false;
		for(Map.Entry<Material, Integer> entry : map.entrySet()) {
			if(entry.getValue() != m.get(entry.getKey())) return false;
		}
		return true;
	}
	
	public boolean isCraft(CraftingInventory inv, int xShift, int yShift) {
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				Object i = getIngredient(x, y);
				ItemStack is = inv.getItem((y + yShift) * 3 + (x + xShift) + 1);
				if(i instanceof Item) {
					// Так здесь используется обычный ItemStack::isSimular, 
					// но тогда возникает проблема с прочностью и аттрибутами
					/*
					if(!(i == null && (is == null || is.getType() == Material.AIR)) && ((i == null && is != null) || (i != null && is == null) || !CustomItems.isSimular((Item) i, new Item(is)))) {
						return false;
					} 
					*/
					if(!(i == null && (is == null || is.getType() == Material.AIR)) && ((i == null && is != null) || (i != null && is == null) || !((Item) i).isSimular(is))) {
						return false;
					} 
				} else {
					if(!(i == null && (is == null || is.getType() == Material.AIR)) && ((i == null && is != null) || (i != null && is == null) || is.getType() != (Material) i)) {
						return false;
					} 
				}
			}
		}
		return true;
	}

	@Override
	public Item getIcon() {
		return getResult();
	}

	@Override
	public GuiseItemAction getClickAction(String type) {
		return (player, item) -> {
			if(type.equalsIgnoreCase("view")) {
				new GuiseCraft(PluginPlayer.getPlayer(player).getOpenGuise(), this).open(player);
			} else if(type.equalsIgnoreCase("admin")) {
				player.getInventory().addItem(getResult().getCopy());
			}
		};
	}

	@Override
	public GuiseItemAction getLoadAction(String type) {
		return null;
	}
	
	class GuiseCraft extends Guise {
		
		public GuiseCraft(GuiseInterface parent, CustomRecipe recipe) {
			super(CivCore.getInstance(), 45, "Рецепт", recipe.getName());
			super.setItemEmpty(new Item(160, 15));
			addItem(0, 4, new GuiseItem(GuiseRecipes.BACK, (p,i) -> parent.open(p), null));
			for(int x = 0; x < 3; x++) {
				for(int y = 0; y < 3; y++) {
					if(x >= recipe.width || y >= recipe.height) {
						addItem(x + 3, y + 1, new GuiseItem(new Item(Material.AIR)));
						continue;
					}
					Object o = recipe.getIngredient(x, y);
					if(o == null) {
						addItem(x + 3, y + 1, new GuiseItem(new Item(Material.AIR)));
					} else if(o instanceof Item) {
						addItem(x + 3, y + 1, new GuiseItem((Item) o, (p,i) -> {
							if(!i.isValidTags() || !i.getTags().hasKey("CivcraftField")) return;
							CustomRecipe rec = CustomRecipe.getRecipes().stream()
									.filter(r -> r.getResult().getTags().hasKey("CivcraftField") &&
											r.getResult().getTags().getString("CivcraftField")
											.equals(i.getTags().getString("CivcraftField")))
									.findFirst().orElse(null);
							if(rec != null) new GuiseCraft(this, rec).open(p);
						}, null));
					} else {
						addItem(x + 3, y + 1, new GuiseItem(new Item((Material) o)));
					}
				}
			}
		}
	}
}
