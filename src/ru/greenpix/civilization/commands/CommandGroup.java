package ru.greenpix.civilization.commands;

import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import ru.greenpix.civilization.groups.Group;
import ru.greenpix.civilization.groups.Perms;
import ru.greenpix.civilization.objects.ICObject;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.utils.RunnableManager;

public class CommandGroup extends AbstractCommand {

	private ICObject home;
	
	public CommandGroup(String parent, ICObject home) {
		super(parent);
		this.home = home;
		addArgumentType(Group.class, a -> home.getGroupByName(a), "Группы '%value%' не существует.");
	}
	
	@Override
	public void showHelp(CommandSender sender, String label) {
		super.showTitle(sender, "&a&lКоманды Групп");
		super.showHelp(sender, label);
	}
	
	@Override
	public void onCommand(CommandSender sender, String label, String[] args) {
		showHelp(sender, label);
	}
	
	@SubCommand(aliases = {}, desc = "Посмотреть список всех групп")
	public void list(CivPlayer sender) {
		showTitle(sender.toBukkit(), "&aСписок всех групп");
		home.getGroups().stream()
				.map(e -> " &7-> &e" + e.getName() + (e.isDefault() ? " ✪" : "") + " &7- &a" + e.getOnline().size() + "&7/&a" + e.size() + "&7 игроков онлайн")
				.collect(Collectors.toList())
				.forEach(sender::sendMessage);
	}
	
	@SubCommand(aliases = {"permlist"}, desc = "Посмотреть список всех прав в игре")
	public void perms(CivPlayer sender) {
		showTitle(sender.toBukkit(), "&aСписок всех прав для групп");
		Perms.find(home.getClass()).stream()
				.map(e -> " &7-> &e" + e.permission + " &7- " + e.description)
				.collect(Collectors.toList())
				.forEach(sender::sendMessage);
	}
	
	@SubCommand(aliases = {"new"}, desc = "Создать группу")
	public void create(CivPlayer sender,
			@Argument("name") String name) {
		if(home.getGroupByName(name) != null) {
			sender.sendMessage("&cГруппа '" + name + "' уже существует!");
			return;
		}
		Group group = new Group(home, name);
		sender.sendMessage("&aГруппа '" + group.getName() + "' была успешно создана!");
	}
	
	@SubCommand(aliases = {"del"}, desc = "Удалить группу")
	public void delete(CivPlayer sender,
			@Argument("group") Group group) {
		if(group.isDefault()) {
			sender.sendMessage("&cНельзя удалить системную группу!");
			return;
		}
		home.getGroups().remove(group);
		RunnableManager.async(() -> group.deleteSql());
		sender.sendMessage("&aГруппа '" + group.getName() + "' была успешно удалена!");
	}
	
	@SubCommand(aliases = {}, desc = "Добавить игрока в группу")
	public void add(CivPlayer sender,
			@Argument("group") Group group,
			@Argument("player") String player) {
		if(group.getName().equalsIgnoreCase(Group.GROUP_OF_MEMBERS)) {
			sender.sendMessage("&cНельзя добавить игрока в базовую группу! Используйте: &6/town add &e[player]");
			return;
		}
		if(group.containsIgnoreCase(player)) {
			sender.sendMessage("&cИгрок '" + player + "' уже в группе '" + group.getName() + "'");
			return;
		}
		group.add(player);
		sender.sendMessage("&aИгрок '" + player + "' был добавлен в группу '" + group.getName() + "'!");
	}
	
	@SubCommand(aliases = {}, desc = "Убрать игрока из группы")
	public void remove(CivPlayer sender,
			@Argument("group") Group group,
			@Argument("player") String player) {
		if(group.getName().equalsIgnoreCase(Group.GROUP_OF_MEMBERS)) {
			sender.sendMessage("&cНельзя удалить игрока из базовой группы! Используйте: &6/town kick &e[player]");
			return;
		}
		if(group.getName().equalsIgnoreCase(Group.GROUP_OF_LEADERS) && home.getOwner().equalsIgnoreCase(player)) {
			sender.sendMessage("&cНельзя удалить создателя '" + home.getName() + "' из высшей группы!");
			return;
		}
		if(group.containsIgnoreCase(player)) {
			sender.sendMessage("&cИгрок '" + player + "' не в группе '" + group.getName() + "'");
			return;
		}
		group.remove(player);
		sender.sendMessage("&aИгрок '" + player + "' был удален из группы '" + group.getName() + "'!");
	}
	
	@SubCommand(aliases = {}, desc = "Убрать права из группы")
	public void addperm(CivPlayer sender,
			@Argument("group") Group group,
			@Argument("perm") String perm) {
		if(group.getName().equalsIgnoreCase(Group.GROUP_OF_LEADERS)) {
			sender.sendMessage("&cНельзя изменять права высшей группы!");
			return;
		}
		if(group.hasPermission(perm)) {
			sender.sendMessage("&cГруппа '" + group.getName() + "' уже имеет права '" + perm + "'");
			return;
		}
		group.addPermission(perm);
		sender.sendMessage("&aПрава были добавлены в группу '" + group.getName() + "'!");
	}
	
	@SubCommand(aliases = {}, desc = "Добавить права в группу")
	public void removeperm(CivPlayer sender,
			@Argument("group") Group group,
			@Argument("perm") String perm) {
		if(group.getName().equalsIgnoreCase(Group.GROUP_OF_LEADERS)) {
			sender.sendMessage("&cНельзя изменять права высшей группы!");
			return;
		}
		if(!group.hasPermission(perm)) {
			sender.sendMessage("&cГруппа '" + group.getName() + "' не имеет права '" + perm + "'");
			return;
		}
		group.removePermission(perm);
		sender.sendMessage("&aПрава были удалены из группы '" + group.getName() + "'!");
	}
	
	@SubCommand(aliases = {}, desc = "Посмотреть информацию о группе")
	public void info(CivPlayer sender,
			@Argument("group") Group group) {
		showTitle(sender.toBukkit(), "&aИнформация о Группе '" + group.getName() + "'");
		sender.sendMessage(" &7-> &eОнлайн: &a" + group.getOnline().size() + "&7/&a" + group.size());
		sender.sendMessage(" &7-> &eЖители: &7" + group.stream().map(e -> CivPlayer.isOnline(e) ? "&a" + e + "&7" : "&7" + e).collect(Collectors.toList()));
		sender.sendMessage(" &7-> &eПрава: &7" + group.getPermissions());
	}
}
