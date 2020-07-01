package ru.greenpix.civilization.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.objects.Groupable;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.developer.Utils;

public abstract class AbstractCommand implements CommandExecutor {

	public static final String ONLY_FOR_PLAYERS = "§cOnly for players!";
	
	private final Map<Class<?>, Getter<?>> types = new HashMap<>();
	
	private final Map<Class<?>, String> messages = new HashMap<>();
	
	private final String parent;
	
	{
		addArgumentType(Player.class, a -> Bukkit.getPlayer(a), "Игрок '%value%' не найден на сервере!");
		addArgumentType(CivPlayer.class, a -> CivPlayer.getByName(a), "Игрок '%value%' не найден на сервере!");
		addArgumentType(Town.class, a -> Town.getByName(a), "Города '%value%' не существует!");
		addArgumentType(Civilization.class, a -> Civilization.getByName(a), "Цивилизации '%value%' не существует!");
	}
	
	public AbstractCommand() {
		this.parent = "";
	}
	
	public AbstractCommand(String parent) {
		this.parent = parent + " ";
	}
	
	public AbstractCommand(AbstractCommand cmd, String parent) {
		this.parent = cmd.parent + parent + " ";
	}
	
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			if(check2(sender)) onCommand(sender, label, args);
			return true;
		} else if (args[0].equalsIgnoreCase("help") || args[0].equals("?")) {
			showHelp(sender, label);
			return true;
		} else {
			for(Method method : getClass().getMethods()) {
				SubCommand sub = method.getAnnotation(SubCommand.class);
				if(sub == null) continue;
				if(sub.aliases().length > 0) {
					for(String aliase : sub.aliases()) {
						if(args[0].equalsIgnoreCase(aliase)) {
							executeSub(method, sub, sender, label, args);
							return true;
						}
					}
				} 
				if(args[0].equalsIgnoreCase(method.getName())) {
					executeSub(method, sub, sender, label, args);
					return true;
				}
			}
		}
		if(check2(sender)) onCommand(sender, label, args);
		return true;
	}
	
	private boolean check2(CommandSender sender) {
		try {
			return check(getClass().getMethod("onCommand", CommandSender.class, String.class, String[].class), sender);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean check(Method method, CommandSender sender) {
		MustHaveHome home = method.getAnnotation(MustHaveHome.class);
		if(home != null) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ONLY_FOR_PLAYERS);
				return false;
			}
			CivPlayer player = CivPlayer.wrap(sender);
			if(home.value() == Town.class) {
				if(player.getTown() == null) {
					sender.sendMessage("§c" + home.msg().replace("%home%", "городе"));
					return false;
				}
			} else if(home.value() == Civilization.class) {
				if(player.getCivilization() == null) {
					sender.sendMessage("§c" + home.msg().replace("%home%", "цивилизации"));
					return false;
				}
			}
		}
		BukkitPermissions bPerm = method.getAnnotation(BukkitPermissions.class);
		if(bPerm != null) {
			if(!Stream.of(bPerm.value()).allMatch(sender::hasPermission)) {
				sender.sendMessage(bPerm.msg());
				return false;
			}
		}
		GroupPermissions gPerm = method.getAnnotation(GroupPermissions.class);
		if(gPerm != null) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ONLY_FOR_PLAYERS);
				return false;
			}
			CivPlayer cp = CivPlayer.wrap(sender);
			Groupable g = cp.getHome();
			String hh = "дома";
			if(home != null) {
				if(home.value() == Town.class) {
					g = cp.getTown();
					hh = "города";
				}
				else if(home.value() == Civilization.class) {
					g = cp.getCivilization();
					hh = "цивилизации";
				}
			}
			final Groupable fg = g;
			if((!cp.hasHome()) || (!Stream.of(gPerm.value()).allMatch(e -> cp.hasPermission(fg, e)))) {
				sender.sendMessage("§cУ вас нет доступа к этой команде! Обратитесь к создателю " + hh + ".");
				return false;
			}
		}
		return true;
	}
	
	public void executeSub(Method method, SubCommand sub, CommandSender sender, String label, String[] args) {
		if(check(method, sender)) onSubCommand(method, sub, sender, label, args);
		return;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return execute(sender, label, args);
	}
	
	public abstract void onCommand(CommandSender sender, String label, String[] args);
	
	public void showHelp(CommandSender sender, String label) {
		for(Method method : getClass().getMethods()) {
			SubCommand sub = method.getAnnotation(SubCommand.class);
			if(sub == null) continue;
			sender.sendMessage("§e/" + parent + label + " " + method.getName() + " " + getArgsLabel(method, sender, label) + "§7- " + sub.desc());
		}
	}
	
	public String getArgsLabel(Method method, CommandSender sender, String label) {
		Argument arg;
		StringBuilder b = new StringBuilder();
		String name;
		for(int i = 1; i < method.getParameters().length; i++) {
			arg = method.getParameters()[i].getAnnotation(Argument.class);
			if(arg == null) {
				name = method.getParameters()[i].getName();
				b.append("§2(" + name + ") ");
			}
			else {
				name = arg.value();
				if(arg.required()) b.append("§a[" + name + "] ");
				else b.append("§2(" + name + ") ");
			}
		}
		return new String(b);
	}
	
	public void onSubCommand(Method method, SubCommand sub, CommandSender sender, String label, String[] args) {
		Object[] p = new Object[method.getParameterCount()];
		if(method.getParameterTypes()[0] == CommandSender.class) {
			p[0] = sender;
		} else if(method.getParameterTypes()[0] == ColorCommandSender.class) {
			p[0] = new ColorCommandSender(sender);
		} else if(method.getParameterTypes()[0] == Player.class) {
			if(sender instanceof Player) p[0] = (Player) sender;
			else sender.sendMessage(ONLY_FOR_PLAYERS);
		} else if(method.getParameterTypes()[0] == CivPlayer.class) {
			if(sender instanceof Player) {
				p[0] = CivPlayer.wrap(sender);
				if(p[0] == null) return;
			}
			else sender.sendMessage(ONLY_FOR_PLAYERS);
		}
		Class<?> c;
		Parameter param;
		for(int i = 1; i < p.length; i++) {
			param = method.getParameters()[i];
			Argument arg = method.getParameters()[i].getAnnotation(Argument.class);
			String argName = arg == null ? param.getName() : arg.value();
			if(arg != null && !arg.required()) {
				if(args.length - 1 < i) {
					p[i] = null;
					continue;
				}
			} else {
				if(args.length - 1 < i) {
					String argl = getArgsLabel(method, sender, label).replace("§a", "§e").replace("§2", "§7");
					sender.sendMessage("§cНедостаточно аргументов! Используйте: §6/" + parent + label + " " + method.getName() + " " + argl);
					return;
				}
			}
			c = param.getType();
			if (c == String[].class) {
				String[] array = new String[args.length - i];
				for(int a = i; a < args.length; a++) {
					array[a - i] = args[a];
				}
				p[i] = array;
			} else if (c == String.class) {
				if(param.getAnnotation(BlindArgument.class) != null) {
					StringBuilder b = new StringBuilder();
					for(int a = i; a < args.length; a++) {
						b.append(args[a]);
						b.append(" ");
					}
					b.deleteCharAt((b.length() - 1));
					p[i] = new String(b);
				} else {
					p[i] = args[i];
				}
			} else if (c == Byte.class || c == byte.class) {
				try {
					p[i] = Byte.parseByte(args[i]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cИспользуйте число (byte) в аргументе §6[" + argName + "]§c!");
					return;
				}
			} else if (c == Short.class || c == short.class) {
				try {
					p[i] = Short.parseShort(args[i]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cИспользуйте число (short) в аргументе §6[" + argName + "]§c!");
					return;
				}
			} else if (c == Integer.class || c == int.class) {
				try {
					p[i] = Integer.parseInt(args[i]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cИспользуйте число (int) в аргументе §6[" + argName + "]§c!");
					return;
				}
			} else if (c == Long.class || c == long.class) {
				try {
					p[i] = Long.parseLong(args[i]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cИспользуйте число (long) в аргументе §6[" + argName + "]§c!");
					return;
				}
			} else if (c == Float.class || c == float.class) {
				try {
					p[i] = Float.parseFloat(args[i]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cИспользуйте число (float) в аргументе §6[" + argName + "]§c!");
					return;
				}
			} else if (c == Double.class || c == double.class) {
				try {
					p[i] = Double.parseDouble(args[i]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cИспользуйте число (double) в аргументе §6[" + argName + "]§c!");
					return;
				}
			} else if (c == Boolean.class || c == boolean.class) {
				try {
					p[i] = Boolean.parseBoolean(args[i]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cИспользуйте true/false в аргументе §6[" + argName + "]§c!");
					return;
				}
			} else if (c == Character.class || c == char.class) {
				if (args[i].length() == 1) {
					p[i] = args[i].charAt(0);
				} else {
					sender.sendMessage("§cИспользуйте 1 символ в аргументе §6[" + argName + "]§c!");
					return;
				}
			} else {
				Getter<?> getter = types.get(c);
				if(getter == null) {
					sender.sendMessage("§cПроизошла критическая ошибка, обратитесь к администраторам! Проблема: ");
					sender.sendMessage("§cjava.lang.NullPointerException: argument[" + i + "] " + c.getCanonicalName() + " not found");
					return;
				}
				Object o = getter.get(args[i]);
				if(o != null) {
					p[i] = o;
				} else {
					sender.sendMessage("§c" + messages.getOrDefault(c, "Неверное значение аргумента: %arg%").replace("%value%", args[i]).replace("%arg%", argName));
					return;
				}
			}
		}
		try {
			method.invoke(this, p);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void showTitle(CommandSender sender, String title) {
		int length = (60 - title.length()) / 2;
		StringBuilder builder = new StringBuilder();
		builder.append("&7");
		for(int i = 0; i < length; i += 2) builder.append("-=");
		builder.append(" (");
		builder.append(title);
		builder.append("&7) ");
		for(int i = 0; i < length; i += 2) builder.append("=-");
		sender.sendMessage("");
		sender.sendMessage(Utils.color(new String(builder)));
	}
	
	public <T> void addArgumentType(Class<T> clazz, Getter<T> getter) {
		types.put(clazz, getter);
	}
	
	public <T> void addArgumentType(Class<T> clazz, Getter<T> getter, String msg) {
		types.put(clazz, getter);
		messages.put(clazz, msg);
	}
	
	public static interface Getter<T> {
		
		T get(String arg);
		
	}
}
