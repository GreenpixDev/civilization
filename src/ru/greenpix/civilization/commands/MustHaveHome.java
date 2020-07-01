package ru.greenpix.civilization.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.greenpix.civilization.objects.Groupable;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MustHaveHome {

	public Class<? extends Groupable> value();
	
	public String msg() default "Ты должен проживать в %home%, чтобы использовать эту команду.";
	
}
