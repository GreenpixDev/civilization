package ru.greenpix.civilization.objects;

import java.util.List;
import java.util.stream.Collectors;

import ru.greenpix.civilization.player.Request;

public interface Requester extends Named {

	public void broadcast(String msg, String... perms);
	
	public List<Request> getRequests();
	
	default List<Request> getRequests(Requester sender) {
		return getRequests().stream().filter(r -> r.getSender().equals(sender)).collect(Collectors.toList());
	}
	
	default Request getRequest(Requester sender) {
		return getRequests().stream().filter(r -> r.getSender().equals(sender)).findFirst().orElse(null);
	}
	
	default List<Request> getRequests(String sender) {
		return getRequests().stream().filter(r -> r.getSender().getName().equalsIgnoreCase(sender)).collect(Collectors.toList());
	}
	
	default Request getRequest(String sender) {
		return getRequests().stream().filter(r -> r.getSender().getName().equalsIgnoreCase(sender)).findFirst().orElse(null);
	}
	
}
