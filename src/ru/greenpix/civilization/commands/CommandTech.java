package ru.greenpix.civilization.commands;

import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.greenpix.civilization.guises.GuiseList;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.technologies.Technology;

public class CommandTech extends AbstractCommand {

	{
		addArgumentType(Technology.class, a -> Technology.getByName(a), "Технологии '%value%' не существует в игре!");
	}
	
	@Override
	public void showHelp(CommandSender sender, String label) {
		super.showTitle(sender, "&b&lКоманды Изучения");
		super.showHelp(sender, label);
	}
	
	@MustHaveHome(Civilization.class)
	@Override
	public void onCommand(CommandSender sender, String label, String[] args) {
		if(args.length == 0) {
			if(sender instanceof Player) {
				new GuiseList.GuiseTechnologies("research").open((Player) sender);	
			} else {
				showHelp(sender, label);
			}
			return;
		}
		showHelp(sender, label);
	}
	
	@MustHaveHome(Civilization.class)
	@SubCommand(aliases = {"list"}, desc = "Посмотреть список изученных технологий")
	public void researched(CivPlayer sender) {
		String msg = sender.getCivilization().getTechnologies().stream()
				.map(e -> "&b" + e.getName() + "&7")
				.collect(Collectors.toList()).toString();
		sender.sendMessage("Изученные Технологии (" + sender.getCivilization().getTechnologies().size() + "): &7" + msg);
	}
}
