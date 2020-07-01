package ru.greenpix.civilization.items.crafts;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.google.common.base.Preconditions;

import ru.greenpix.developer.utils.items.Item;

public class CraftPack implements Cloneable {
	
	private List<RecipeBuilder> buffer = new ArrayList<RecipeBuilder>();
	
	public CraftPack() {
		next();
	}
	
	public CraftPack(CraftPack parent) {
		parent.buffer.forEach(b -> buffer.add(b.clone()));
	}
	
	public CustomRecipe[] registerAll(String replacement, Item... results) {
		Preconditions.checkArgument(results.length == size(), "Length results array ("+results.length+") must be buffer size ("+size()+")");
		CustomRecipe[] recipes = new CustomRecipe[size()];
		RecipeBuilder b;
		for(int i = 0; i < size(); i++) {
			b = buffer.get(i);
			recipes[i] = b.setName(b.getName().replace("%replace%", replacement))
					.setResult(results[i]).registerAndCreate();
		}
		return recipes;
	}
	
	public CraftPack bindAll(char key, Item ingredient) {
		buffer.forEach(b -> {
			if(b.containsKey(key)) b.bind(key, ingredient);
		});
		return this;
	}
	
	public CraftPack bindAll(char key, Material ingredient) {
		buffer.forEach(b -> {
			if(b.containsKey(key)) b.bind(key, ingredient);
		});
		return this;
	}
	
	public CraftPack setName(String name) {
		current().setName(name);
		return this;
	}
	
	public CraftPack addShape(String... shape) {
		current().addShape(shape);
		return this;
	}
	
	public CraftPack next() {
		buffer.add(new RecipeBuilder());
		return this;
	}
	
	public int size() {
		return buffer.size();
	}
	
	public RecipeBuilder current() {
		return buffer.get(size() - 1);
	}
	
	@Override
	public CraftPack clone() {
		return new CraftPack(this);
	}
}
