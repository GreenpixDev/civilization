package ru.greenpix.civilization.diplomacy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.utils.RunnableManager;

public class Diplomacy {

	private static final List<Relationship> global = Collections.synchronizedList(new ArrayList<>());

	public static List<Relationship> getGlobal() {
		return global;
	}
	
	public static List<Relationship> getGlobal(Status status) {
		return global.stream().filter(e -> e.getStatus().equals(status)).collect(Collectors.toList());
	}
	
	public static List<Relationship> getRelations(Civilization civ) {
		return global.stream().filter(e -> e.getReceiver().equals(civ) || e.getSender().equals(civ)).collect(Collectors.toList());
	}
	
	public static List<Relationship> getRelations(Civilization civ, Status status) {
		return global.stream().filter(e -> (e.getReceiver().equals(civ) || e.getSender().equals(civ)) && e.getStatus().equals(status)).collect(Collectors.toList());
	}
	
	public static List<Relationship> getSenderRelations(Civilization sender) {
		return global.stream().filter(e -> e.getSender().equals(sender)).collect(Collectors.toList());
	}
	
	public static List<Relationship> getSenderRelations(Civilization sender, Status status) {
		return global.stream().filter(e -> e.getSender().equals(sender) && e.getStatus().equals(status)).collect(Collectors.toList());
	}
	
	public static List<Relationship> getReceiverRelations(Civilization receiver) {
		return global.stream().filter(e -> e.getSender().equals(receiver)).collect(Collectors.toList());
	}
	
	public static List<Relationship> getReceiverRelations(Civilization receiver, Status status) {
		return global.stream().filter(e -> e.getSender().equals(receiver) && e.getStatus().equals(status)).collect(Collectors.toList());
	}
	
	public static Relationship getRelationship(Civilization civ1, Civilization civ2) {
		return global.stream().filter(e -> 
				(e.getReceiver().equals(civ1) || e.getReceiver().equals(civ2)) && 
				(e.getSender().equals(civ1) || e.getSender().equals(civ2))
		).findFirst().orElse(new Relationship(Status.NEUTRAL, civ1, civ2, "default"));
	}
	
	public static Status getStatus(Civilization civ1, Civilization civ2) {
		Relationship r = getRelationship(civ1, civ2);
		return r == null ? Status.NEUTRAL : r.getStatus();
	}
	
	public static boolean add(Relationship r) {
		r.getReceiver().getGroupOfMembers().getOnline().forEach(p -> p.asyncUpdateTag());
		r.getSender().getGroupOfMembers().getOnline().forEach(p -> p.asyncUpdateTag());
		Relationship now = getRelationship(r.getReceiver(), r.getSender());
		if(now == null) {
			global.add(r);
			RunnableManager.async(() -> r.save());
			return true;
		} else if(now.getStatus() == r.getStatus()) {
			return false;
		} else {
			global.remove(now);
			global.add(r);
			RunnableManager.async(() -> r.save());
			return true;
		}
	}
}
