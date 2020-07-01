package ru.greenpix.civilization.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.diplomacy.Diplomacy;
import ru.greenpix.civilization.diplomacy.Relationship;
import ru.greenpix.civilization.diplomacy.Status;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.player.Request;
import ru.greenpix.civilization.utils.StringUtils;
import ru.greenpix.developer.utils.protocol.sounds.FixedSound;

public class CommandDiplomacy extends AbstractCommand {

	{
		addArgumentType(Status.class, a -> Status.getByName(a), "Используйте war/hostile/neutral/peace/ally в аргументе %arg%.");
	}
	
	@Override
	public void showHelp(CommandSender sender, String label) {
		super.showTitle(sender, "&a&lКоманды Дипломатии");
		super.showHelp(sender, label);
	}
	
	@Override
	public void onCommand(CommandSender sender, String label, String[] args) {
		showHelp(sender, label);
	}
	
	@SubCommand(aliases = {}, desc = "Посмотреть отношения цивилизации")
	public void show(CommandSender sender, 
			@Argument("civ") Civilization civ) {
		
	}
	
	@SubCommand(aliases = {}, desc = "Посмотреть отношения всех цивилизаций")
	public void global(ColorCommandSender sender,
			@Argument(value = "status", required = false) Status status) {
		List<Relationship> list;
		if(status == null) list = Diplomacy.getGlobal();
		else list = Diplomacy.getGlobal(status);
		showTitle(sender, "&aМировые Отношения");
		if(list.size() == 0) sender.sendMessage(" &7Пока что тут ничего нет...");
		else list.forEach(r -> sender.sendMessage(" &7-> &f" + r.getSender().getName() + " &7>>> " + r.getStatus().color + "&l" + r.getStatus().getDisplayName().toUpperCase() + "&7 >>> &f" + r.getReceiver().getName()));
	}
	
	@MustHaveHome(Civilization.class)
	@SubCommand(aliases = {}, desc = "Предложить заключить нейтралитет/мир/союз")
	public void request(CivPlayer sender, 
			@Argument("civ") Civilization civ, 
			@Argument("neutral/peace/ally") Status status, 
			@Argument("reason") @BlindArgument String comment) {
		if(!sender.hasPermission(sender.getCivilization(), status.getPermission())) {
			sender.sendMessage("&cУ вас нет прав заключать " + status.getDisplayName() + "! Обратитесь к создателю цивилизации.");
			return;
		}
		if(sender.getCivilization().equals(civ)) {
			sender.sendMessage("&cВы не можете заключить " + status.getDisplayName() + " со своей цивилизацией.");
			return;
		}
		if(status == Status.WAR || status == Status.HOSTILE) {
			sender.sendMessage("&cНельзя заключить войну или вражду. Используйте: &7/dip declare &e" + civ.getName() + " " + status.name().toLowerCase() + " [причина]");
			return;
		}
		if(comment.length() > 64) {
			sender.sendMessage("&cВаша причина слишком большая! Уложитесь в 64 символа.");
			return;
		}
		if(Diplomacy.getRelationship(sender.getCivilization(), civ).getStatus() == status) {
			sender.sendMessage("&cВы уже заключили " + status.getDisplayName() + " с цивилизацией " + civ.getName() + ".");
			return;
		}
		new Request(sender.getCivilization().getName() + " предлагает заключить " + status.color + "&l" + status.getDisplayName().toUpperCase() + "\n" +
		" &7-> &fПричина: &e" + comment, 
				() -> {
					Diplomacy.add(new Relationship(status, sender.getCivilization(), civ, comment)); 
					CivCore.broadcastTitle(sender.getCivilization().getName() + " заключила " + status.color + "&l" + status.getDisplayName().toUpperCase() + "&f с " + civ.getName(), "Причина: &e" + comment, 10, 80, 10);
					CivCore.broadcastSound(FixedSound.LEVEL_UP, 2f);
					CivCore.broadcastSound(status.getSound(), 1f);
				},
				status.getPermission()).call(sender, sender.getCivilization(), civ);
	}
	
