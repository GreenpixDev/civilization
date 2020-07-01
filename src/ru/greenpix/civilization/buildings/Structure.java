package ru.greenpix.civilization.buildings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.buildings.OwnerBuildings.DenyReason;
import ru.greenpix.civilization.buildings.wonders.Wonder;
import ru.greenpix.civilization.groups.Perms;
import ru.greenpix.civilization.guises.GuiseElement;
import ru.greenpix.civilization.guises.GuiseList;
import ru.greenpix.civilization.holograms.UpdateHologram;
import ru.greenpix.civilization.objects.Countable;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.processes.BuildingProcess;
import ru.greenpix.civilization.technologies.Technology;
import ru.greenpix.civilization.utils.StringUtils;
import ru.greenpix.developer.Placeholder;
import ru.greenpix.developer.files.Config;
import ru.greenpix.developer.utils.guises.GuiseItem.GuiseItemAction;
import ru.greenpix.developer.utils.items.Item;
import ru.greenpix.developer.utils.protocol.sounds.FixedSound;

public class Structure extends Config implements Countable, GuiseElement {
	
	private static final List<Structure> structures = new ArrayList<Structure>();
	
	private final Set<Style> styles = new HashSet<Style>();
	
	public Structure(String name, File file) {
		super(CivCore.getInstance(), name, file, null);
		structures.add(this);
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends Building> getBuildingClass() {
		if(contains("class")) {
			try {
				return (Class<? extends Building>) Class.forName(getString("class"));
			} catch (ClassNotFoundException e) {
				CivCore.getInstance().getLogger().warning("Class " + getString("class") + " not found!");
			}
		} else if(contains("classname")) {
			try {
				return (Class<? extends Building>) Class.forName(getString("classname"));
			} catch (ClassNotFoundException e) {
				CivCore.getInstance().getLogger().warning("Class " + getString("classname") + " not found!");
			}
		}
		return Building.class;
	}
	
	public boolean isWonder() {
		return Wonder.class.isAssignableFrom(getBuildingClass());
	}

	public Set<Style> getStyles() {
		return styles;
	}
	
	public Style getStyle(String name) {
		return styles.stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public Style getDefaultStyle() {
		return getStyle("default");
	}
	
	public String getDisplayName() {
		return getString("displayname");
	}
	
	public static List<Structure> getStructures() {
		return structures;
	}
	
	public static Structure getByName(String name) {
		return structures.stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public static Structure getByClass(Class<? extends Building> clazz) {
		return structures.stream().filter(s -> s.getBuildingClass().equals(clazz)).findFirst().orElse(null);
	}
	
	public Item getIcon() {
		if(!contains("icon")) new Item(Material.STONE);
		return new Item(getString("icon"));
	}
	
	public double getCost() {
		if(!contains("cost")) return 0D;
		return getDouble("cost");
	}
	
	public int getDurability() {
		if(!contains("durability")) return 100;
		return getInt("durability");
	}
	
	public int getHammers() {
		if(!contains("hammers")) return 1;
		return getInt("hammers");
	}
	
	public int getOffsetX() {
		return getInt("offsetX");
	}
	
	public int getOffsetY() {
		return getInt("offsetY");
	}
	
	public int getOffsetZ() {
		return getInt("offsetZ");
	}
	
	public Vector getOffset() {
		return new Vector(getOffsetX(), getOffsetY(), getOffsetZ());
	}
	
	public Vector getHologramOffset() {
		return new Vector(getDouble("hologram.offsetX"), getDouble("hologram.offsetY"), getDouble("hologram.offsetZ"));
	}
	
	public List<Technology> getTechnologies() {
		if(contains("technology")) {
			Technology tech = Technology.getByName(getString("technology"));
			return tech == null ? new ArrayList<Technology>() : Arrays.asList(tech);
		}
		return getStringList("technologies").stream()
				.map(t -> Technology.getByName(t))
				.filter(e -> e != null)
				.collect(Collectors.toList());
	}
	
	public List<Structure> getParents() {
		return getStringList("parents").stream()
				.map(e -> Structure.getByName(e))
				.filter(e -> e != null)
				.collect(Collectors.toList());
	}
	
	public List<String> getDescription() {
		return getStringList("description");
	}
	
	public double getTax() {
		return getDouble("tax");
	}
	
	public int getLimit() {
		return getInt("limit");
	}
	
	public boolean hasLimit() {
		return getLimit() > 0;
	}
	
	public int getTime(Town town) {
		return (int) ((getHammers() / Math.max(0.0000000001D, (double) town.getHammersPerMinute())) * 60);
	}

	@Override
	public double getValue(String type) {
		double v = 0;
		switch (type) {
		case Countable.SCORES:
			v += getInt("scores");
			break;
		case Countable.MONEY:
			v -= getTax();
			v += getDouble("buffs.income");
			break;
		default:
			break;
		}
		return v + getDouble("buffs." + type);
	}
	
	public UpdateHologram createHologram(Location l, Placeholder... phs) {
		return new UpdateHologram(l, contains("hologram.update") ? getInt("hologram.update") : 10, contains("hologram.lines") ? getStringList("hologram.lines") : Arrays.asList(
				"&6&l%structure%", 
				"Прочность: &e%durability%"
		), phs);
	}

	@Override
	public GuiseItemAction getClickAction(String type) {
		return (player, item) -> {
			CivPlayer p = CivPlayer.getByPlayer(player);
			if(!p.hasTown() || !p.getTown().canBuild(this)) {
				FixedSound.ITEM_BREAK.playSound(player, 0.5F, 2F);
				if(this.isWonder() && Wonder.existsAliveWonder(this)) {
					p.sendMessage("&cЧтобы построить это чудо света, Вам нужно уничтожить его в городе " + Wonder.getWonder(this).getTown().getName());
				}
				return;
			}
			if(this.isWonder() && (System.currentTimeMillis() - p.getCivilization().breakWonder) < 600000) {
				FixedSound.WOLF_HURT.playSound(player, 0.5F, 1F);
				p.sendMessage("&cУ вас недавно разрушили чудо! К сожалению, вы сможете построить чудо только через 10 минут =(");
				return;
			}
			if(!p.hasPermission(p.getTown(), Perms.BUILDINGS_BUILD.replace("<type>", this.getName()))) {
				FixedSound.ITEM_BREAK.playSound(player, 0.5F, 2F);
				p.sendMessage("&cУ вас нет разрешения построить эту постройку. Попросите мэра города это сделать.");
				return;
			}
			if(p.getTown().hasDebt()) {
				p.sendMessage("&cВы не можете строить постройки, пока Ваш город не выплатит долг.");
				p.closeGuise();
				return;
			}
			if(p.getTown().getProcess() != null && !p.getTown().getProcess().isCompleted()) {
				p.sendMessage("&cВы не можете строить 2 постройки одновременно!");
				p.closeGuise();
				return;
			}
			if(p.getBalance() < this.getCost()) {
				if(p.hasPermission(p.getTown(), Perms.TREASURE_USE)) {
					if(p.getTown().getBalance() < this.getCost()) {
						p.sendMessage("&cНедостаточно монет для постройки (у вас " + p.getBalance() + " монет).");
						return;
					}
				} else {
					p.sendMessage("&cНедостаточно монет для постройки (у вас " + p.getBalance() + " монет).");
					return;
				}
			}
			new GuiseList<>("build", "Выбор Стиля", getStyles()).open(player);
		};
	}

	@Override
	public GuiseItemAction getLoadAction(String type) {
		return (player, item) -> {
			CivPlayer p = CivPlayer.getByPlayer(player);
			item.setDisplayName("&e" + StringUtils.align(this.getDisplayName(), 40));
			String[] techs = new String[this.getTechnologies().size() + 1];
			String[] parents = new String[this.getParents().size() + 1];
			techs[0] = "&aНеобходимо изучить:";
			parents[0] = "&aНеобходимо построить:";
			BuildingProcess process = p.getTown().getProcess();
			if(process != null && !process.isCompleted() && equals(process.getBuilding().getStructure())) {
				item.setEnchantAnimation(true);
				item.addLore("&3" + StringUtils.align("&lСТРОИТСЯ", 40),
						"&eПрогресс: &b" + StringUtils.formatDouble(process.getProgress(), 1) + "%",
						"&eОсталось: &6" + StringUtils.formatTime((int) (process.getTimeLeft())));
			}
			else {
				DenyReason reason = p.getTown().getReasonBuildDenied(this);
				if(reason == null) {
					item.addLore("&a" + StringUtils.align("&lДОСТУПНО", 40),
							"&eПостроится за: &6" + StringUtils.formatTime(this.getTime(p.getTown())));
				} else if(reason == DenyReason.WONDER_LIMIT) {
					// OwnerBuildings o = Wonder.getWonder(s).getOwner();
					Town o = Wonder.getWonder(this).getTown();
					String status = StringUtils.align("&lПостроено в " + o.getName(), 40);
					if(p.getCivilization().getTowns().contains(o)) {
						item.addLore("&a" + status);
						item.setId(160);
						item.setData(5);
					} else {
						item.addLore("&c" + status);
						item.setId(160);
						item.setData(1);
					}
				} else if(reason == DenyReason.TOWN_LIMIT){
					item.addLore("&c" + StringUtils.align("&lДОСТИГНУТ ЛИМИТ", 40));
					item.setId(160);
					item.setData(1);
				} else {
					item.addLore("&c" + StringUtils.align("&lНЕДОСТУПНО", 40));
					item.setId(160);
					item.setData(14);
				}
			}
			item.addLore("&7----------------------------------------");
			item.addLore(this.getDescription());
			item.addLore("&7----------------------------------------");
			if(this.hasLimit()) item.addLore("&eЛимит: &4" + this.getLimit());
			item.addLore(
					"&eПостроено: &6" + p.getTown().getBuildings(this).size() + (this.hasLimit() ? "/" + this.getLimit() : ""),
					"&eНалог: &6" + this.getTax() + "/мин",
					"&eОчков: &6" + this.getGlobalScores(),
					"&eСтоимость: &6" + this.getCost(),
					"&7----------------------------------------");
			for(int i = 0; i < this.getTechnologies().size(); i++) {
				String start;
				Technology parent = this.getTechnologies().get(i);
				if(p.getCivilization().getTechnologies().contains(parent)) start = "&a✓ ";
				else {
					techs[0] = "&cНеобходимо изучить:";
					start = "&c✕ ";
				}
				techs[i + 1] = start + parent.getDisplayName();
			}
			if(techs.length > 1) item.addLore(techs);
			for(int i = 0; i < this.getParents().size(); i++) {
				String start;
				Structure parent = this.getParents().get(i);
				if(p.getTown().getBuildings(parent).size() > 0) start = "&a✓ ";
				else {
					parents[0] = "&cНеобходимо построить:";
					start = "&c✕ ";
				}
				parents[i + 1] = start + parent.getDisplayName();
			}
			if(parents.length > 1) item.addLore(parents);
		};
	}
}
