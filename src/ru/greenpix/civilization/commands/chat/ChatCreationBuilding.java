package ru.greenpix.civilization.commands.chat;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.commands.chat.ChatStepByStep.Step;
import ru.greenpix.civilization.groups.Perms;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.processes.PreviewBuilding;
import ru.greenpix.civilization.utils.LocationUtils;

public class ChatCreationBuilding extends ChatConfirm {
	
	private Style style;
	
	private PreviewBuilding preview;
	
	private Location location;
	
	public ChatCreationBuilding(Player player, Style style) {
		super(
				"&eНапишите в чат &ayes &eили &aда &eдля постройки \"" + style.getStructure().getDisplayName() + "\".\n" +
				"&eДля перемещения ратушы напишите в чат &bmove &eили &bпередвинуть&e." + "\n" +
	            "&eДля отмены напишите в чат &ccancel &eили &cотмена&e."
		);
		this.style = style;
		this.location = player.getLocation();
		this.preview = new PreviewBuilding(player, style, true).start();
	}
	
	@Override
	public Step getStep() {
		return (p, arg) -> onConfirm(p);
	}
	
	@Override
	public Step getOther() {
		return (p, arg) -> onCommand(p, arg);
	}
	
	@Override
	public void cancel(CivPlayer p) {
		super.cancel(p);
		preview.close();
	}
	
	public boolean onConfirm(CivPlayer sender) {
		if(preview == null) {
			sender.sendMessage("&7Постройка прогружается, подождите пару секунд...");
			return false;
		}
		if(preview.getRegion().getPos2().getBlockY() > 255) {
			sender.sendMessage("&cПостройка слишком высоко!");
			sender.sendMessage("&cВыберите другое место для строительства!");
			return false;
		}
		if(!sender.getTown().getTownhall().getWorld().equals(sender.getLocation().getWorld()) || 
				LocationUtils.distance2D(sender.getTown().getCenter(), location) > sender.getTown().getBorder()) {
			sender.sendMessage("&cПостройка находится слишком далеко от вашего города!");
			sender.sendMessage("&cВыберите другое место для строительства!");
			return false;
		}
		for(Building building : Building.getBuildings()) {
			if(building.getRegion().concerns2D(preview.getRegion())) {
				sender.sendMessage("&cПостройка находится слишком близко к постройке \"" + building.getStructure().getDisplayName() + "\"!");
				sender.sendMessage("&cВыберите другое место для строительства!");
				return false;
			}
		}
		if(sender.getTown().hasDebt()) {
			sender.sendMessage("&cВы не можете строить постройки, пока Ваш город не выплатит долг.");
			return false;
		}
		if(sender.getTown().getProcess() != null && !sender.getTown().getProcess().isCompleted()) {
			sender.sendMessage("&cВы не можете строить 2 постройки одновременно!");
			return false;
		}
		if(sender.getBalance() < getStructure().getCost()) {
			if(sender.hasPermission(sender.getTown(), Perms.TREASURE_USE) && sender.getTown().getBalance() >= getStructure().getCost()) {
				try {
					preview.close();
					sender.getTown().build(location, style);
					sender.getTown().withdraw(getStructure().getCost());
				} catch (Throwable e) {
					sender.sendMessage("&4ERROR: Произошла критическая ошибка. Попробуйте создать постройку еще раз.");
					sender.sendMessage("&c" + e.getClass().getSimpleName() + ": " + e.getMessage());
					e.printStackTrace();
				}
				return true;
			}
			sender.sendMessage("&cНедостаточно монет для постройки (у вас " + sender.getBalance() + " монет).");
			return false;
		}
		try {
			preview.close();
			sender.getTown().build(location, style);
			sender.withdraw(getStructure().getCost());
		} catch (Throwable e) {
			sender.sendMessage("&4ERROR: Произошла критическая ошибка. Попробуйте создать постройку еще раз.");
			sender.sendMessage("&c" + e.getClass().getSimpleName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean onCommand(CivPlayer sender, String arg) {
		if(preview == null) {
			sender.sendMessage("&7Постройка прогружается, подождите пару секунд...");
			return false;
		}
		if(arg.equalsIgnoreCase("move") || arg.equalsIgnoreCase("передвинуть")) {
			PreviewBuilding pre = this.preview;
			this.preview = null;
			this.location = null;
			pre.close(() -> {
				location = sender.getLocation();
				preview = new PreviewBuilding(sender.toBukkit(), style, true).start();
				sender.sendMessage("&aВы переместили постройку!");
				send(sender);
			});
			return false;
		}
		return true;
	}
	
	public Structure getStructure() {
		return style.getStructure();
	}

}
