package ru.greenpix.civilization.buildings;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.entity.Player;

import ru.greenpix.civilization.clipboard.Clipboard;
import ru.greenpix.civilization.clipboard.SchematicReader;
import ru.greenpix.civilization.commands.chat.ChatCreationBuilding;
import ru.greenpix.civilization.commands.chat.ChatCreationCivilization;
import ru.greenpix.civilization.guises.GuiseElement;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.utils.StringUtils;
import ru.greenpix.developer.utils.guises.GuiseItem.GuiseItemAction;
import ru.greenpix.developer.utils.items.Item;
import ru.greenpix.developer.utils.protocol.sounds.FixedSound;

public class Style implements GuiseElement {

	private final String name;
	
	private final Structure structure;
	
	private final Clipboard clipboard;
	
	public Style(String name, Structure structure, File file) throws IOException {
		this.name = name;
		this.structure = structure;
		this.clipboard = SchematicReader.cache(file, getCacheTime());
		structure.getStyles().add(this);
	}
	
	public String getName() {
		return name;
	}

	public Structure getStructure() {
		return structure;
	}

	public Clipboard getClipboard() {
		return clipboard;
	}
	
	public int getCacheTime() {
		if(structure.contains("styles." + getName() + ".cache_time")) {
			return structure.getInt("styles." + getName() + ".cache_time");
		}
		if(structure.contains("cache_time")) {
			return structure.getInt("cache_time");
		}
		return 120;
	}
	
	public double getCost() {
		return structure.getDouble("styles." + getName() + ".cost");
	}
	
	public List<String> getDescription() {
		return structure.getStringList("styles." + getName() + ".description");
	}
	
	public String getDisplayName() {
		if(structure.contains("styles." + getName() + ".displayname")) {
			return structure.getString("styles." + getName() + ".displayname");
		}
		return getName();
	}
	
	public boolean hasPermission(Player p) {
		if(getName().equalsIgnoreCase("default")) return true;
		return p.isOp() || p.hasPermission("*") || p.hasPermission("civcraft.*") || p.hasPermission("civcraft.styles.*") || p.hasPermission("civcraft.styles." + getName() + ".*") || p.hasPermission("civcraft.styles." + getName() + "." + getStructure());
	}

	@Override
	public Item getIcon() {
		if(structure.contains("styles." + getName() + ".icon")) {
			return new Item(structure.getString("styles." + getName() + ".icon"));
		} else {
			return Item.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19");
		}
	}

	@Override
	public GuiseItemAction getClickAction(String type) {
		return (player, item) -> {
			if(hasPermission(player)) {
				switch (type) {
				case "civ":
					new ChatCreationCivilization(this).send(CivPlayer.getByPlayer(player));
					break;
				case "build":
					new ChatCreationBuilding(player, this).send(CivPlayer.getByPlayer(player));
					break;
				default:
					break;
				}
				player.closeInventory();
			} else {
				FixedSound.ITEM_BREAK.playSound(player, .8f, 2f);
			}
		};
	}

	@Override
	public GuiseItemAction getLoadAction(String type) {
		return (player, item) -> {
			item.setDisplayName("&e" + StringUtils.align(getDisplayName(), 40));
			if(hasPermission(player)) {
				item.addLore("&a" + StringUtils.align("&lДОСТУПНО", 40));
			} else {
				item.addLore("&c" + StringUtils.align("&lНЕДОСТУПНО", 40));
				item.setId(160);
				item.setData(14);
			}
			item.addLore("",
						"&eСтоимость стиля: &c" + getCost() + " руб навсегда",
						"&7----------------------------------------");
			item.addLore(getDescription());
			item.addLore("&7----------------------------------------");
		};
	}
}
