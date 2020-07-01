package ru.greenpix.civilization.commands.chat;

import java.util.Comparator;

import org.bukkit.Location;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.Region;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.listeners.PlayerListener;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.processes.PreviewBuilding;
import ru.greenpix.civilization.utils.LocationUtils;

public class ChatCreationCivilization extends ChatStepByStep {

	public final static int MIN_DISTANCE = 200;
	
	private PreviewBuilding preview = null;
	
	private Location location = null;
	
	private final Style style;
	
	public ChatCreationCivilization(Style townhall) {
		this.style = townhall;
		setMessages(
				p ->
				"&eНапишите название Вашей цивилизации в чат (кириллица поддерживается)." + "\n" +
				"&eДля отмены напишите в чат &ccancel &eили &cотмена&e.",
				p ->
				"&eНапишите тег Вашей цивилизации в чат (длина в 3-4 буквы)." + "\n" +
				"&eИспользуйте только латинские буквы" + "\n" +
				"&eДля отмены напишите в чат &ccancel &eили &cотмена&e.",
				p ->
				"&eНапишите название Вашей столицы в чат (кириллица поддерживается)." + "\n" +
				"&eДля отмены напишите в чат &ccancel &eили &cотмена&e.",
				p ->
				"&eВы создаете цивилизацию " + getResults()[0] + "(" + getResults()[1] + ") со столицей " + getResults()[2] + ".\n" +
				"&eНапишите в чат &ayes &eили &aда &eдля создания цивилизации." + "\n" +
				"&eДля перемещения ратушы напишите в чат &bmove &eили &bпередвинуть&e." + "\n" +
				"&eДля отмены напишите в чат &ccancel &eили &cотмена&e."
		);
		setSteps((p, args) -> {
					if(checkCancel(args, p)) return false;
					if(args.length() < 3) {
						p.sendMessage("&cДлина названия Вашей цивилизации должна быть не меньше 3 букв!");
						return false;
					}
					if(args.length() > 16) {
						p.sendMessage("&cДлина названия Вашей цивилизации должна быть не больше 16 букв!");
						return false;
					}
					if(!args.matches("[а-яА-Яa-zA-Z0-9_]*")) {
						p.sendMessage("&cИспользуйте только латинские и русские буквы!");
						return false;
					}
					if(Civilization.getByName(args) != null) {
						p.sendMessage("&cЦивилизация с названием " + args + " уже существует!");
						return false;
					}
					return true;
				},
				(p, args) -> {
					if(checkCancel(args, p)) return false;
					if(args.length() != 3 && args.length() != 4) {
						p.sendMessage("&cДлина тега Вашей цивилизации должна быть равна 3 или 4 буквам!");
						return false;
					}
					if(!args.matches("[a-zA-Z]*")) {
						p.sendMessage("&cИспользуйте только латинские буквы!");
						return false;
					}
					if(Civilization.getCivilizations().stream().anyMatch(c -> c.getTag().equalsIgnoreCase(args))) {
						p.sendMessage("&cТег " + args + " уже занят!");
						return false;
					}
					return true;
				},
				(p, args) -> {
					if(checkCancel(args, p)) return false;
					if(args.length() < 3) {
						p.sendMessage("&cДлина названия Вашей столицы должна быть не меньше 3 букв!");
						return false;
					}
					if(args.length() > 16) {
						p.sendMessage("&cДлина названия Вашей цивилизации должна быть не больше 16 букв!");
						return false;
					}
					if(!args.matches("[а-яА-Яa-zA-Z0-9_]*")) {
						p.sendMessage("&cИспользуйте только латинские и русские буквы!");
						return false;
					}
					if(Town.getByName(args) != null) {
						p.sendMessage("&cГород с названием " + args + " уже существует!");
						return false;
					}
					location = p.getLocation();
					preview = new PreviewBuilding(p.toBukkit(), style, true).start();
					return true;
				},
				(p, args) -> {
					if(preview == null) {
						p.sendMessage("&7Постройка прогружается, подождите пару секунд...");
						return false;
					}
					if(args.equalsIgnoreCase("move") || args.equalsIgnoreCase("передвинуть")) {
						PreviewBuilding pre = preview;
						location = null;
						preview = null;
						pre.close(() -> {
							location = p.getLocation();
							preview = new PreviewBuilding(p.toBukkit(), style, true).start();
							p.sendMessage("&aВы переместили ратушу! " + "\n" +
					                      "&eНапишите в чат &ayes &eили &aда &eдля создания цивилизации." + "\n" +
					                      "&eДля перемещения ратушы напишите в чат &bmove &eили &bпередвинуть&e." + "\n" +
					                      "&eДля отмены напишите в чат &ccancel &eили &cотмена&e.");
						});
						return false;
					}
					if(args.equalsIgnoreCase("yes") || args.equalsIgnoreCase("да")) {
						if(preview.getRegion().getPos2().getBlockY() > 255) {
							p.sendMessage("&cПостройка слишком высоко!");
							p.sendMessage("&cВыберите другое место для строительства!");
							return false;
						}
						if(!Region.getWorldBorder(preview.getRegion().getWorld()).contains(preview.getRegion())) {
							p.sendMessage("&cПостройка располагается за пределами мира!");
							p.sendMessage("&cВыберите другое место для строительства!");
							return false;
						}
						Building b = Building.getBuildings().stream()
								.min(Comparator.comparing(e -> LocationUtils.distance2D(e.getCenter(), location)))
								.orElse(null);
						if(b != null && b.getCenter().distance(location) < ChatCreationCivilization.MIN_DISTANCE) {
							p.sendMessage("&cТы находишься слишком близко к городу " + b.getTown().getName() + "!");
							p.sendMessage("&6Перемести постройку немного подальше, примерно на " + ((int) (ChatCreationCivilization.MIN_DISTANCE - b.getCenter().distance(location))) + " блоков.");
							return false;
						}
						return true;
					}
					preview.close();
					cancel(p);
					return false;
				}
		);
	}
	
