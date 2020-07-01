package ru.greenpix.civilization.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.greenpix.civilization.guises.GuiseRecipes;
import ru.greenpix.civilization.items.CustomItems;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.processes.ResearchProcess;
import ru.greenpix.civilization.technologies.Technology;
import ru.greenpix.developer.utils.items.Item;

public class CommandAdmin extends AbstractCommand {

	{
		addArgumentType(Technology.class, a -> Technology.getByName(a), "Технологии '%value%' не существует в игре!");
	}
	
	private GuiseRecipes guise = new GuiseRecipes(false);
	
	@Override
	public void showHelp(CommandSender sender, String label) {
		super.showTitle(sender, "&b&lКоманды Разработчика");
		super.showHelp(sender, label);
	}
	
	@Override
	public void onCommand(CommandSender sender, String label, String[] args) {
		showHelp(sender, label);
	}
	
	@BukkitPermissions("civcraft.admin")
	@SubCommand(aliases = {}, desc = "")
	public void items(Player sender) {
		guise.open((Player) sender);
	}
	
	@BukkitPermissions("civcraft.admin")
	@SubCommand(aliases = {}, desc = "")
	public void item(Player sender,
			@Argument("item") String item) {
		Item i = CustomItems.getItem(item);
		if(item == null) {
			sender.sendMessage("Item not found!");
			return;
		}
		sender.getInventory().addItem(i.getCopy());
	}
	
	@BukkitPermissions("civcraft.admin")
	@SubCommand(aliases = {}, desc = "")
	public void givetech(CommandSender sender,
			@Argument("civ") Civilization civ,
			@Argument("tech") Technology tech) {
		if(civ.hasTechnology(tech)) return;
		new ResearchProcess(civ, tech).onComplete();
	}
	
	/*
	@BukkitPermissions("civcraft.admin")
	@SubCommand(aliases = {}, desc = "")
	public void querySQL(CommandSender sender,
			@Argument("query") @BlindArgument String query) {
		try {
			Tables.getDatabase().getConnection().querySQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@BukkitPermissions("civcraft.admin")
	@SubCommand(aliases = {}, desc = "")
	public void updateSQL(CommandSender sender,
			@Argument("query") @BlindArgument String query) {
		try {
			Tables.getDatabase().getConnection().querySQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@BukkitPermissions("civcraft.admin")
	@SubCommand(aliases = {}, desc = "")
	public void dumpSQL(CommandSender sender,
			@Argument("dumpname") String database) {
		final long ms = System.currentTimeMillis();
		sender.sendMessage("This may take a few minutes... Please, wait...");
		RunnableManager.async(() -> {
			try {
				MysqlDatabase db = new MysqlDatabase(Tables.getDatabase().getUser(), database);
				db.connect();
				for(MysqlTable t : Tables.getDatabase().getTables()) {
					db.getConnection().updateSQL("DROP TABLE IF EXISTS `" + t.getName() + "`;");
					db.getConnection().updateSQL("CREATE TABLE " + db.getDatabase() + "." + t.getName() + " SELECT * FROM " + Tables.getDatabase().getDatabase() + "." + t.getName() + ";");
					sender.sendMessage("-> " + t.getName() + " dumped.");
				}
				db.close();
				sender.sendMessage("Dumped for " + (System.currentTimeMillis() - ms) + " ms.");
			} catch (SQLException e) {
				e.printStackTrace();
				sender.sendMessage("An occured exception, check logs.");
			}
		});
	}
	
	@BukkitPermissions("civcraft.admin")
	@SubCommand(aliases = {}, desc = "")
	public void restoreSQL(CommandSender sender,
			@Argument("dumpname") String database) {
		final long ms = System.currentTimeMillis();
		sender.sendMessage("This may take a few minutes... Please, wait...");
		RunnableManager.async(() -> {
			try {
				MysqlDatabase db = new MysqlDatabase(Tables.getDatabase().getUser(), database);
				db.connect();
				for(MysqlTable t : Tables.getDatabase().getTables()) {
					Tables.getDatabase().getConnection().updateSQL("DELETE FROM `" + t.getName() + "`;");
					db.getConnection().updateSQL("INSERT INTO " + Tables.getDatabase().getDatabase() + "." + t.getName() + " SELECT * FROM " + db.getDatabase() + "." + t.getName() + ";");
					sender.sendMessage("-> " + t.getName() + " restored.");
				}
				db.close();
				sender.sendMessage("Restored for " + (System.currentTimeMillis() - ms) + " ms.");
			} catch (SQLException e) {
				e.printStackTrace();
				sender.sendMessage("An occured exception, check logs.");
			}
		});
	}
	*/
}
