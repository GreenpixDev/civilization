package ru.greenpix.civilization.items;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemCivcraft {

	public Tier tier();
	
	public String recipe() default "null";
	
	public String technology() default "null";
	
}
