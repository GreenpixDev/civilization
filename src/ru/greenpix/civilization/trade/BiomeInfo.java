package ru.greenpix.civilization.trade;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Biome;

public enum BiomeInfo {
	
	Default(.5, .5, .5, .5),
	Beach(0.25,	0.5, 0.06, 1.5, Biome.BEACHES),
	Birch_Forest(1.5, 0.75, 0.025, 0.5, Biome.BIRCH_FOREST),
	Birch_Forest_Hills(1.5,	1, 0.02, 1, Biome.BIRCH_FOREST_HILLS),
	Birch_Forest_Hills_Mountains(2, 0.15, 0.02, 0.5 , Biome.MUTATED_BIRCH_FOREST_HILLS),
	Birch_Forest_Mountains(2.5, 0.5, 0.01, 1, Biome.MUTATED_BIRCH_FOREST),
	Cold_Beach(2, 0, 0.01, 1, Biome.COLD_BEACH),
	Cold_Taiga(1, 0.25, 0.02, 1.5, Biome.TAIGA_COLD),
	Cold_Taiga_Hills(2.5, 0.15, 0.02, 0.25, Biome.TAIGA_COLD_HILLS),
	Cold_Taiga_Mountains(3, 0, 0, 1, Biome.MUTATED_TAIGA_COLD),
	Deep_Ocean(0.5, 0.25, 0, 1.5, Biome.DEEP_OCEAN),
	Desert(0.25, 1, 0.03, 1, Biome.DESERT),
	Desert_Hills(2, 0.25, 0.03, 1, Biome.DESERT_HILLS),
	Desert_Mountains(3, 0, 0.01, 1, Biome.MUTATED_DESERT),
	Extreme_Hills(4, 0, 0, 0.15, Biome.EXTREME_HILLS),
	Extreme_Hills_Mountains(4, 0, 0, 0.5, Biome.MUTATED_EXTREME_HILLS),
	Extreme_Hills_Plus(4.5, 0, 0, 0, Biome.EXTREME_HILLS_WITH_TREES),
	Extreme_Hills_Plus_Mountains(4.5, 0, 0, 0.5, Biome.MUTATED_EXTREME_HILLS_WITH_TREES),
	//Flower_Forest(1, 1.25, 0.05, 0.75, Biome.FLOWER_FOREST),
	Forest(1, 1.25, 0.025, 0.5, Biome.FOREST),
	Forest_Hills(2, 0.25, 0.03, 1, Biome.FOREST_HILLS),
	Frozen_Ocean(0.25, 0, 0, 3, Biome.FROZEN_OCEAN),
	Frozen_River(2, 2.5, 0.015, 1, Biome.FROZEN_RIVER),
	Ice_Mountains(2, 0.25, 0.02, 1, Biome.ICE_MOUNTAINS),
	Ice_Plains(1, 0.5, 0.02, 0.5, Biome.ICE_FLATS),
	Ice_Plains_Spikes(0.5, 0, 0.03, 3, Biome.MUTATED_ICE_FLATS),
	Jungle(0.5, 1, 0.02, 1, Biome.JUNGLE),
	Jungle_Edge(1, 0.5, 0.03, 1, Biome.JUNGLE_EDGE),
	Jungle_Edge_Mountains(2, 0.25, 0.01, 0.05, Biome.MUTATED_JUNGLE_EDGE),
	Jungle_Hills(2, 0.25, 0.03, 1, Biome.JUNGLE_HILLS),
	Jungle_Mountains(3, 0.05, 0, 0.25, Biome.MUTATED_JUNGLE),
	Mega_Spruce_Taiga(2.5, .025, .01, 0.25, Biome.MUTATED_REDWOOD_TAIGA),
	Mega_Spruce_Taiga_Hills(3, 0, 0.03, 0.5, Biome.MUTATED_REDWOOD_TAIGA_HILLS),
	Mega_Taiga(1.5, 0.25, 0.02, 1.5, Biome.REDWOOD_TAIGA),
	Mega_Taiga_Hills(3, 0.1, 0, 0.1, Biome.REDWOOD_TAIGA_HILLS),
	Mesa(2, 0, 0.03, 1.5, Biome.MESA),
	//Mesa_Bryce(2, 0.5, 0.01, 0.25, Biome.MESA_BRYCE),
	Mesa_Plateau(3, 0, 0.01, 0.25, Biome.MESA_CLEAR_ROCK),
	Mesa_Plateau_Forest(1.5, 1, 0.02, 0.5, Biome.MESA_ROCK),
	Mesa_Plateau_Mountains(4, 0.25, 0, 0.25, Biome.MUTATED_MESA_CLEAR_ROCK),
	Mesa_Plateau_Forest_Mountains(3, 0.25, 0, 1, Biome.MUTATED_MESA_ROCK),
	Mushroom_Island(1, 0.5, 0.03, 0.5, Biome.MUSHROOM_ISLAND),
	Mushroom_Shore(0.5, 0.25, 0.05, 0.5, Biome.MUSHROOM_ISLAND_SHORE),
	Ocean(0.5, 0.25, 0.02, 1, Biome.OCEAN),
	River(0.5, 0.25, 0.02, 1, Biome.RIVER),
	Plains(0.25, 2, 0.025, 0.5, Biome.PLAINS),
	Roofed_Forest(1, 0.5, 0.025, 0.75, Biome.ROOFED_FOREST),
	Roofed_Forest_Mountains(2, 2, 0, 0.5, Biome.MUTATED_ROOFED_FOREST),
	Savanna(1, 1.5, 0.03, 1, Biome.SAVANNA),
	Savanna_Mountains(4, 0.25, 0, 0.25, Biome.MUTATED_SAVANNA),
	Savanna_Plateau(1.25, 1.25, 0.025, 1, Biome.SAVANNA_ROCK),
	Savanna_Plateau_Mountains(3.25, 0.5, 0, 0, Biome.MUTATED_SAVANNA_ROCK),
	Small_Mountains(2.5, 0.5, 0.015, 0.5, Biome.SMALLER_EXTREME_HILLS),
	Stone_Beach(3, 0, 0.015, 0.15, Biome.STONE_BEACH),
	//Sunflower_Plains(1, 1.5, 0.04, 1, Biome.SUNFLOWER_PLAINS),
	Swampland(0.25, 1, 0.02, 1.5, Biome.SWAMPLAND),
	Swampland_Mountains(2.5, 0.1, 0.02, 0.5, Biome.MUTATED_SWAMPLAND),
	Taiga(2, 0.5, 0.02, 0.5, Biome.TAIGA),
	Taiga_Hills(2.5, 0.25, 0.02, 0.15, Biome.TAIGA_HILLS),
	Taiga_Mountains(1.5, 0.5, 0.03, 1, Biome.MUTATED_TAIGA);
	
	public final Biome[] biomes;
	
	public final double hammers;
	
	public final double growth;
	
	public final double happiness;
	
	public final double beakers;
	
	private BiomeInfo(double hammers, double growth, double happiness, double beakers, Biome... biomes) {
		this.biomes = biomes;
		this.hammers = hammers;
		this.growth = growth;
		this.happiness = happiness;
		this.beakers = beakers;
	}
	
	public static BiomeInfo getByBiome(Biome biome) {
		for(BiomeInfo i : values()) {
			for(Biome b : i.biomes) {
				if(b == biome) return i;
			}
		}
		return Default;
	}
	
	public static BiomeInfo[] getBiomes(String... regex) {
		List<BiomeInfo> list = new ArrayList<>();
		for(BiomeInfo i : values()) {
			for(String r : regex) {
				if(i.name().toUpperCase().matches(r)) {
					list.add(i);
					break;
				}
			}
		}
		return list.stream().toArray(BiomeInfo[]::new);
	}
}
