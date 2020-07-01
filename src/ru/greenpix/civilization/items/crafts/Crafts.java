package ru.greenpix.civilization.items.crafts;

public interface Crafts {
	
	/**
	 * @recipe shield
	 * @key A - сердцевина щита (металл)
	 * @key B - дерево
	 */
	
	CraftPack SHIELD_RECIPE = new CraftPack()
			.setName("%replace%_shield")
			.addShape("BAB","BBB"," B ");
	
	/**
	 * @recipe bow
	 * @key A - нитки
	 * @key B - палочка
	 */
	
	CraftPack BOW_RECIPE = new CraftPack()
			.setName("%replace%_bow")
			.addShape("AB ","A B","AB ")
			.addShape(" BA","B A"," BA");
	
	/**
	 * @recipe sword, axe, pickaxe, bow
	 * @key A - слиток
	 * @key B - палочка
	 * @key C - нитки для лука
	 */
	
	CraftPack TOOL_PACK = new CraftPack()
			.setName("%replace%_sword")
			.addShape("A","A","B")
			.next()
			.setName("%replace%_axe")
			.addShape("AA", "AB", " B")
			.addShape("AA", "BA", "B ")
			.next()
			.setName("%replace%_pickaxe")
			.addShape("AAA"," B ", " B ");
	
	/**
	 * @recipe helmet, chestplate, leggings, boots
	 * @key A - слиток
	 */
	
	CraftPack ARMOR_PACK = new CraftPack()
			.setName("%replace%_helmet")
			.addShape("AAA","A A")
			.next()
			.setName("%replace%_chestplate")
			.addShape("A A","AAA","AAA")
			.next()
			.setName("%replace%_leggings")
			.addShape("AAA","A A","A A")
			.next()
			.setName("%replace%_boots")
			.addShape("A A","A A");
	
}
