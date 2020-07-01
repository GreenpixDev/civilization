package ru.greenpix.civilization.commands.chat;

import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.commands.chat.ChatStepByStep.Step;

public class ChatDeleteBuilding extends ChatConfirm {

	private final Building building;
	
	public ChatDeleteBuilding(Building b) {
		super(
				"&6&lВы уверены, что хотите удалить постройку '" + b.getStructure().getDisplayName() + "'?\n" +
				"&eНапишите в чат &ayes &eили &aда&e, чтобы удалить постройку.\n" +
	            "&eДля отмены напишите в чат &ccancel &eили &cотмена&e."
		);
		this.building = b;
	}
	
	@Override
	public Step getStep() {
		return (p, arg) -> {
			p.sendMessage("&aПостройка '" + building.getStructure().getDisplayName() + "' была удалена!");
			building.remove();
			return true;
		};
	}
}
