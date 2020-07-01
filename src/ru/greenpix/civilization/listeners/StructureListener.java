package ru.greenpix.civilization.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import ru.greenpix.civilization.Prefixs;
import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.clipboard.CachedClipboard;
import ru.greenpix.civilization.diplomacy.Diplomacy;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.developer.utils.protocol.title.Title;

public class StructureListener implements Listener {

	public static int[] brittleBlocks = new int[]{
			6, 83, 18, 161, 31, 32, 37, 38, 39, 40, 50, 59, 106, 111, 175, 76, 75, 66, 157, 28, 27, 140, 141, 142, 115, 104, 105
	};
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if(e.isCancelled()) return;
		Location l = e.getBlock().getLocation();
		Building building = Building.getBuildings().parallelStream().filter(b -> b.isRegion2D(l)).findFirst().orElse(null);
		if(building != null) {
			if(building.isDestroyed()) return;
			Civilization civ = building.getTown().getCivilization();
			CivPlayer player = CivPlayer.wrap(e.getPlayer());
			boolean war = player.getCivilization() != null && Diplomacy.getRelationship(player.getCivilization(), civ).isWar();
			if(!building.getTown().equals(player.getTown()) && !war) {
				player.sendMessage("&cВы не можете сломать постройку цивилизации " + civ.getName() + ", так как не воюете с ней!");
				e.setCancelled(true);
			} else if(building.getClipboard() instanceof CachedClipboard && !((CachedClipboard) building.getClipboard()).isCached()) {
				Title.sendActionBar(e.getPlayer(), Prefixs.SYS + "Постройка прогружается...");
				e.setCancelled(true);
				((CachedClipboard) building.getClipboard()).cache(b -> {
					if(!b) e.getPlayer().sendMessage(Prefixs.ERROR + "Ошибка прогрузки, обратитесь к администраторам!");
					else Title.sendActionBar(e.getPlayer(), Prefixs.SYS + "Постройка прогрузилась!");
				});
			} else if(building.isRegion(l) && building.inhereBuilding(l)) {
				if(war) {
					for(int id : brittleBlocks) {
						if(e.getBlock().getTypeId() == id) {
							player.sendMessage("&cЭтот тип блока нельзя разрушить!");
							e.setCancelled(true);
							return;
						}
					}
					building.damage(player, e.getBlock());
				}
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if(e.isCancelled()) return;
		Location l = e.getBlock().getLocation();
		Building building = Building.getBuildings().parallelStream().filter(b -> b.isRegion2D(l)).findFirst().orElse(null);
		if(building != null) {
			if(building.isDestroyed()) return;
			Civilization civ = building.getTown().getCivilization();
			CivPlayer player = CivPlayer.wrap(e.getPlayer());
			boolean war = player.getCivilization() != null && Diplomacy.getRelationship(player.getCivilization(), civ).isWar();
			if(!building.getTown().equals(player.getTown()) && !war) {
				player.sendMessage("&cЭта территория принадлежит городу " +  building.getTown().getName() + "!");
				e.setCancelled(true);
			} else if(building.getClipboard() instanceof CachedClipboard && !((CachedClipboard) building.getClipboard()).isCached()) {
				Title.sendActionBar(e.getPlayer(), Prefixs.SYS + "Постройка прогружается...");
				e.setCancelled(true);
				((CachedClipboard) building.getClipboard()).cache(b -> {
					if(!b) e.getPlayer().sendMessage(Prefixs.ERROR + "Ошибка прогрузки, обратитесь к администраторам!");
					else Title.sendActionBar(e.getPlayer(), Prefixs.SYS + "Постройка прогрузилась!");
				});
			} else if(building.isRegion(l) && building.inhereBuilding(l)) {
				e.setCancelled(true);
			} 
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.isCancelled()) return;
		if(e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.SOIL) {
			e.setCancelled(true);
			return;
		}
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Location l = e.getClickedBlock().getLocation();
		Building building = Building.getBuildings().parallelStream().filter(b -> b.isRegion(l)).findFirst().orElse(null);
		if(building != null) {
			if(building.isDestroyed()) return;
			Civilization civ = building.getTown().getCivilization();
			CivPlayer player = CivPlayer.wrap(e.getPlayer());
			boolean war = player.getCivilization() != null && Diplomacy.getRelationship(player.getCivilization(), civ).isWar();
			if(!building.getTown().equals(player.getTown()) && !war) {
				player.sendMessage("&cЭта территория принадлежит городу " +  building.getTown().getName() + "!");
				e.setCancelled(true);
			}
		}
	}
}
