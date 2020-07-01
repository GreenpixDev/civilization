package ru.greenpix.civilization.commands;

import java.util.Comparator;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.diplomacy.Diplomacy;
import ru.greenpix.civilization.diplomacy.Status;
import ru.greenpix.civilization.groups.Perms;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.player.Request;
import ru.greenpix.civilization.utils.StringUtils;

public class CommandTown extends AbstractCommand {

	@Override
	public void showHelp(CommandSender sender, String label) {
		super.showTitle(sender, "&b&lКоманды Города");
		super.showHelp(sender, label);
	}
	
	@Override
	public void onCommand(CommandSender sender, String label, String[] args) {
		showHelp(sender, label);
	}
	
	@SubCommand(aliases = {}, desc = "Посмотреть список всех городов")
	public void list(ColorCommandSender sender) {
		String msg = Town.getTowns().stream()
				.map(e -> (e.isCaptured() ? "&6" : e.isCapital() ? "&a" : "&f") + e.getName() + "&7")
				.collect(Collectors.toList()).toString();
		sender.sendMessage("Список Городов (" + Town.getTowns().size() + "): &7" + msg);
	}
	
	@SubCommand(aliases = {}, desc = "Посмотреть топ 10 городов")
	public void top(ColorCommandSender sender) {
		Town[] top = Town.getTowns().stream()
				.sorted(Comparator.comparing(Town::getGlobalScores).reversed())
				.limit(10)
				.toArray(Town[]::new);
		showTitle(sender, "&6&lТоп &c&l10 &6&lгородов");
		for(int i = 0; i < top.length; i++) {
			sender.sendMessage(" &7-> &e" + (i + 1) + ") " + top[i].getName() + " &7- &f" + top[i].getGlobalScores() + " очков");
		}
	}
	
	@MustHaveHome(Town.class)
	@GroupPermissions(Perms.TREASURE_WITHDRAW)
	@SubCommand(aliases = {}, desc = "Взять деньги из казны города")
	public void withdraw(CivPlayer sender, 
			@Argument("money") double money) {
		if(sender.getTown().getBalance() < money) {
			money = sender.getTown().getBalance();
		}
		sender.getTown().withdraw(money);
		sender.deposit(money);
		sender.sendMessage("Со счета казны города взято &a" + money + "&f монет.");
	}
	
	@MustHaveHome(Town.class)
	@GroupPermissions(Perms.TREASURE_DEPOSIT)
	@SubCommand(aliases = {}, desc = "Положить деньги в казну города")
	public void deposit(CivPlayer sender, 
			@Argument("money") double money) {
		if(sender.getBalance() < money) {
			money = sender.getBalance();
		}
		sender.getTown().deposit(money);
		sender.withdraw(money);
		sender.sendMessage("На счет казны города зачислено &a" + money + "&f монет.");
	}
	
	@MustHaveHome(Town.class)
	@GroupPermissions(Perms.MEMBERS_INVITE)
	@SubCommand(aliases = {"invite"}, desc = "Пригласить игрока в город")
	public void add(CivPlayer sender, 
			@Argument("player") CivPlayer player) {
		if(player.hasTown()) {
			sender.sendMessage("&cИгрок " + player.getName() + " уже проживает в городе.");
			return;
		}
		String msg = "Игрок " + sender.getName() + " приглашает вас в город &b" + sender.getTown().getName() + "&f!";
		new Request(msg, () -> {
			sender.getTown().addMember(player);
			sender.getTown().broadcast("Игрок " + player.getName() + " присоединился к городу.");
		}).call(sender, sender, player);
	}
	
	@MustHaveHome(Town.class)
	@SubCommand(aliases = {"q", "leave"}, desc = "Покинуть город")
	public void quit(CivPlayer sender) {
		if(sender.getName().equalsIgnoreCase(sender.getTown().getOwner())) {
			sender.sendMessage("&cВы не можете покинуть город, так как вы его создали.");
			return;
		}
		sender.getTown().broadcast("Игрок " + sender.getName() + " покинул город.");
		sender.getTown().removeMember(sender.getName());
	}
	
