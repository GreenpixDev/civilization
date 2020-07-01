package ru.greenpix.civilization.items.crafts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.developer.utils.items.Item;

public class RecipeBuilder implements Cloneable {
	
	/**
	 *  @ingredient A - металл
	 *  @ingredient B - дерево
	 */
	
	public static final RecipeBuilder SWORD_PATTERN = 
			new RecipeBuilder().addShape("A", "A", "B");
	
	public static final RecipeBuilder AXE_PATTERN = 
			new RecipeBuilder().addShape("AA", "AB", " B").addShape("AA", "BA", "B ");
	
	public static final RecipeBuilder PICKAXE_PATTERN = 
			new RecipeBuilder().addShape("AAA", " B ", " B ");
	
	public static final RecipeBuilder SHIELD_PATTERN = 
			new RecipeBuilder().addShape("BAB", "BBB", " B ");
	
	/**
	 *  @ingredient A - металл
	 */
	
	public static final RecipeBuilder HELMET_PATTERN = 
			new RecipeBuilder().addShape("AAA", "A A");
	
	public static final RecipeBuilder CHESTPLATE_PATTERN = 
			new RecipeBuilder().addShape("AAA", "A A", "A A");
	
	public static final RecipeBuilder LEGGINGS_PATTERN = 
			new RecipeBuilder().addShape("A A", "AAA", "AAA");
	
	public static final RecipeBuilder BOOTS_PATTERN = 
			new RecipeBuilder().addShape("A A", "A A");
	
	/**
	 *  @ingredient A - нитки
	 *  @ingredient B - дерево
	 */
	
	public static final RecipeBuilder BOW_PATTERN = 
			new RecipeBuilder().addShape("AB ", "A B", "AB ").addShape(" BA", "B A", " BA");
	
	private String name;
	
	private Item result;
	
	private Set<CustomRecipe> recipes = new HashSet<>();
	
	private Set<ShapedRecipe> bukkit = new HashSet<>();
	
	private Set<String[]> shapes = new HashSet<>();
	
	private Map<Character, Object> ingredients = new HashMap<>();
	
	public RecipeBuilder() {
		this(null, null);
	}
	
	public RecipeBuilder(String name, Item result) {
		this.name = name;
		this.result = result;
	}
	
	public RecipeBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public RecipeBuilder setResult(Item result) {
		this.result = result;
		return this;
	}
	
	public RecipeBuilder addShape(String... shape) {
		shapes.add(shape);
		return this;
	}
	
	public RecipeBuilder bind(char key, Item ingredient) {
		this.ingredients.put(key, ingredient);
		return this;
	}
	
	public RecipeBuilder bind(char key, Material ingredient) {
		this.ingredients.put(key, new Item(ingredient));
		return this;
	}
	
	public RecipeBuilder register(Plugin plugin) {
		int id = 0;
		for(String[] shape : shapes) {
			NamespacedKey key = new NamespacedKey(plugin, name + "_" + id);
			ShapedRecipe recipe = new ShapedRecipe(key, result.getHandle());
			recipe.shape(shape);
			ingredients.forEach((k, i) -> recipe.setIngredient(k, (i instanceof Item ? ((Item) i).getType() : (Material) i)));
			Bukkit.addRecipe(recipe);
			bukkit.add(recipe);
			id++;
		}
		return this;
	}
	
	public CustomRecipe create() {
		CustomRecipe recipe = null;
		for(String[] shape : shapes) {
			int height = shape.length;
			int width = 1;
			for(String line : shape) {
				if(line.length() > width) width = line.length();
			}
			recipe = new CustomRecipe(name, result, width, height);
			Object i;
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {
					char c;
					if(x < shape[y].length()) c = shape[y].charAt(x);
					else c = ' ';
					if(c == ' ' || c == '-') recipe.setIngredient(x, y, (Material) null);
					else {
						i = ingredients.get(c);
						if(i instanceof Item) recipe.setIngredient(x, y, (Item) i);
						else recipe.setIngredient(x, y, (Material) i);
					}
				}
			}
			recipes.add(recipe);
		}
		return recipe;
	}
	
	public CustomRecipe registerAndCreate() {
		register(CivCore.getInstance());
		return create();
	}
	
	public Set<ShapedRecipe> getShapedRecipes() {
		return bukkit;
	}
	
	public Set<CustomRecipe> getCustomRecipes() {
		return recipes;
	}
	
	public String getName() {
		return name;
	}
	
	public Item getResult() {
		return result;
	}
	
	protected boolean containsKey(char key) {
		for(String[] shape : shapes) {
			for(String line : shape) {
				if(line.contains(Character.toString(key))) return true;
			}
		}
		return false;
	}
	
	@Override
	public RecipeBuilder clone() {
		RecipeBuilder builder = new RecipeBuilder(name, result);
		shapes.forEach(e -> builder.addShape(e));
		bukkit.forEach(e -> builder.bukkit.add(e));
		recipes.forEach(e -> builder.recipes.add(e));
		ingredients.forEach((k, i) -> builder.ingredients.put(k, i));
		return builder;
	}
}
