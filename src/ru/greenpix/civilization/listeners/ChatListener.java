package ru.greenpix.civilization.listeners;

import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import ru.greenpix.civilization.diplomacy.Diplomacy;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.utils.RunnableManager;
import ru.greenpix.developer.Utils;

public class ChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(e.isCancelled()) return;
		CivPlayer player = CivPlayer.wrapSafely(e.getPlayer());
		if(player == null) return;
		if(player.getChatExecutor() != null) {
			String message = e.getMessage();
			RunnableManager.sync(() -> {
				if(player.getChatExecutor() != null) {
					player.getChatExecutor().execute(player, message);
				}
			});
			e.setCancelled(true);
			return;
		}
		Civilization civ = player.getCivilization();
		Town town = player.getTown();
		boolean gl = e.getFormat().startsWith("!");
		if(civ != null) {
			for(CivPlayer p : e.getRecipients().stream().map(a -> CivPlayer.getByPlayer(a)).collect(Collectors.toList())) {
				ChatColor color = ChatColor.WHITE;
				if(p.getCivilization() != null) {
					color = Diplomacy.getRelationship(civ, p.getCivilization()).getStatus().color;
				}
				String msg = (gl ? "&6Ⓖ " : "&7Ⓛ ") + civ.getDisplayTag() + " " + color + player.getName() + (town == null ? "" : " &7(" + town.getName() + ")");
				String msg2 = " &f-> " + e.getMessage();
				send(p, player, msg, msg2);
			}
		} else {
			String msg = (gl ? "&6Ⓖ " : "&7Ⓛ ") + "&7Бродяга &f" + player.getName();
			String msg2 = " &f-> " + e.getMessage();
			e.getRecipients().stream()
				.map(a -> CivPlayer.getByPlayer(a))
				.collect(Collectors.toList())
				.forEach(p -> send(p, player, msg, msg2));
		}
		e.setCancelled(true);
		return;
	}
	
	public void send(CivPlayer player, CivPlayer p, String msg, String msg2) {
		TextComponent f = new TextComponent();
		TextComponent text = new TextComponent(Utils.color(msg));
		if(p.hasHome()) {
			String info = 
					"&6Цивилизация: &e" + p.getCivilization().getName() + "\n" +
					"&6Город: &e" + p.getTown().getName();
			text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.color(info)).create()));
			text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + p.getName() + " "));
		}
		f.addExtra(text);
		for(BaseComponent c : TextComponent.fromLegacyText(Utils.color(msg2))) {
			f.addExtra(c);
		}
		player.toBukkit().spigot().sendMessage(f);
	}
}
