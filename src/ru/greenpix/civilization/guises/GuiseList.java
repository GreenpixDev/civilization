package ru.greenpix.civilization.guises;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;

import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.technologies.Technology;
import ru.greenpix.developer.utils.guises.GuiseBook;
import ru.greenpix.developer.utils.guises.GuiseItem;
import ru.greenpix.developer.utils.items.Item;

public class GuiseList<T extends GuiseElement> extends GuiseBook {
	
	public final static Item FRAME = new Item(Material.STAINED_GLASS_PANE, 15);
	
	public final static GuiseBuilder BUILDER = new GuiseBuilder(6).update(5).itemFrame(FRAME);
	
	public GuiseList(String type, String title, Collection<T> c) {
		super(BUILDER.build(title));
		for(T e : c) {
			addItem(new GuiseItem(e.getIcon(), e.getClickAction(type), e.getLoadAction(type)));
		}
	}
	
	public GuiseList(String type, GuiseBuilder builder, Collection<T> c) {
		super(builder.build());
		for(T e : c) {
			addItem(new GuiseItem(e.getIcon(), e.getClickAction(type), e.getLoadAction(type)));
		}
	}
	
	public GuiseList(String type, GuiseBuilder builder, Stream<T> stream) {
		super(builder.build());
		for(T e : stream.collect(Collectors.toList())) {
			addItem(new GuiseItem(e.getIcon(), e.getClickAction(type), e.getLoadAction(type)));
		}
	}
	
	public GuiseList(String type, String title, Stream<T> stream) {
		super(BUILDER.build(title));
		for(T e : stream.collect(Collectors.toList())) {
			addItem(new GuiseItem(e.getIcon(), e.getClickAction(type), e.getLoadAction(type)));
		}
	}
	
	public GuiseList(String title, Collection<T> c) {
		this(null, title, c);
	}
	
	public GuiseList(GuiseBuilder builder, Collection<T> c) {
		this(null, builder, c);
	}
	
	public GuiseList(GuiseBuilder builder, Stream<T> stream) {
		this(null, builder, stream);
	}
	
	public GuiseList(String title, Stream<T> stream) {
		this(null, title, stream);
	}
	
	public static class GuiseTechnologies extends GuiseList<Technology> {

		public GuiseTechnologies(String type) {
			super(type, "Технологии", Technology.getTechologies().stream()
					.sorted(Comparator.comparing(Technology::getCost)));
		}
		
	}
	
	public static class GuiseStructures extends GuiseList<Structure> {
		
		public GuiseStructures(String type) {
			super(type, "Технологии", Structure.getStructures().stream()
					.sorted(Comparator.comparing(Structure::isWonder).thenComparing(Structure::getCost)));
		}
	}
}
