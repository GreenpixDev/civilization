package ru.greenpix.civilization.commands;

import java.util.Comparator;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import ru.greenpix.civilization.diplomacy.Diplomacy;
import ru.greenpix.civilization.diplomacy.Status;
import ru.greenpix.civilization.groups.Perms;
import ru.greenpix.civilization.listeners.PlayerListener;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.utils.StringUtils;

public class CommandCivilization extends AbstractCommand {

	@Override
	public void showHelp(CommandSender sender, String label) {
		super.showTitle(sender, "&6&lКоманды Цивилизации");
		super.showHelp(sender, label);
	}
	
	@Override
	public void onCommand(CommandSender sender, String label, String[] args) {
		showHelp(sender, label);
	}
	
	@SubCommand(aliases = {}, desc = "Получить жезл основания цивилизации")
	public void get(CivPlayer p) {
		if(p.getCivilization() != null) {
			p.sendMessage("&cТы уже живешь в цивилизации.");
			return;
		}
		PlayerListener.giveCivRod(p.toBukkit());
	}
	
	@SubCommand(aliases = {}, desc = "Посмотреть список всех цивилизаций")
	public void list(ColorCommandSender sender) {
		String msg = Civilization.getCivilizations().stream()
				.sorted(Comparator.comparing(e -> e.getEra().index()))
				.map(e -> e.getEra().getColor() + e.getName() + "&7")
				.collect(Collectors.toList()).toString();
		sender.sendMessage("Список Городов (" + Civilization.getCivilizations().size() + "): &7" + msg);
	}
	
	@SubCommand(aliases = {}, desc = "Посмотреть топ 10 цивилизаций")
	public void top(ColorCommandSender sender) {
		Civilization[] top = Civilization.getCivilizations().stream()
				.sorted(Comparator.comparing(Civilization::getGlobalScores).reversed())
				.limit(10)
				.toArray(Civilization[]::new);
		showTitle(sender, "&6&lТоп &c&l10 &6&lцивилизаций");
		for(int i = 0; i < top.length; i++) {
			sender.sendMessage(" &7-> &e" + (i + 1) + ") " + top[i].getName() + " &7- &f" + top[i].getGlobalScores() + " очков");
		}
	}
	
	@MustHaveHome(Civilization.class)
	@SubCommand(aliases = {}, desc = "Назначить мера города")
	public void setmayor(CivPlayer sender,
			@Argument("town") Town town,
			@Argument("player") CivPlayer player) {
		if (!sender.getCivilization().getOwner().equals(sender.getName())) {
			sender.sendMessage("&cЭто действие доступно только основателю цивилизации!");
			return;
		}
		if (!town.getCivilization().equals(sender.getCivilization())) {
			sender.sendMessage("&cЭто не ваш город!");
			return;
		}
		if (!sender.getCivilization().getGroupOfMembers().contains(player.getName())) {
			sender.sendMessage("&cЭтот игрок не живет в вашей цивилизации!");
			return;
		}
		player.getTown().broadcast("Игрок " + player.getName() + " покинул город.");
		player.getTown().getGroupOfMembers().remove(player.getName());
		player.getTown().getGroupOfLeaders().remove(player.getName());
		player.getTown().setOwner(player.getName());
		player.getTown().writeSql();
		
		town.setOwner(player.getName());
		town.getGroupOfMembers().add(player);
		town.getGroupOfLeaders().add(player);
		town.broadcast("Игрок " + player.getName() + " присоединился к городу.");
		town.writeSql();
		
		player.setHome(town);
		player.sendMessage("&aВас назначили мером города " + town.getName());
		player.sendMessage("&aИспользуйте /town home, чтобы телепортироваться в Ваш новый город!");
	}
	
	@MustHaveHome(Civilization.class)
	@GroupPermissions("*")
	@SubCommand(aliases = {"groups"}, desc = "Управление группами")
	public void group(CivPlayer sender,
			@Argument(value = "args", required = false) String[] args) {
		new CommandGroup("civ", sender.getCivilization())
		.execute(sender.toBukkit(), "group", args == null ? new String[0] : args);
	}
	
	@MustHaveHome(Civilization.class)
	@GroupPermissions(Perms.TREASURE_WITHDRAW)
	@SubCommand(aliases = {}, desc = "Взять деньги из казны города")
	public void withdraw(CivPlayer sender, 
			@Argument("money") double money) {
		if(sender.getCivilization().getBalance() < money) {
			money = sender.getCivilization().getBalance();
		}
		sender.getCivilization().withdraw(money);
		sender.deposit(money);
		sender.sendMessage("Со счета казны цивилизации взято &a" + money + "&f монет.");
	}
	
	@MustHaveHome(Civilization.class)
	@GroupPermissions(Perms.TREASURE_DEPOSIT)
	@SubCommand(aliases = {}, desc = "Положить деньги в казну города")
	public void deposit(CivPlayer sender, 
			@Argument("money") double money) {
		if(sender.getBalance() < money) {
			money = sender.getBalance();
		}
		sender.getCivilization().deposit(money);
		sender.withdraw(money);
		sender.sendMessage("На счет казны цивилизации зачислено &a" + money + "&f монет.");
	}
	
	@SubCommand(aliases = {"show"}, desc = "Посмотреть информацию о своей или другой цивилизации")
	public void info(CivPlayer sender, 
			@Argument(value = "town", required = false) Civilization civ) {
		if(civ != null) {
			sendInfo(sender, civ);
		} else if(sender.getCivilization() == null) {
			sender.sendMessage("&cУкажите название города.");
		} else {
			sendInfo(sender, sender.getCivilization());
		}
	}
	
	private void sendInfo(CivPlayer receiver, Civilization civ) {
		showTitle(receiver.toBukkit(), "&d&lИнформация о " + civ.getName());
		receiver.sendMessage(" &7-> &6Столица: &e" + civ.getCapital().getName());
		receiver.sendMessage(" &7-> &6Основана: &e" + StringUtils.formatDateAndTime(civ.getTimestamp()));
		receiver.sendMessage(" &7-> &6Основатель: &e" + civ.getOwner());
		receiver.sendMessage(" &7-> &6Очков: &e" + civ.getGlobalScores());
		receiver.sendMessage(" &7-> &6Казна: &e" + civ.getBalance() + " монет");
		receiver.sendMessage(" &7-> &6Наука: &e" + civ.getBeakersPerMinute() + " колбочек/мин");
		receiver.sendMessage(" &7-> &6Войн: &c" + Diplomacy.getRelations(civ, Status.WAR).size() +
				"&6, Союзников: &a" + Diplomacy.getRelations(civ, Status.ALLY).size());
		receiver.sendMessage(" &7-> &6Города (" + civ.getTowns().size() + "): &e" + civ.getTowns().stream().map(e -> (e.isCapital() ? "&a" : "&7") + e.getName() + "&e").collect(Collectors.toList()));
	}
}
