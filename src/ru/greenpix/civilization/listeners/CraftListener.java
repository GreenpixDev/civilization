package ru.greenpix.civilization.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.items.CustomItems;
import ru.greenpix.civilization.items.ItemCivcraft;
import ru.greenpix.civilization.items.crafts.CustomRecipe;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.technologies.Technology;
import ru.greenpix.developer.utils.items.Item;
import ru.greenpix.developer.utils.protocol.sounds.FixedSound;

public class CraftListener implements Listener {

	public static final List<ItemStack> vanilla = new ArrayList<>();
	
	{
		String[] items = new String[] {"SWORD", "PICKAXE", "AXE", "BOOTS", "LEGGINGS", "CHESTPLATE", "HELMET"};
		String[] types = new String[] {"IRON", "GOLD", "DIAMOND", "LEATHER"};
		Material m;
		for(String type : types) {
			for(String i : items) {
				m = Material.getMaterial(type + "_" + i);
				if(m != null) vanilla.add(new ItemStack(m));
			}
		}
	}
	
	@EventHandler
	private void onFurnace(FurnaceSmeltEvent e) {
		CustomItems.FURNACE_RECIPES.forEach((source, result) -> {
			if(source.isSimular(e.getSource())) {
				e.setResult(result.getHandle());
			}
		});
	}
	
	@EventHandler
	private void onCraft(CraftItemEvent e) {
		if(e.isCancelled()) return;
		if(e.getRecipe() == null) return;
		Item item = new Item(e.getInventory().getItem(0));
		if(item.getHandle() == null || !item.isValidTags() || !item.getTags().hasKey("CivcraftField")) return;
		ItemCivcraft info = CustomItems.getItemInfo(item.getTags().getString("CivcraftField"));
		if(info == null) return;
		if(!info.technology().equals("null")) {
			CivPlayer player = CivPlayer.getByName(e.getWhoClicked().getName());
			if(player.getCivilization() == null || !player.getCivilization().hasTechnology(info.technology())) {
				e.setCancelled(true);
				FixedSound.ITEM_BREAK.playSound(player.toBukkit(), 1F, 2F);
				player.sendMessage("&cВы не можете скрафтить этот предмет. Для этого нужно изучить технологию '" + Technology.getByName(info.technology()).getDisplayName() + "'.");
				return;
			}
		}
	}
	
	@EventHandler
	private void onPreCraft(PrepareItemCraftEvent e) {
		if(e.getRecipe() == null) return;
		// key - ингредиент, value - слот
		Map<Material, Integer> ingredients = new HashMap<>();
		ItemStack is;
		for(int i = 1; i < 10; i++) {
			is = e.getInventory().getItem(i);
			if(is != null) {
				if(!ingredients.containsKey(is.getType())) ingredients.put(is.getType(), 1);
				else ingredients.put(is.getType(), ingredients.get(is.getType()) + 1);
			}
		}
		// Получаем все крафты с ингредиентам
		Set<CustomRecipe> all = CustomRecipe.getRecipes().stream()
				.filter(r -> r.equalsIngredientMap(ingredients))
				.collect(Collectors.toSet());
		for(CustomRecipe r : all) {
			for(int x = 0; x < 4 - r.width; x++) {
				for(int y = 0; y < 4 - r.height; y++) {
					if(r.isCraft(e.getInventory(), x, y)) {
						e.getInventory().setItem(0, r.getResult().getHandle());
						return;
					}
				}
			}
		}
		if(e.getRecipe() instanceof ShapedRecipe && ((ShapedRecipe) e.getRecipe()).getKey().getNamespace().equalsIgnoreCase(CivCore.getInstance().getName())) {
			e.getInventory().setItem(0, new ItemStack(Material.AIR));
			return;
		}
		for(int i = 1; i < 10; i++) {
			ItemStack ist = e.getInventory().getItem(i);
			// Так здесь используется обычный ItemStack::isSimular, 
			// но тогда возникает проблема с прочностью и аттрибутами
			if(ist != null && CustomItems.ALL.stream().filter(it -> it.isSimular(ist)).count() != 0) {
				e.getInventory().setItem(0, new ItemStack(Material.AIR));
				return;
			}
		}
		for(ItemStack stack : vanilla) {
			if(e.getRecipe().getResult().isSimilar(stack)) {
				e.getInventory().setItem(0, new ItemStack(Material.AIR));
				break;
			}
		}
	}
}
