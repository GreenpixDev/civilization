package ru.greenpix.civilization.technologies;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import ru.greenpix.civilization.groups.Perms;
import ru.greenpix.civilization.guises.GuiseElement;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.objects.Countable;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.utils.StringUtils;
import ru.greenpix.developer.Utils;
import ru.greenpix.developer.utils.guises.GuiseItem.GuiseItemAction;
import ru.greenpix.developer.utils.items.Item;
import ru.greenpix.developer.utils.protocol.sounds.FixedSound;

public class Technology implements Countable, GuiseElement {

	private final static List<Technology> techologies = new ArrayList<>();
	
	private final String name;
	private final ConfigurationSection options;
	
	public Technology(ConfigurationSection section) {
		this.options = section;
		this.name = section.getName();
		techologies.add(this);
	}
	
	public List<Technology> getParents() {
		if(!options.contains("parents")) return new ArrayList<>();
		return options.getStringList("parents").stream().map(p -> getByName(p)).collect(Collectors.toList());
	}

	public Item getIcon() {
		if(!options.contains("icon")) new Item(Material.STONE);
		return new Item(options.getString("icon"));
	}
	
	public String getDisplayName() {
		if(!options.contains("displayname")) return getName();
		return Utils.color(options.getString("displayname"));
	}
	
	public List<String> getDescription() {
		if(!options.contains("description")) return new ArrayList<>();
		return options.getStringList("description");
	}
	
	public double getCost() {
		if(!options.contains("cost")) return 0D;
		return options.getDouble("cost");
	}
	
	public int getBeakers() {
		if(!options.contains("beakers")) return 1;
		return options.getInt("beakers");
	}
	
	public int getScores() {
		if(!options.contains("scores")) return 0;
		return options.getInt("scores");
	}
	
	public Era getEra() {
		if(!options.contains("era")) return null;
		return Era.valueOf(options.getString("era").toUpperCase());
	}
	
	public String getName() {
		return name;
	}

	public ConfigurationSection getOptions() {
		return options;
	}
	
	public static List<Technology> getTechologies() {
		return techologies;
	}
	
	public static Technology getByName(String name) {
		for(Technology tech : getTechologies()) {
			if(tech.getName().equalsIgnoreCase(name)) return tech;
		} return null;
	}
	
	public int getTime(Civilization civ) {
		return (int) ((getBeakers() / Math.max(0.0000000001D, (double) civ.getBeakersPerMinute())) * 60);
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public double getValue(String type) {
		if(type == Countable.SCORES && options.contains("scores")) {
			return options.getInt("scores");
		}
		return 0;
	}

	@Override
	public GuiseItemAction getClickAction(String type) {
		return (player, item) -> {
			CivPlayer p = CivPlayer.getByPlayer(player);
			if(p.getCivilization() == null || !p.getCivilization().canResearch(this)) {
				FixedSound.ITEM_BREAK.playSound(player, 0.5F, 2F);
				return;
			}
			if(p.getCivilization().getTechnologies().contains(this)) return;
			if(!p.hasPermission(p.getCivilization(), Perms.RESEARCH)) {
				FixedSound.ITEM_BREAK.playSound(player, 0.5F, 2F);
				p.sendMessage("&cУ вас нет разрешения изучать. Попросите лидера цивилизации это сделать.");
				return;
			}
			if(p.getCivilization().getProcess() != null && !p.getCivilization().getProcess().isCompleted()) {
				p.sendMessage("&cВы не можете изучать 2 технологии одновременно!");
				p.closeGuise();
				return;
			}
			if(p.getBalance() < this.getCost()) {
				if(p.hasPermission(p.getCivilization(), Perms.TREASURE_USE) && p.getCivilization().getBalance() >= this.getCost()) {
					p.getCivilization().research(this);
					p.getCivilization().withdraw(this.getCost());
					return;
				}
				p.sendMessage("&cНедостаточно монет для изучения (у вас " + p.getBalance() + " монет).");
				return;
			}
			p.getCivilization().research(this);
			p.withdraw(this.getCost());
		};
	}

	@Override
	public GuiseItemAction getLoadAction(String type) {
		return (player, item) -> {
			CivPlayer p = CivPlayer.getByPlayer(player);
			item.setDisplayName("&e" + StringUtils.align(this.getDisplayName(), 40));
			String[] parents = new String[this.getParents().size() + 1];
			parents[0] = "&aНеобходимо изучить:";
			if(p.getCivilization().getProcess() != null && !p.getCivilization().getProcess().isCompleted() && p.getCivilization().getProcess().getTechnology().equals(this)) {
				item.setEnchantAnimation(true);
				item.addLore("&3" + StringUtils.align("&lИЗУЧАЕТСЯ", 40),
						"&eПрогресс: &b" + StringUtils.formatDouble(p.getCivilization().getProcess().getProgress(), 1) + "%",
						"&eОсталось: &6" + StringUtils.formatTime((int) p.getCivilization().getProcess().getTimeLeft()));
			}
			else if(!p.getCivilization().canResearch(this)) {
				item.addLore("&c" + StringUtils.align("&lНЕДОСТУПНО", 40));
				close(item);
			} else {
				if(p.getCivilization().getTechnologies().contains(this)) {
					item.addLore("&b" + StringUtils.align("&lИЗУЧЕНО", 40));
					open(item);
				}
				else {
					item.addLore("&a" + StringUtils.align("&lДОСТУПНО", 40),
							"&eИзучится за: &6" + StringUtils.formatTime(this.getTime(p.getCivilization())));
				}
			}
			item.addLore("&7----------------------------------------");
			item.addLore(this.getDescription());
			item.addLore("&7----------------------------------------",
					"&eЭра: " + (this.getEra() == null ? "&6NULL" : this.getEra().getColor() + this.getEra().getDisplayName()),
					//"&eТип: &6" + tech.getType(),
					"&eСтоимость: &6" + this.getCost(),
					"&7----------------------------------------");
			for(int i = 0; i < this.getParents().size(); i++) {
				String start;
				Technology parent = this.getParents().get(i);
				if(p.getCivilization().getTechnologies().contains(parent)) start = "&a✓ ";
				else {
					parents[0] = "&cНеобходимо изучить:";
					start = "&c✕ ";
				}
				parents[i + 1] = start + parent.getDisplayName();
			}
			if(parents.length > 1) item.addLore(parents);
		};
	}
	
	private void close(Item item) {
		item.setId(160);
		item.setData(14);
	}
	
	private void open(Item item) {
		item.setId(160);
		item.setData(5);
	}
}
