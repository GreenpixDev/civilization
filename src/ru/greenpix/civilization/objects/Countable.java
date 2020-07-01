package ru.greenpix.civilization.objects;

import ru.greenpix.civilization.CivCore;

public interface Countable {
	
	String BEAKERS = "beakers";
	String HAMMERS = "hammers";
	String CULTURE = "culture";
	String RELIGION = "religion";
	String GROWTH = "growth";
	String MONEY = "money";
	String HAPPINESS = "happiness";
	String UNHAPPINESS = "unhappiness";
	String SCORES = "scores";
	
	public double getValue(String type);
	
	default int getBeakersPerMinute() {
		return (int) (getValue(BEAKERS) * CivCore.getGameSpeed());
	}
	
	default int getHammersPerMinute() {
		return (int) (getValue(HAMMERS) * CivCore.getGameSpeed());
	}
	
	default int getCulturePerMinute() {
		return (int) (getValue(CULTURE) * CivCore.getGameSpeed());
	}
	
	default int getReligionPerMinute() {
		return (int) (getValue(RELIGION) * CivCore.getGameSpeed());
	}
	
	default int getGrowthPerMinute() {
		return (int) (getValue(GROWTH) * CivCore.getGameSpeed());
	}
	
	default int getMoneyPerMinute() {
		return (int) (getValue(MONEY) * CivCore.getGameSpeed());
	}
	
	default int getHappiness() {
		return (int) (getValue(HAPPINESS) * CivCore.getGameSpeed());
	}
	
	default int getUnhappiness() {
		return (int) (getValue(UNHAPPINESS) * CivCore.getGameSpeed());
	}
	
	default int getGlobalScores() {
		return (int) getValue(SCORES);
	}
}
