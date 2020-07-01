package ru.greenpix.civilization.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.objects.Requester;

public class Request {

	private static final List<Request> last = new ArrayList<>();
	
	private final Runnable accept;
	
	private final String message;
	
	private final String[] perms;
	
	private Requester sender;
	
	private Requester receiver;
	
	private boolean closed = false;
	
	public Request(String message, Runnable accept, String... perms) {
		this.accept = accept;
		this.message = message;
		this.perms = perms;
	}
	
	public String[] getPermissions() {
		return perms;
	}
	
	public Requester getSender() {
		return sender;
	}
	
	public Requester getReceiver() {
		return receiver;
	}
	
	public Request send(Requester sender, Requester receiver) {
		last.add(this);
		(this.sender = sender).broadcast("&7Запрос отправлен. Ожидайте ответа в течении 2 минут.", perms);
		(this.receiver = receiver).broadcast(message, perms);
		this.receiver.broadcast("&7Чтобы принять, используйте /accept " + sender.getName() + ".", perms);
		this.receiver.broadcast("&7Чтобы отклонить, используйте /deny " + sender.getName() + ".", perms);
		receiver.getRequests().add(this);
		Bukkit.getScheduler().runTaskLater(CivCore.getInstance(), () -> {
			if(!closed) {
				receiver.broadcast("&7Запрос от " + sender.getName() + " больше не актуален.", perms);
				sender.broadcast("&7" + receiver.getName() + " не ответил на ваш запрос за 2 минуты.", perms);
				receiver.getRequests().remove(this);
				closed = true;
			}
			if(last.contains(this)) last.remove(this);
		}, 2400L);
		return this;
	}
	
	public void accept() {
		if(last.contains(this)) last.remove(this);
		receiver.broadcast("&7Запрос от " + sender.getName() + " был принят.", perms);
		sender.broadcast("&2" + receiver.getName() + "&a принял(а) ваш запрос.", perms);
		accept.run();
		receiver.getRequests().remove(this);
		closed = true;
	}
	
	public void deny() {
		receiver.broadcast("&7Запрос от " + sender.getName() + " был отклонен.", perms);
		sender.broadcast("&6" + receiver.getName() + "&e отклонил(а) ваш запрос.", perms);
		receiver.getRequests().remove(this);
		closed = true;
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public boolean call(CivPlayer player, Requester sender, Requester receiver) {
		return call(this, player, sender, receiver);
	}
	
	public static boolean call(Request request, CivPlayer player, Requester sender, Requester receiver) {
		if(receiver.getRequest(sender) != null) {
			player.sendMessage("&cВы уже отправили запрос.");
			return false;
		}
		if(last.stream().anyMatch(e -> e.getSender().equals(sender) && e.getReceiver().equals(receiver))) {
			player.sendMessage("&cВы недавно отправляли запрос, который " + receiver.getName() + " отклонил.");
			return false;
		}
		request.send(sender, receiver);
		return true;
	}
}