	@MustHaveHome(Town.class)
	@GroupPermissions(Perms.MEMBERS_KICK)
	@SubCommand(aliases = {}, desc = "Выгнать игрока из города")
	public void kick(CivPlayer sender, 
			@Argument("player") String player) {
		if(sender.getName().equals(player)) {
			quit(sender);
			return;
		}
		if(player.equalsIgnoreCase(sender.getTown().getOwner())) {
			sender.sendMessage("&cВы не можете кикнуть создателя города.");
			return;
		}
		if(!sender.getTown().getGroupOfMembers().containsIgnoreCase(player)) {
			sender.sendMessage("&cИгрок " + player + " не проживает в вашем городе.");
			return;
		}
		sender.getTown().broadcast("Игрок " + player + " был выгнан из города.");
		sender.getTown().removeMember(player);
	}	
	
	@MustHaveHome(Town.class)
	@SubCommand(aliases = {"tp"}, desc = "Телепортирует вас в город")
	public void home(CivPlayer sender) {
		sender.teleport(sender.getTown().getHome());
	}
	
	@MustHaveHome(Town.class)
	@SubCommand(aliases = {}, desc = "Посмотреть доходы")
	public void income(CivPlayer sender) {
		int in;
		for(Building b : sender.getTown().getBuildings()) {
			in = b.getMoneyPerMinute();
			sender.sendMessage(" &7-> &e" + b.getDisplayName() + " " + (in < 0 ? "&c" : "&a+") + in + " монет/мин");
		}
	}
	
	@MustHaveHome(Town.class)
	@GroupPermissions("*")
	@SubCommand(aliases = {"groups"}, desc = "Управление группами")
	public void group(CivPlayer sender,
			@Argument(value = "args", required = false) String[] args) {
		new CommandGroup("town", sender.getTown())
		.execute(sender.toBukkit(), "group", args == null ? new String[0] : args);
	}
	
	@SubCommand(aliases = {"show"}, desc = "Посмотреть информацию о своем или другом городе")
	public void info(CivPlayer sender, 
			@Argument(value = "town", required = false) Town town) {
		if(town != null) {
			sendInfo(sender, town);
		} else if(sender.getTown() == null) {
			sender.sendMessage("&cУкажите название города.");
		} else {
			sendInfo(sender, sender.getTown());
		}
	}
	
	/*
	@MustHaveHome(Town.class)
	@SubCommand(aliases = {"giveup"}, desc = "Признать поражение и уступить город. После этого можно использовать /town quit")
	public void capitulate(CivPlayer sender) {
		if(!sender.getName().equalsIgnoreCase(sender.getTown().getOwner())) {
			sender.sendMessage("&cЭта команда доступна только лидеру (мэру) города!");
			return;
		}
		if(!sender.getTown().isCaptured()) {
			sender.sendMessage("&cВаш город не был захвачен!");
			return;
		}
		sender.getTown().giveup();
	}
	*/
	
	private void sendInfo(CivPlayer receiver, Town town) {
		int income = town.getMoneyPerMinute();
		showTitle(receiver.toBukkit(), "&b&lИнформация о " + town.getName());
		receiver.sendMessage(" &7-> &6Цивилизация: &e" + town.getCivilization().getName());
		receiver.sendMessage(" &7-> &6Родная Цивилизация: &e" + town.getMother().getName());
		receiver.sendMessage(" &7-> &6Основана: &e" + StringUtils.formatDateAndTime(town.getTimestamp()));
		receiver.sendMessage(" &7-> &6Основатель: &e" + town.getOwner());
		receiver.sendMessage(" &7-> &6Очков: &e" + town.getGlobalScores());
		receiver.sendMessage(" &7-> &6Казна: &e" + town.getBalance() + " монет");
		receiver.sendMessage(" &7-> &6Доход: " + (income < 0 ? "&c" : "&e") + income + " монет/мин");
		receiver.sendMessage(" &7-> &6Производство: &e" + town.getHammersPerMinute() + " молоточков/мин");
		receiver.sendMessage(" &7-> &6Построек: &e" + town.getBuildings().size());
		receiver.sendMessage(" &7-> &6Локация: &e" + StringUtils.formatLocation(town.getCenter()));
		receiver.sendMessage(" &7-> &6Войн: &c" + Diplomacy.getRelations(town.getCivilization(), Status.WAR).size() +
				"&6, Союзников: &a" + Diplomacy.getRelations(town.getCivilization(), Status.ALLY).size());
		receiver.sendMessage(" &7-> &6Игроков (" + town.getGroupOfMembers().size() + "): &e" + town.getGroupOfMembers().stream().map(e -> (CivPlayer.isOnline(e) ? "&a" : "&7") + e + "&e").collect(Collectors.toList()));
	}
}