	@Override
	public void cancel(CivPlayer p) {
		super.cancel(p);
		PlayerListener.giveCivRod(p.toBukkit());
	}
	
	public boolean checkCancel(String arg, CivPlayer p) {
		if(arg.equalsIgnoreCase("cancel") || arg.equalsIgnoreCase("отмена") || arg.equalsIgnoreCase("no") || arg.equalsIgnoreCase("n") || arg.equalsIgnoreCase("c") || arg.equalsIgnoreCase("нет")) {
			if(preview != null) preview.close();
			cancel(p);
			return true;
		}
		return false;
	}

	@Override
	public void onComplete(CivPlayer player) {
		preview.close();
		try {
			long ms = System.currentTimeMillis();
			Civilization civ = new Civilization(player.toBukkit(), getResults()[0], getResults()[1]);
			Town capital = new Town(civ, player.toBukkit(), location, getResults()[2]);
			capital.writeSql();
			civ.setCapital(capital);
			civ.writeSql();
			capital.addMember(player);
			capital.getGroupOfLeaders().add(player);
			civ.getGroupOfLeaders().add(player);
			CivCore.broadcast("Основана новая цивилизация под названием &6'" + civ.getName() + "'&f игроком " + player.getName() + ".");
			CivCore.broadcastTitle("&lОснована новая цивилизация!", "Название: &a" + civ.getName() + "&f, Создатель: &a" + player.getName(), 10, 60, 10);
			CivCore.getInstance().getLogger().info("Civilization '" + civ.getName() + "' created for " + (System.currentTimeMillis() - ms) + " ms.");
		} catch (Throwable e) {
			player.sendMessage("&4ERROR: Произошла критическая ошибка. Попробуйте создать цивилизацию еще раз.");
			player.sendMessage("&c" + e.getClass().getSimpleName() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
}
