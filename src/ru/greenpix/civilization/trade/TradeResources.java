package ru.greenpix.civilization.trade;

import static ru.greenpix.civilization.trade.BiomeInfo.*;

public enum TradeResources {

	// Стратегические
	OIL("Нефть", 375, 10),
	URAN("Уран", 500, 2.5),
	TITANIUM("Титануим", 5, 500),
	
	// Бонусные
	CITRUS("Цитрусовые", 100, 100, BiomeInfo.getBiomes("DESERT", "JUNGLE")),
	CORN("Кукуруза", 125, 80, BiomeInfo.getBiomes("JUNGLE")),
	COTTON("Хлопок", 125, 80, BiomeInfo.getBiomes("FOREST")),
	GRAPES("Виноград", 125, 80, BiomeInfo.getBiomes("FOREST")),
	OLIVES("Оливки", 125, 80, BiomeInfo.getBiomes("PLAINS")),
	SPICES("Пряности", 150, 70, BiomeInfo.getBiomes("PLAINS")),
	SILVER("Серебро", 150, 25),
	RICE("Рис", 150, 70, Beach, River),
	TUNA("Тунец", 250, 50, Ocean, Deep_Ocean, River),
	CRABS("Крабы", 250, 50, Ocean, Deep_Ocean, River),
	TEA("Чай", 250, 50, BiomeInfo.getBiomes("EXTREME")),
	MARBLE("Мрамор", 250, 20),
	COLORANTS("Красители", 250, 25),
	IVORY("Слоновая Кость", 300, 30, BiomeInfo.getBiomes("DESERT", "MESA")),
	COFFEE("Кофе", 300, 30, BiomeInfo.getBiomes("JUNGLE")),
	SILK("Шелк", 375, 3.5),
	PEARLS("Жемчуг", 375, 10, BiomeInfo.getBiomes("OCEAN", "RIVER")),
	TRUFFLES("Трюфели", 375, 10, BiomeInfo.getBiomes("SWAPLAND", "JUNGLE")),
	GOLD("Золото", 375, 2.5),
	;
	
	private final String display;
	
	private final int income;
	
	private final BiomeInfo[] biomes;
	
	private double chance;
	
	private TradeResources(String display, int income, double chance, BiomeInfo... biomes) {
		this.display = display;
		this.income = income;
		this.setChance(chance);
		this.biomes = biomes;
	}

	public String getDisplayName() {
		return display;
	}
	
	public BiomeInfo[] getBiomes() {
		return biomes;
	}

	public int getIncome() {
		return income;
	}

	public double getChance() {
		return chance;
	}

	public void setChance(double chance) {
		this.chance = chance;
	}
}