	@MustHaveHome(Civilization.class)
	@SubCommand(aliases = {}, desc = "Объявить войну/вражду")
	public void declare(CivPlayer sender, 
			@Argument("civ") Civilization civ, 
			@Argument("war/hostile") Status status, 
			@Argument("reason") @BlindArgument String comment) {
		if(!sender.hasPermission(sender.getCivilization(), status.getPermission())) {
			sender.sendMessage("&cУ вас нет прав объявлять " + StringUtils.replaceLast(status.getDisplayName(), "у") + "! Обратитесь к создателю цивилизации.");
			return;
		}
		if(sender.getCivilization().equals(civ)) {
			sender.sendMessage("&cВы не можете объявить войну или вражду своей цивилизации.");
			return;
		}
		if(status != Status.WAR && status != Status.HOSTILE) {
			sender.sendMessage("&cОбъявить можно только войну или вражду. Используйте: &7/dip request &e" + civ.getName() + " " + status.name().toLowerCase() + " [причина]");
			return;
		}
		if(status == Status.WAR && civ.getGroupOfMembers().online() == 0) {
			sender.sendMessage("&cВы не можете объявить войну цивилизации, в которой нет игроков онлайн.");
			return;
		}
		if(comment.length() > 64) {
			sender.sendMessage("&cВаша причина слишком большая! Уложитесь в 64 символа.");
			return;
		}
		Relationship r = Diplomacy.getRelationship(sender.getCivilization(), civ);
		// Возможно ли объявить вражду или войну
		if(r.getStatus() == status) {
			sender.sendMessage("&cВы уже в состоянии " + StringUtils.replaceLast(status.getDisplayName(), "ы") + " с цивилизацией " + civ.getName() + ".");
			return;
		}
		if(r.isWar()) {
			sender.sendMessage("&cК сожалению, вы уже воюете =(");
			return;
		}
		if(r.isHostile() && System.currentTimeMillis() - r.getTimestamp().getTime() < 1000 * 600) {
			sender.sendMessage("&cВы можете объявить войну только через 10 минут после объявления вражды.");
			return;
		} 
		if(r.isPeace() && System.currentTimeMillis() - r.getTimestamp().getTime() < 1000 * 1800) {
			sender.sendMessage("&cВы можете объявить войну только через 30 минут после заключения мира.");
			return;
		} else if(r.isAlly()) {
			if(status == Status.WAR) {
				sender.sendMessage("&cВы не можете объявить войну своим союзникам!");
				return;
			} 
			if(System.currentTimeMillis() - r.getTimestamp().getTime() < 1000 * 3600) {
				sender.sendMessage("&cВы можете объявить вражду только через 60 минут после заключения союза.");
				return;
			} 
		}
		int dif = sender.getCivilization().getEra().index() - civ.getEra().index();
		if(status == Status.WAR && dif > 1) {
			sender.sendMessage("&cВы не можете объявить войну цивилизации, которая по развитию на " + dif + " эры ниже.");
			return;
		}
		if(status == Status.HOSTILE && dif > 3) {
			sender.sendMessage("&cВы не можете объявить вражду цивилизации, которая по развитию на " + dif + " эры ниже.");
			return;
		}
		// Штрафы
		
		Diplomacy.add(new Relationship(status, sender.getCivilization(), civ, comment));
		CivCore.broadcastTitle(sender.getCivilization().getName() + " объявила " + status.color + "&l" + StringUtils.replaceLast(status.getDisplayName(), "у").toUpperCase() + "&f " + civ.getName(), "Причина: &e" + comment, 10, 80, 10);
		CivCore.broadcastSound(status.getSound(), 1f);
	}
	
	/*
	@SubCommand(aliases = {}, desc = "Подарить город цивилизации")
	public void gifttown(Player sender, 
			@Argument("civ") Civilization civ, 
			@Argument("town") Town town, 
			@Argument("reason")@BlindArgument String reason) {
		
	}
	
	@SubCommand(aliases = {}, desc = "Предложить купить город у цивилизации")
	public void buytown(Player sender, 
			@Argument("civ") Civilization civ, 
			@Argument("town") Town town, 
			@Argument("money") int money) {
		
	}
	*/
}
