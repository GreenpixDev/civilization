package ru.greenpix.civilization.items;

public enum AttributeType {

	MAX_HEALTH("generic.maxHealth"),
	FOLLOW_RANGE("generic.followRange"),
	KNOCKBACK_RESISTANCE("generic.knockbackResistance"),
	MOVEMENT_SPEED("generic.movementSpeed"),
	ATTACK_DAMAGE("generic.attackDamage"),
	ARMOR("generic.armor"),
	ARMOR_TOUGHNESS("generic.armorToughness"),
	ATTACK_SPEED("generic.attackSpeed"),
	LUCK("generic.luck");
	
	private final String name;
	
	private AttributeType(String name) {
		this.name = name;
	}
	
	public String attributeName() {
		return name;
	}
	
}
