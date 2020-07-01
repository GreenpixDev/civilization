package ru.greenpix.civilization.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.common.Townhall;
import ru.greenpix.civilization.commands.chat.ChatDeleteBuilding;
import ru.greenpix.civilization.guises.GuiseList;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;

public class CommandBuild extends AbstractCommand {
	
	{
		addArgumentType(Structure.class, a -> Structure.getByName(a), "Постройки '%value%' не существует в игре!");
	}
	
	@Override
	public void showHelp(CommandSender sender, String label) {
		super.showTitle(sender, "&b&lКоманды Построек");
		super.showHelp(sender, label);
	}
	
	@MustHaveHome(Town.class)
	@Override
	public void onCommand(CommandSender sender, String label, String[] args) {
		if(args.length == 0) {
			if(sender instanceof Player) {
				new GuiseList.GuiseStructures("build").open((Player) sender);	
			} else {
				showHelp(sender, label);
			}
			return;
		}
		Structure str = Structure.getByName(args[0]);
		if(sender instanceof Player && str != null) {
			new GuiseList<>("build", "Выбор Стиля", str.getStyles()).open((Player) sender);
		} else {
			showHelp(sender, label);
		}
	}
	
	@MustHaveHome(Town.class)
	@SubCommand(aliases = {"destroy", "delete", "del"}, desc = "Удалить постройку, на которой вы находитесь")
	public void remove(CivPlayer sender) {
		Building b = Building.getBuildings().parallelStream().filter(e -> e.isRegion(sender.getLocation())).findFirst().orElse(null);
		if(b == null) {
			sender.sendMessage("&cВы должны находится на той постройке, которую хотите удалить.");
			return;
		}
		if(!sender.getTown().equals(b.getTown())) {
			sender.sendMessage("&cЭта постройка не принадлежит вашему городу!");
			return;
		}
		if(b instanceof Townhall) {
			sender.sendMessage("&cНельзя удалить " + b.getDisplayName() + "!");
			return;
		}
		new ChatDeleteBuilding(b).send(sender);
	}
	
	/*
	@SubCommand(aliases = {}, desc = "Починить постройку, на которой вы находитесь")
	public void repair(Player sender) {
		
	}
	
	@SubCommand(aliases = {}, desc = "Посмотреть список всех построек")
	public void list(Player sender) {
		
	}
	*/
}
