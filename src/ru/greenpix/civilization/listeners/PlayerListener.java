package ru.greenpix.civilization.listeners;

import java.util.Comparator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.commands.chat.ChatCreationCivilization;
import ru.greenpix.civilization.diplomacy.Diplomacy;
import ru.greenpix.civilization.diplomacy.Relationship;
import ru.greenpix.civilization.diplomacy.Status;
import ru.greenpix.civilization.guises.GuiseList;
import ru.greenpix.civilization.items.CustomItems;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.utils.LocationUtils;
import ru.greenpix.civilization.utils.StringUtils;
import ru.greenpix.developer.utils.items.Item;
import ru.greenpix.developer.utils.items.ItemClickable;
import ru.greenpix.developer.utils.protocol.sounds.FixedSound;
import ru.greenpix.developer.utils.protocol.title.Title;

public class PlayerListener implements Listener {

	public final static ItemClickable CREATE_CIV = 
			ItemClickable.create(CivCore.getInstance(), p -> {
				CivPlayer player = CivPlayer.getByPlayer(p);
				if(player.getCivilization() != null) {
					player.sendMessage("&cТы уже живешь в цивилизации.");
					return;
				}
				Location l = player.getLocation();
				Building b = Building.getBuildings().stream()
						.min(Comparator.comparing(e -> LocationUtils.distance2D(e.getCenter(), l)))
						.orElse(null);
				if(b != null && b.getCenter().distance(l) < ChatCreationCivilization.MIN_DISTANCE) {
					player.sendMessage("&cТы находишься слишком близко к городу " + b.getTown().getName() + "!");
					player.sendMessage("&6Отбеги немного подальше от города, примерно на " + ((int) (ChatCreationCivilization.MIN_DISTANCE - b.getCenter().distance(l))) + " блоков.");
					return;
				}
				p.getInventory().setItemInMainHand(null);
				new GuiseList<>("civ", "Выбор стиля", Structure.getByName("townhall").getStyles()).open(p);
			}, Action.RIGHT_CLICK_BLOCK);
	
