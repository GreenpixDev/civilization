package ru.greenpix.civilization.items;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import ru.greenpix.civilization.items.crafts.Crafts;
import ru.greenpix.civilization.items.crafts.FurnaceRecipe;
import ru.greenpix.civilization.items.crafts.RecipeBuilder;
import ru.greenpix.developer.utils.items.Item;

import static ru.greenpix.civilization.items.Tier.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static ru.greenpix.civilization.items.AttributeType.*;

public interface CustomItems {
	
	Set<Item> ALL = new HashSet<Item>();
	
	Map<Item, Item> FURNACE_RECIPES = new HashMap<Item, Item>();
	
	/*
	 *  T1
	 */
	
	@ItemCivcraft(tier = T1, technology = "blacksmith_craft")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {2, -0.01}, mode = {0, 1}, slot = Slot.HEAD)
	Item COPPER_HELMET = new Item(Material.GOLD_HELMET, "Медный Шлем");
	
	@ItemCivcraft(tier = T1, technology = "blacksmith_craft")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {6, -0.01}, mode = {0, 1}, slot = Slot.CHEST)
	Item COPPER_CHESTPLATE = new Item(Material.GOLD_CHESTPLATE, "Медный Нагрудник");
	
	@ItemCivcraft(tier = T1, technology = "blacksmith_craft")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {5, -0.01}, mode = {0, 1}, slot = Slot.LEGS)
	Item COPPER_LEGGINGS = new Item(Material.GOLD_LEGGINGS, "Медные Штаны");
	
	@ItemCivcraft(tier = T1, technology = "blacksmith_craft")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {2, -0.01}, mode = {0, 1}, slot = Slot.FEET)
	Item COPPER_BOOTS = new Item(Material.GOLD_BOOTS, "Медные Ботинки");
	
	@FurnaceRecipe(result = "COPPER_INGOT")
	Item COPPER_ORE = new Item(Material.GOLD_ORE, "&fМедная Руда");
	
	@ItemCivcraft(tier = T1)
	Item COPPER_INGOT = new Item(Material.GOLD_INGOT, "Медный Слиток");
	
	@ItemCivcraft(tier = T1, technology = "metal_casting")
	@ItemData(unbreakable = true)
	@Attributes(type = {ATTACK_DAMAGE, ATTACK_SPEED}, amount = {7, -2.2}, slot = Slot.MAINHAND)
	Item COPPER_SWORD = new Item(Material.GOLD_SWORD, "Медный Меч");
	
	@ItemCivcraft(tier = T1, technology = "metal_casting")
	@ItemData(unbreakable = true)
	@Attributes(type = {ATTACK_DAMAGE, ATTACK_SPEED}, amount = {9, -3.3}, slot = Slot.MAINHAND)
	Item COPPER_AXE = new Item(Material.GOLD_AXE, "Медная Секира");
	
	@ItemCivcraft(tier = T1, technology = "metal_casting")
	@ItemData(unbreakable = true)
	@Attributes(type = {MAX_HEALTH, MOVEMENT_SPEED}, amount = {2, -0.03}, mode = {0, 1}, slot = Slot.OFFHAND)
	Item COPPER_SHIELD = new Item(Material.SHIELD, "Медный Щит");
	
	@ItemCivcraft(tier = T1, technology = "metal_casting")
	@ItemData(unbreakable = true)
	Item COPPER_PICKAXE = new Item(Material.GOLD_PICKAXE, "Медная Кирка");
	
	@ItemCivcraft(tier = T1, technology = "archery")
	@ItemData(unbreakable = true, idEnchants = {48, 51}, lvlEnchants = {1, 1})
	Item COMMON_BOW = new Item(Material.BOW, "Охотничий Лук");
	
	@ItemCivcraft(tier = T1)
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {1, 0.01}, mode = {0, 1}, slot = Slot.HEAD)
	Item LEATHER_HELMET = new Item(Material.LEATHER_HELMET, "Кожаная Шапка");
	
	@ItemCivcraft(tier = T1)
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {4, 0.01}, mode = {0, 1}, slot = Slot.CHEST)
	Item LEATHER_CHESTPLATE = new Item(Material.LEATHER_CHESTPLATE, "Кожаная Куртка");
	
	@ItemCivcraft(tier = T1)
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {3, 0.01}, mode = {0, 1}, slot = Slot.LEGS)
	Item LEATHER_LEGGINGS = new Item(Material.LEATHER_LEGGINGS, "Кожаные Штаны");
	
	@ItemCivcraft(tier = T1)
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {1, 0.01}, mode = {0, 1}, slot = Slot.FEET)
	Item LEATHER_BOOTS = new Item(Material.LEATHER_BOOTS, "Кожаные Ботинки");
	
	/*
	 * 	T2
	 */
	
	@ItemCivcraft(tier = T2, technology = "metallurgy")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {3, -0.02}, mode = {0, 1}, slot = Slot.HEAD)
	Item IRON_HELMET = new Item(Material.IRON_HELMET, "Железный Шлем");
	
	@ItemCivcraft(tier = T2, technology = "metallurgy")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {8, -0.02}, mode = {0, 1}, slot = Slot.CHEST)
	Item IRON_CHESTPLATE = new Item(Material.IRON_CHESTPLATE, "Железный Нагрудник");
	
	@ItemCivcraft(tier = T2, technology = "metallurgy")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {6, -0.02}, mode = {0, 1}, slot = Slot.LEGS)
	Item IRON_LEGGINGS = new Item(Material.IRON_LEGGINGS, "Железные Штаны");
	
	@ItemCivcraft(tier = T2, technology = "metallurgy")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {3, -0.02}, mode = {0, 1}, slot = Slot.FEET)
	Item IRON_BOOTS = new Item(Material.IRON_BOOTS, "Железные Ботинки");
	
	@ItemCivcraft(tier = T2)
	Item IRON_PLATE = new Item(Material.IRON_PLATE, "Железная Пластина");
	
	@ItemCivcraft(tier = T2, technology = "forging")
	@ItemData(unbreakable = true)
	@Attributes(type = {ATTACK_DAMAGE, ATTACK_SPEED}, amount = {8, -2.3}, slot = Slot.MAINHAND)
	Item IRON_SWORD = new Item(Material.IRON_SWORD, "Железный Меч");
	
	@ItemCivcraft(tier = T2, technology = "forging")
	@ItemData(unbreakable = true)
	@Attributes(type = {ATTACK_DAMAGE, ATTACK_SPEED}, amount = {10, -3.32}, slot = Slot.MAINHAND)
	Item IRON_AXE = new Item(Material.IRON_AXE, "Железная Секира");
	
	@ItemCivcraft(tier = T2, technology = "forging")
	@ItemData(unbreakable = true)
	@Attributes(type = {MAX_HEALTH, MOVEMENT_SPEED}, amount = {4, -0.06}, mode = {0, 1}, slot = Slot.OFFHAND)
	Item IRON_SHIELD = new Item(Material.SHIELD, "Железный Щит");
	
	@ItemCivcraft(tier = T2, technology = "forging")
	@ItemData(unbreakable = true, idEnchants = 32, lvlEnchants = 3)
	Item IRON_PICKAXE = new Item(Material.IRON_PICKAXE, "Железная Кирка");
	
	@ItemCivcraft(tier = T2, technology = "arrow_plumage")
	@ItemData(unbreakable = true, idEnchants = {48, 51}, lvlEnchants = {3, 1})
	Item LONG_BOW = new Item(Material.BOW, "Длинный Лук");
	
	@ItemCivcraft(tier = T2, technology = "leather_dressing")
	@ItemData(unbreakable = true)
	@ItemColor(red = 255, green = 204, blue = 0)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {2, 0.02}, mode = {0, 1}, slot = Slot.HEAD)
	Item CARVED_LEATHER_HELMET = new Item(Material.LEATHER_HELMET, "Изысканная Шапка");
	
	@ItemCivcraft(tier = T2, technology = "leather_dressing")
	@ItemData(unbreakable = true)
	@ItemColor(red = 255, green = 204, blue = 0)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {6, 0.02}, mode = {0, 1}, slot = Slot.CHEST)
	Item CARVED_LEATHER_CHESTPLATE = new Item(Material.LEATHER_CHESTPLATE, "Изысканная Куртка");
	
	@ItemCivcraft(tier = T2, technology = "leather_dressing")
	@ItemData(unbreakable = true)
	@ItemColor(red = 255, green = 204, blue = 0)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {5, 0.02}, mode = {0, 1}, slot = Slot.LEGS)
	Item CARVED_LEATHER_LEGGINGS = new Item(Material.LEATHER_LEGGINGS, "Изысканные Штаны");
	
	@ItemCivcraft(tier = T2, technology = "leather_dressing")
	@ItemData(unbreakable = true)
	@ItemColor(red = 255, green = 204, blue = 0)
	@Attributes(type = {ARMOR, MOVEMENT_SPEED}, amount = {2, 0.02}, mode = {0, 1}, slot = Slot.FEET)
	Item CARVED_LEATHER_BOOTS = new Item(Material.LEATHER_BOOTS, "Изысканные Ботинки");
	
	@ItemCivcraft(tier = T2)
	Item CARVED_LEATHER = new Item(Material.LEATHER, "Резная Кожа");
	
	@ItemCivcraft(tier = T2)
	Item SILK_STRING = new Item(Material.STRING, "Шелковые Нитки");
	
	@ItemCivcraft(tier = T2)
	Item FORTIFIED_STICK = new Item(Material.STICK, "Укрепленная Палка");
	
	/*
	 * 	T3
	 */
	
	@ItemCivcraft(tier = T3, technology = "chemistry")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {5, 1, -0.03}, mode = {0, 0, 1}, slot = Slot.HEAD)
	Item STEEL_HELMET = new Item(Material.CHAINMAIL_HELMET, "Стальной Шлем");
	
	@ItemCivcraft(tier = T3, technology = "chemistry")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {10, 1, -0.03}, mode = {0, 0, 1}, slot = Slot.CHEST)
	Item STEEL_CHESTPLATE = new Item(Material.CHAINMAIL_CHESTPLATE, "Стальной Нагрудник");
	
	@ItemCivcraft(tier = T3, technology = "chemistry")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {8, 1, -0.03}, mode = {0, 0, 1}, slot = Slot.LEGS)
	Item STEEL_LEGGINGS = new Item(Material.CHAINMAIL_LEGGINGS, "Стальные Штаны");
	
	@ItemCivcraft(tier = T3, technology = "chemistry")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {4, 1, -0.03}, mode = {0, 0, 1}, slot = Slot.FEET)
	Item STEEL_BOOTS = new Item(Material.CHAINMAIL_BOOTS, "Стальные Ботинки");
	
	@ItemCivcraft(tier = T3)
	Item STEEL_INGOT = new Item(Material.IRON_INGOT, "Стальной Слиток");
	
	@ItemCivcraft(tier = T3, technology = "damask_steel")
	@ItemData(unbreakable = true)
	@Attributes(type = {ATTACK_DAMAGE, ATTACK_SPEED}, amount = {9, -2.4}, slot = Slot.MAINHAND)
	Item STEEL_SWORD = new Item(Material.STONE_SWORD, "Стальной Меч");
	
	@ItemCivcraft(tier = T3, technology = "damask_steel")
	@ItemData(unbreakable = true)
	@Attributes(type = {ATTACK_DAMAGE, ATTACK_SPEED}, amount = {11, -3.35}, slot = Slot.MAINHAND)
	Item STEEL_AXE = new Item(Material.STONE_AXE, "Стальная Секира");
	
	@ItemCivcraft(tier = T3, technology = "damask_steel")
	@ItemData(unbreakable = true)
	@Attributes(type = {MAX_HEALTH, MOVEMENT_SPEED}, amount = {6, -0.09}, mode = {0, 1}, slot = Slot.OFFHAND)
	Item STEEL_SHIELD = new Item(Material.SHIELD, "Стальной Щит");
	
	@ItemCivcraft(tier = T3, technology = "damask_steel")
	@ItemData(unbreakable = true, idEnchants = 32, lvlEnchants = 4)
	Item STEEL_PICKAXE = new Item(Material.STONE_PICKAXE, "Стальная Кирка");
	
	@ItemCivcraft(tier = T3, technology = "compound_bow")
	@ItemData(unbreakable = true, idEnchants = {48, 51}, lvlEnchants = {5, 1})
	Item COMPOUND_BOW = new Item(Material.BOW, "Составной Лук");
	
	@ItemCivcraft(tier = T3, technology = "tanning")
	@ItemData(unbreakable = true)
	@ItemColor(red = 21, green = 127, blue = 0)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {3, 1, 0.03}, mode = {0, 0, 1}, slot = Slot.HEAD)
	Item HARDENED_LEATHER_HELMET = new Item(Material.LEATHER_HELMET, "Закаленная Шапка");
	
	@ItemCivcraft(tier = T3, technology = "tanning")
	@ItemData(unbreakable = true)
	@ItemColor(red = 21, green = 127, blue = 0)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {8, 1,0.03}, mode = {0, 0, 1}, slot = Slot.CHEST)
	Item HARDENED_LEATHER_CHESTPLATE = new Item(Material.LEATHER_CHESTPLATE, "Закаленная Куртка");
	
	@ItemCivcraft(tier = T3, technology = "tanning")
	@ItemData(unbreakable = true)
	@ItemColor(red = 21, green = 127, blue = 0)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {6, 1, 0.03}, mode = {0, 0, 1}, slot = Slot.LEGS)
	Item HARDENED_LEATHER_LEGGINGS = new Item(Material.LEATHER_LEGGINGS, "Закаленные Штаны");
	
	@ItemCivcraft(tier = T3, technology = "tanning")
	@ItemData(unbreakable = true)
	@ItemColor(red = 21, green = 127, blue = 0)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {3, 1, 0.03}, mode = {0, 0, 1}, slot = Slot.FEET)
	Item HARDENED_LEATHER_BOOTS = new Item(Material.LEATHER_BOOTS, "Закаленные Ботинки");
	
	@ItemCivcraft(tier = T3)
	Item TANNED_LEATHER = new Item(Material.LEATHER, "Дубленная Кожа");
	
	@ItemCivcraft(tier = T3)
	Item STRONG_STRING = new Item(Material.STRING, "Прочные Нитки");
	
	@ItemCivcraft(tier = T3)
	Item METAL_STICK = new Item(Material.BONE, "Укрепленная Палка");
	
	/*
	 * 	T4
	 */
	
	@ItemCivcraft(tier = T4, technology = "metal_alloys")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {6, 2, -0.04}, mode = {0, 0, 1}, slot = Slot.HEAD)
	Item TUNGSTEN_HELMET = new Item(Material.DIAMOND_HELMET, "Вольфрамовый Шлем");
	
	@ItemCivcraft(tier = T4, technology = "metal_alloys")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {12, 2, -0.04}, mode = {0, 0, 1}, slot = Slot.CHEST)
	Item TUNGSTEN_CHESTPLATE = new Item(Material.DIAMOND_CHESTPLATE, "Вольфрамовый Нагрудник");
	
	@ItemCivcraft(tier = T4, technology = "metal_alloys")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {9, 2, -0.04}, mode = {0, 0, 1}, slot = Slot.LEGS)
	Item TUNGSTEN_LEGGINGS = new Item(Material.DIAMOND_LEGGINGS, "Вольфрамовые Штаны");
	
	@ItemCivcraft(tier = T4, technology = "metal_alloys")
	@ItemData(unbreakable = true)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {5, 2, -0.04}, mode = {0, 0, 1}, slot = Slot.FEET)
	Item TUNGSTEN_BOOTS = new Item(Material.DIAMOND_BOOTS, "Вольфрамовые Ботинки");
	
	@FurnaceRecipe(result = "TUNGSTEN_INGOT")
	Item TUNGSTEN_ORE = new Item(Material.DIAMOND_ORE, "&fВольфрамовая Руда");
	
	@ItemCivcraft(tier = T4)
	Item TUNGSTEN_INGOT = new Item(Material.IRON_INGOT, "Вольфрамовый Слиток");
	
	@ItemCivcraft(tier = T4)
	Item TUNGSTEN_PLATE = new Item(Material.GOLD_PLATE, "Вольфрамовая Пластина");
	
	@ItemCivcraft(tier = T4, technology = "alloying")
	@ItemData(unbreakable = true)
	@Attributes(type = {ATTACK_DAMAGE, ATTACK_SPEED}, amount = {11, -2.5}, slot = Slot.MAINHAND)
	Item TUNGSTEN_SWORD = new Item(Material.DIAMOND_SWORD, "Вольфрамовый Меч");
	
	@ItemCivcraft(tier = T4, technology = "alloying")
	@ItemData(unbreakable = true)
	@Attributes(type = {ATTACK_DAMAGE, ATTACK_SPEED}, amount = {13, -3.4}, slot = Slot.MAINHAND)
	Item TUNGSTEN_AXE = new Item(Material.DIAMOND_AXE, "Вольфрамовая Секира");
	
	@ItemCivcraft(tier = T4, technology = "alloying")
	@ItemData(unbreakable = true)
	@Attributes(type = {MAX_HEALTH, MOVEMENT_SPEED}, amount = {10, -0.12}, mode = {0, 1}, slot = Slot.OFFHAND)
	Item TUNGSTEN_SHIELD = new Item(Material.SHIELD, "Вольфрамовый Щит");
	
	@ItemCivcraft(tier = T4, technology = "alloying")
	@ItemData(unbreakable = true, idEnchants = 32, lvlEnchants = 3)
	Item TUNGSTEN_PICKAXE = new Item(Material.DIAMOND_PICKAXE, "Вольфрамовая Кирка");
	
	@ItemCivcraft(tier = T4, technology = "lamination")
	@ItemData(unbreakable = true, idEnchants = {48, 51}, lvlEnchants = {7, 1})
	Item CROSSBOW = new Item(Material.BOW, "Арбалет");
	
	@ItemCivcraft(tier = T4, technology = "composites_chemistry")
	@ItemData(unbreakable = true)
	@ItemColor(red = 255, green = 0, blue = 0)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {5, 2, 0.04}, mode = {0, 0, 1}, slot = Slot.HEAD)
	Item COMPOSITE_HELMET = new Item(Material.LEATHER_HELMET, "Композитный Шлем");
	
	@ItemCivcraft(tier = T4, technology = "composites_chemistry")
	@ItemData(unbreakable = true)
	@ItemColor(red = 255, green = 0, blue = 0)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {10, 2, 0.04}, mode = {0, 0, 1}, slot = Slot.CHEST)
	Item COMPOSITE_CHESTPLATE = new Item(Material.LEATHER_CHESTPLATE, "Композитная Куртка");
	
	@ItemCivcraft(tier = T4, technology = "composites_chemistry")
	@ItemData(unbreakable = true)
	@ItemColor(red = 255, green = 0, blue = 0)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {8, 2, 0.04}, mode = {0, 0, 1}, slot = Slot.LEGS)
	Item COMPOSITE_LEGGINGS = new Item(Material.LEATHER_LEGGINGS, "Композитные Штаны");
	
	@ItemCivcraft(tier = T4, technology = "composites_chemistry")
	@ItemData(unbreakable = true)
	@ItemColor(red = 255, green = 0, blue = 0)
	@Attributes(type = {ARMOR, ARMOR_TOUGHNESS, MOVEMENT_SPEED}, amount = {4, 2, 0.04}, mode = {0, 0, 1}, slot = Slot.FEET)
	Item COMPOSITE_BOOTS = new Item(Material.LEATHER_BOOTS, "Композитные Ботинки");
	
	@ItemCivcraft(tier = T4)
	Item COMPOSITE = new Item(Material.PRISMARINE_SHARD, "Композит");
	
	@ItemCivcraft(tier = T4)
	Item SYNTHESIZED_STRING = new Item(Material.STRING, "Синтезированные Нитки");
	
	@ItemCivcraft(tier = T4)
	Item CARBIDE_STICK = new Item(Material.BLAZE_ROD, "Карбидовая Палка");
	
	static Item getItem(String fieldName) {
		try {
			return (Item) CustomItems.class.getField(fieldName).get(null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}
	
	static ItemCivcraft getItemInfo(String fieldName) {
		Field field;
		try {
			field = CustomItems.class.getField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
		
		return field.getAnnotation(ItemCivcraft.class);
	}
	
	static Map<Item, ItemCivcraft> getItems() {
		Map<Item, ItemCivcraft> map = new LinkedHashMap<Item, ItemCivcraft>();
		Item item;
		for(Field field : CustomItems.class.getFields()) {
			if(field.getType() != Item.class) continue;
			try {
				item = (Item) field.get(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}
			map.put(item, field.getAnnotation(ItemCivcraft.class));
		}
		return map;
	}
	
	static void initAnnotations() {
		ItemCivcraft civ;
		ItemData data;
		ItemColor color;
		Attributes att;
		FurnaceRecipe furnace;
		Slot slot;
		Item item;
		Item result;
		for(Field field : CustomItems.class.getFields()) {
			if(field.getType() != Item.class) continue;
			try {
				item = (Item) field.get(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}
			item.getTags().setString("CivcraftField", field.getName());
			att = field.getAnnotation(Attributes.class);
			if(att != null) {
				for(int i = 0; i < att.amount().length; i++) {
					slot = i >= att.slot().length ? att.slot()[0] : att.slot()[i];
					Item.attributeOperation = i >= att.mode().length ? att.mode()[0] : att.mode()[i];
					item.getTags().setAttribute(att.type()[i].attributeName(), att.amount()[i], slot.name().toLowerCase());
				}
			}
			civ = field.getAnnotation(ItemCivcraft.class);
			if(civ != null) {
				item.setDisplayName(civ.tier().color() + item.getDisplayName());
				item.addLore("&eУровень: " + civ.tier().color() + civ.tier().name());
			}
			data = field.getAnnotation(ItemData.class);
			if(data != null) {
				ItemMeta meta = item.getMeta();
				meta.setUnbreakable(data.unbreakable());
				meta.addItemFlags(data.flags());
				item.setMeta(meta);
				if(!data.skull().isEmpty()) item.setSkull(data.skull());
				if(data.idEnchants().length == data.lvlEnchants().length) {
					for(int i = 0; i < data.idEnchants().length; i++) {
						item.addEnchant(data.idEnchants()[i], data.lvlEnchants()[i]);
					}
				}
			}
			color = field.getAnnotation(ItemColor.class);
			if(color != null) {
				item.setColor(Color.fromRGB(color.red(), color.green(), color.blue()));
			}
			furnace = field.getAnnotation(FurnaceRecipe.class);
			if(furnace != null) {
				result = getItem(furnace.result());
				Bukkit.addRecipe(new org.bukkit.inventory.FurnaceRecipe(result.getHandle(), item.getType()));
				FURNACE_RECIPES.put(item, result);
			}
			ALL.add(item);
		}
		Item.attributeOperation = 0;
		initShieldBanner(COPPER_SHIELD, DyeColor.BROWN);
		initShieldBanner(IRON_SHIELD, DyeColor.WHITE);
		initShieldBanner(STEEL_SHIELD, DyeColor.SILVER);
		initShieldBanner(TUNGSTEN_SHIELD, DyeColor.CYAN);
	}
	
	static void initShieldBanner(Item item, DyeColor base, Pattern... patterns) {
		BlockStateMeta meta = ((BlockStateMeta) item.getMeta());
		Banner banner = (Banner) meta.getBlockState();
		banner.setBaseColor(base);
		for(Pattern p : patterns) {
			banner.addPattern(p);
		}
		meta.setBlockState(banner);
		item.setMeta(meta);
	}
	
	static void initRecipes() {
		// T1
		Crafts.ARMOR_PACK.clone()
		.bindAll('A', COPPER_INGOT)
		.registerAll("copper", COPPER_HELMET, COPPER_CHESTPLATE, COPPER_LEGGINGS, COPPER_BOOTS);
		Crafts.ARMOR_PACK.clone()
		.bindAll('A', Material.LEATHER)
		.registerAll("leather", LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS);
		Crafts.TOOL_PACK.clone()
		.bindAll('A', COPPER_INGOT)
		.bindAll('B', Material.STICK)
		.registerAll("copper", COPPER_SWORD, COPPER_AXE, COPPER_PICKAXE);
		Crafts.SHIELD_RECIPE.clone()
		.bindAll('A', COPPER_INGOT)
		.bindAll('B', Material.WOOD)
		.registerAll("copper", COPPER_SHIELD);
		Crafts.BOW_RECIPE.clone()
		.bindAll('A', Material.STRING)
		.bindAll('B', Material.STICK)
		.registerAll("common", COMMON_BOW);
		// T2
		new RecipeBuilder("iron_plate", IRON_PLATE)
		.addShape("AAA")
		.bind('A', Material.IRON_INGOT).registerAndCreate();
		Crafts.ARMOR_PACK.clone()
		.bindAll('A', IRON_PLATE)
		.registerAll("iron", IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS);
		Crafts.ARMOR_PACK.clone()
		.bindAll('A', CARVED_LEATHER)
		.registerAll("carved_leather", CARVED_LEATHER_HELMET, CARVED_LEATHER_CHESTPLATE, CARVED_LEATHER_LEGGINGS, CARVED_LEATHER_BOOTS);
		Crafts.TOOL_PACK.clone()
		.bindAll('A', IRON_PLATE)
		.bindAll('B', FORTIFIED_STICK)
		.registerAll("iron", IRON_SWORD, IRON_AXE, IRON_PICKAXE);
		Crafts.SHIELD_RECIPE.clone()
		.bindAll('A', IRON_PLATE)
		.bindAll('B', Material.WOOD)
		.registerAll("iron", IRON_SHIELD);
		Crafts.BOW_RECIPE.clone()
		.bindAll('A', SILK_STRING)
		.bindAll('B', FORTIFIED_STICK)
		.registerAll("long", LONG_BOW);
		new RecipeBuilder("silk_string", SILK_STRING)
		.addShape("AAA", "AAA")
		.bind('A', Material.STRING).registerAndCreate();
		new RecipeBuilder("fortified_stick", FORTIFIED_STICK)
		.addShape("AAA", "AAA", "AAA")
		.bind('A', Material.STICK).registerAndCreate();
		new RecipeBuilder("carved_leather", CARVED_LEATHER)
		.addShape("BBB", "AAA")
		.addShape("AAA", "BBB")
		.bind('A', Material.LEATHER)
		.bind('B', Material.SUGAR_CANE).registerAndCreate();
		// T3
		new RecipeBuilder("steel_ingot", STEEL_INGOT)
		.addShape("ABA")
		.bind('A', Material.IRON_BLOCK)
		.bind('B', Material.COAL_BLOCK).registerAndCreate();
		Crafts.ARMOR_PACK.clone()
		.bindAll('A', STEEL_INGOT)
		.registerAll("steel", STEEL_HELMET, STEEL_CHESTPLATE, STEEL_LEGGINGS, STEEL_BOOTS);
		Crafts.ARMOR_PACK.clone()
		.bindAll('A', TANNED_LEATHER)
		.registerAll("hardened_leather", HARDENED_LEATHER_HELMET, HARDENED_LEATHER_CHESTPLATE, HARDENED_LEATHER_LEGGINGS, HARDENED_LEATHER_BOOTS);
		Crafts.TOOL_PACK.clone()
		.bindAll('A', STEEL_INGOT)
		.bindAll('B', METAL_STICK)
		.registerAll("steel", STEEL_SWORD, STEEL_AXE, STEEL_PICKAXE);
		Crafts.SHIELD_RECIPE.clone()
		.bindAll('A', STEEL_INGOT)
		.bindAll('B', Material.WOOD)
		.registerAll("steel", STEEL_SHIELD);
		Crafts.BOW_RECIPE.clone()
		.bindAll('A', STRONG_STRING)
		.bindAll('B', METAL_STICK)
		.registerAll("compound", COMPOUND_BOW);
		new RecipeBuilder("strong_string", STRONG_STRING)
		.addShape("BBB", "AAA", "BBB")
		.bind('A', SILK_STRING)
		.bind('B', Material.SUGAR_CANE).registerAndCreate();
		new RecipeBuilder("metal_stick", METAL_STICK)
		.addShape("BBB", "AAA", "BBB")
		.bind('A', FORTIFIED_STICK)
		.bind('B', Material.IRON_INGOT).registerAndCreate();
		new RecipeBuilder("tanned_leather", TANNED_LEATHER)
		.addShape("BBB", "AAA")
		.addShape("AAA", "BBB")
		.bind('A', CARVED_LEATHER)
		.bind('B', IRON_PLATE).registerAndCreate();
		// T4
		new RecipeBuilder("tungsten_plate", TUNGSTEN_PLATE)
		.addShape("AAA", "ABA", "AAA")
		.bind('A', TUNGSTEN_INGOT)
		.bind('B', Material.ANVIL).registerAndCreate();
		Crafts.ARMOR_PACK.clone()
		.bindAll('A', TUNGSTEN_PLATE)
		.registerAll("tungsten", TUNGSTEN_HELMET, TUNGSTEN_CHESTPLATE, TUNGSTEN_LEGGINGS, TUNGSTEN_BOOTS);
		Crafts.ARMOR_PACK.clone()
		.bindAll('A', COMPOSITE)
		.registerAll("composite", COMPOSITE_HELMET, COMPOSITE_CHESTPLATE, COMPOSITE_LEGGINGS, COMPOSITE_BOOTS);
		Crafts.TOOL_PACK.clone()
		.bindAll('A', TUNGSTEN_PLATE)
		.bindAll('B', CARBIDE_STICK)
		.registerAll("tungsten", TUNGSTEN_SWORD, TUNGSTEN_AXE, TUNGSTEN_PICKAXE);
		Crafts.SHIELD_RECIPE.clone()
		.bindAll('A', TUNGSTEN_PLATE)
		.bindAll('B', Material.WOOD)
		.registerAll("tungsten", TUNGSTEN_SHIELD);
		Crafts.BOW_RECIPE.clone()
		.bindAll('A', SYNTHESIZED_STRING)
		.bindAll('B', CARBIDE_STICK)
		.registerAll("cross", CROSSBOW);
		new RecipeBuilder("synthesized_string", SYNTHESIZED_STRING)
		.addShape("AAA", "AAA")
		.bind('A', STRONG_STRING).registerAndCreate();
		new RecipeBuilder("carbide_stick", CARBIDE_STICK)
		.addShape("CCC", "ABA", "CCC")
		.bind('A', STEEL_INGOT)
		.bind('B', METAL_STICK)
		.bind('C', Material.COAL_BLOCK).registerAndCreate();
		new RecipeBuilder("composite", COMPOSITE)
		.addShape("AAC", "BBC")
		.addShape("CAA", "CBB")
		.addShape("AA", "BB", "CC")
		.bind('A', STRONG_STRING)
		.bind('B', TANNED_LEATHER)
		.bind('C', Material.REDSTONE_BLOCK).registerAndCreate();
	}
	
}
