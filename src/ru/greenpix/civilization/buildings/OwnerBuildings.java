package ru.greenpix.civilization.buildings;

import java.util.List;

public interface OwnerBuildings {

	public String getName();
	
	public List<Building> getBuildings();
	
	public DenyReason getReasonBuildDenied(Structure structure);

	default boolean canBuild(Structure structure) {
		return getReasonBuildDenied(structure) == null;
	}
	
	public enum DenyReason {
		TOWN_LIMIT,
		WONDER_LIMIT,
		TECHNOLOGY,
		PARENT_BUILDINGS
	}
}