	public static void giveCivRod(Player p) {
		p.getInventory().addItem(new Item(Material.BLAZE_ROD, "&dЖезл Основания Цивилизации &e(ПКМ)").setClickable(CREATE_CIV).getHandle());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		CivPlayer p = new CivPlayer(e.getPlayer());
		p.toBukkit().setGameMode(GameMode.SURVIVAL);
		if(p.getTown() == null && !p.toBukkit().getInventory().contains(Material.APPLE)) {
			p.toBukkit().getInventory().addItem(new ItemStack(Material.APPLE, 32));
		}
		if(p.getTown() == null && !p.toBukkit().getInventory().contains(Material.BLAZE_ROD)) {
			giveCivRod(p.toBukkit());
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		CivPlayer player = CivPlayer.wrap(e.getPlayer());
		if(player == null) return;
		if(player.getTown() != null) {
			if(player.toBukkit().getGameMode() == GameMode.SPECTATOR) {
				player.teleport(player.getTown().getHome());
			}
		}
		player.remove();
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		CivPlayer player = CivPlayer.wrap(e.getPlayer());
		if(player == null) return;
		if(!player.hasHome()) return;
		for(Relationship r : Diplomacy.getRelations(player.getCivilization(), Status.WAR)) {
			Civilization civ;
			if(player.getCivilization().equals(r.getReceiver())) {
				civ = r.getSender();
			} else {
				civ = r.getReceiver();
			}
			for(Town town : civ.getTowns()) {
				if(LocationUtils.distance2D(town.getCenter(), e.getTo()) < town.getBorder()) {
					e.setCancelled(true);
					player.sendMessage("&cТы не можешь телепортироваться к врагам на базу!");
					return;
				}
			}
		}
	}
	
	private void respawn(Player player) {
		Bukkit.getScheduler().runTaskLater(CivCore.getInstance(), () -> {
			player.spigot().respawn();
		}, 1L);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		CivPlayer p = CivPlayer.wrap(e.getEntity());
		respawn(e.getEntity());
		p.toBukkit().setGameMode(GameMode.SPECTATOR);
		new BukkitRunnable() {
			int time = 30;
			@Override
			public void run() {
				if(!p.toBukkit().isOnline()) {
					cancel();
					return;
				}
				if(time == 0) {
					if(p.getTown() != null) {
						e.getEntity().teleport(p.getTown().getHome());
					} else {
						e.getEntity().teleport(e.getEntity().getWorld().getSpawnLocation().clone().add(0, 2, 0));
					}
					p.toBukkit().setGameMode(GameMode.SURVIVAL);
					if(p.getCivilization() != null) {
						PlayerListener.giveCivRod(p.toBukkit());
					}
					cancel();
					return;
				}
				Title.sendTitles(p.toBukkit(), "&c&lВы умерли!", "&eДо воскрешения " + StringUtils.formatTime(time), 0, 20, 20);
				time--;
			}
		}.runTaskTimer(CivCore.getInstance(), 2L, 20L);
	}
	
	/*
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	private void onDeath(EntityDamageEvent e) {
		if(e.isCancelled()) return;
		if(!(e.getEntity() instanceof Player)) return;
		CivPlayer p = CivPlayer.wrap(e.getEntity());
		if(e.getFinalDamage() < p.toBukkit().getHealth()) return;
		e.setCancelled(true);
		p.toBukkit().setGameMode(GameMode.SPECTATOR);
		p.toBukkit().setFoodLevel(20);
		p.toBukkit().setHealth(p.toBukkit().getMaxHealth());
		FixedSound.HURT_FLESH.playSound(p.toBukkit(), 1, 1);
		new BukkitRunnable() {
			int time = 30;
			@Override
			public void run() {
				if(!p.toBukkit().isOnline()) {
					cancel();
					return;
				}
				if(time == 0) {
					if(p.getTown() != null) {
						e.getEntity().teleport(p.getTown().getHome());
					} else {
						e.getEntity().teleport(e.getEntity().getWorld().getSpawnLocation().clone().add(0, 2, 0));
					}
					p.toBukkit().setGameMode(GameMode.SURVIVAL);
					cancel();
					return;
				}
				Title.sendTitles(p.toBukkit(), "&c&lВы умерли!", "&eДо воскрешения " + StringUtils.formatTime(time), 0, 20, 20);
				time--;
			}
		}.runTaskTimer(CivCore.getInstance(), 0, 20);
	}
	*/
	
	@EventHandler
	private void onShoot(ProjectileLaunchEvent e) {
		if(e.getEntity().getShooter() instanceof Player) {
			//e.getEntity().setMetadata("CivPlayer", new FixedMetadataValue(CivCore.getInstance(), ((Player) e.getEntity().getShooter()).getName()));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onPostDamage(EntityDamageByEntityEvent e) {
		CivPlayer p = CivPlayer.wrap(e.getEntity());
		if(p == null) return;
		p.asyncUpdateHealth();
	}
	
	@EventHandler
	private void onDamage(EntityDamageByEntityEvent e) {
		if(e.isCancelled()) return;
		Entity d = e.getDamager();
		/*
		if(e.getDamager().hasMetadata("Civ")) {
			for(MetadataValue meta : e.getDamager().getMetadata("Civ")) {
				if(meta.getOwningPlugin().equals(CivCore.getInstance())) {
					Civilization civ = Civilization.getByName(meta.asString());
					if(civ == null) return;
					if(e.getEntity() instanceof Player) {
						CivPlayer player = CivPlayer.getPlayer(e.getEntity().getName());
						if(player.getCivilization() != null && !(player.getCivilization().getRelation(civ).getRelation() instanceof RelationFriendly)) {
							return;
						}
						e.setCancelled(true);
					} else if(e.getEntity() instanceof LivingEntity) {
						if(CivNPC.isNPC((LivingEntity) e.getEntity())) {
							CivNPC<?> npc = CivNPC.getNPC((LivingEntity) e.getEntity());
							if(npc != null && npc.getCivilization() != null && !(npc.getCivilization().getRelation(civ).getRelation() instanceof RelationFriendly)) {
								return;
							}
							e.setCancelled(true);
						}
					}
				}
			}
		}
		*/
		if(e.getDamager() instanceof Arrow) {
			if(e.getDamager().hasMetadata("CivPlayer")) {
				String name = e.getDamager().getMetadata("CivPlayer").stream().filter(o -> o.getOwningPlugin().equals(CivCore.getInstance())).map(o -> o.asString()).findFirst().orElse(null);
				if(name == null) {
					e.setCancelled(true);
					return;
				}
				Player p = Bukkit.getPlayer(name);
				if(p == null) {
					e.setCancelled(true);
					return;
				}
				d = p;
			}
		}
		if(d instanceof Player) {
			CivPlayer damager = CivPlayer.getByName(d.getName());
			if(e.getEntity() instanceof Player) {
				CivPlayer player = CivPlayer.getByName(e.getEntity().getName());
				if(damager.getCivilization() != null && player.getCivilization() != null && 
						Diplomacy.getRelationship(damager.getCivilization(), player.getCivilization()).isAggressive()) {
					return;
				}
				Location l = player.getLocation();
				Building building = Building.getBuildings().parallelStream().filter(b -> b.isRegion(l)).findFirst().orElse(null);
				if(building != null) {
					damager.sendMessage("&cТы не можешь атаковать этого игрока в городе, так как вы не враждуете.");
					e.setCancelled(true);
					return;
				}
				if(e.getDamager() instanceof Arrow) {
					Player bukkit = player.toBukkit();
					if(bukkit.isBlocking() && Math.random() > 0.2) {
						if(bukkit.getInventory().getItemInMainHand().getType() == Material.SHIELD) {
							ItemStack shield = bukkit.getInventory().getItemInMainHand();
							bukkit.getInventory().setItemInMainHand(bukkit.getInventory().getItem(9));
							bukkit.getInventory().setItem(9, shield);
						} else if(bukkit.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
							ItemStack shield = bukkit.getInventory().getItemInOffHand();
							bukkit.getInventory().setItemInOffHand(bukkit.getInventory().getItem(9));
							bukkit.getInventory().setItem(9, shield);
						}
						FixedSound.ITEM_BREAK.playSound(e.getEntity().getLocation(), 1f, 1f);
					}
				}
			} 
			else if(e.getEntity() instanceof LivingEntity) {
				/*
				if(CivNPC.isNPC((LivingEntity) e.getEntity())) {
					CivNPC<?> npc = CivNPC.getNPC((LivingEntity) e.getEntity());
					if(npc != null) {
						if(damager.getCivilization() != null && npc.getCivilization() != null && 
								!(npc.getCivilization().getRelation(damager.getCivilization()).getRelation() instanceof RelationFriendly)) {
							return;
						}
						for(Building b : Building.getBuildings()) {
							if(b.getRegion().contains(npc.getLocation())) {
								damager.sendMessage("&cТы не можешь атаковать этого NPC в городе, т.к. вы не враждуете.");
								e.setCancelled(true);
								return;
							};
						}
						return;
					}
				}
				*/
				if(e.getFinalDamage() >= ((LivingEntity) e.getEntity()).getHealth()) {
					double reward;
					if(e.getEntity() instanceof Chicken) reward = 3;
					else if(e.getEntity() instanceof Rabbit) reward = 3;
					else if(e.getEntity() instanceof Wolf) reward = 7;
					else if(e.getEntity() instanceof PolarBear) reward = 10;
					else if(e.getEntity() instanceof Animals) reward = 5;
					else if(e.getEntity() instanceof Slime) reward = 5;
					else if(e.getEntity() instanceof Enderman) reward = 50;
					else if(e.getEntity() instanceof IronGolem) reward = 50;
					else if(e.getEntity() instanceof Guardian) reward = 100;
					else reward = 10;
					damager.sendMessage("&7Вы получили &a" + reward + " &7монет за убийство.");
					damager.deposit(reward);
				}
			}
		} else {
			if(e.getDamager() instanceof Arrow && e.getEntity() instanceof Player) {
				Player bukkit = ((Player) e.getEntity());
				if(bukkit.isBlocking() && Math.random() > 0.2) {
					if(bukkit.getInventory().getItemInMainHand().getType() == Material.SHIELD) {
						ItemStack shield = bukkit.getInventory().getItemInMainHand();
						bukkit.getInventory().setItemInMainHand(bukkit.getInventory().getItem(9));
						bukkit.getInventory().setItem(9, shield);
					} else if(bukkit.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
						ItemStack shield = bukkit.getInventory().getItemInOffHand();
						bukkit.getInventory().setItemInOffHand(bukkit.getInventory().getItem(9));
						bukkit.getInventory().setItem(9, shield);
					}
					FixedSound.ITEM_BREAK.playSound(e.getEntity().getLocation(), 1f, 1f);
				}
			}
		}
	}
	
	@EventHandler
	private void onInteract(PlayerInteractEvent e) {
		CivPlayer player = CivPlayer.getByPlayer(e.getPlayer());
		if(e.getItem() != null) {
			if(e.getItem().getType() == Material.BOW) {
				for(ItemStack i : player.toBukkit().getInventory().getArmorContents()) {
					if(i == null || i.getType() == Material.AIR || i.getType() == Material.LEATHER_BOOTS
							|| i.getType() == Material.LEATHER_CHESTPLATE || i.getType() == Material.LEATHER_HELMET
							|| i.getType() == Material.LEATHER_LEGGINGS) continue;
					e.setCancelled(true);
					player.sendMessage("&cВаша броня слишком тяжелая! Вы не можете стрелять из металлической брони.");
					return;
				}
			}
			
			/*
			Item item = new Item(e.getItem());
			if(item.isValidTags() && item.getTags().hasKey("CivcraftField")) {
				ItemCivcraft info = CustomItems.getItemInfo(item.getTags().getString("CivcraftField"));
				if(info != null) {
					if(!info.technology().equals("null")) {
						if(player.getCivilization() == null || !player.getCivilization().hasTechnology(info.technology())) {
							e.setCancelled(true);
							player.sendMessage("&cВы не можете использовать этот предмет. Для этого нужно изучить технологию '" + Technology.getByName(info.technology()).getDisplayName() + "'.");
							return;
						}
					}
				}
			}
			*/
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if(e.isCancelled()) return;
		Item dropped = null;
		switch (e.getBlock().getType()) {
		case GOLD_ORE:
			dropped = CustomItems.COPPER_ORE;
			break;
		case DIAMOND_ORE:
			dropped = CustomItems.TUNGSTEN_ORE;
			break;
		default:
			break;
		}
		if(dropped != null) {
			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);
			Location l = e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5);
			e.getBlock().getWorld().dropItem(l, dropped.getCopy());
		}
	}
}
