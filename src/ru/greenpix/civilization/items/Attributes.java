package ru.greenpix.civilization.items;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Attributes {

	public AttributeType[] type();
	
	public double[] amount();
	
	public Slot[] slot();
	
	public int[] mode() default 0;
	
}
