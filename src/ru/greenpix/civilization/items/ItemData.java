package ru.greenpix.civilization.items;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bukkit.inventory.ItemFlag;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemData {

	public boolean unbreakable() default false;
	
	public ItemFlag[] flags() default {};
	
	public String skull() default "";
	
	public int[] idEnchants() default {};
	
	public int[] lvlEnchants() default {};
	
}
