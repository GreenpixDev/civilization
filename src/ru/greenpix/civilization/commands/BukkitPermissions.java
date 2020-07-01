package ru.greenpix.civilization.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BukkitPermissions {

	public String[] value();
	
	public String msg() default "§cУ вас нет прав использовать эту команду!";
	
}
