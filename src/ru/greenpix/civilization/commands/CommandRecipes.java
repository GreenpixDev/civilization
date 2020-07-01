package ru.greenpix.civilization.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.greenpix.civilization.guises.GuiseRecipes;

public class CommandRecipes extends AbstractCommand {

	private GuiseRecipes guise = new GuiseRecipes();
	
	@Override
	public void onCommand(CommandSender sender, String label, String[] args) {
		if(sender instanceof Player) {
			guise.open((Player) sender);
		} else {
			sender.sendMessage(ONLY_FOR_PLAYERS);
		}
	}

}
