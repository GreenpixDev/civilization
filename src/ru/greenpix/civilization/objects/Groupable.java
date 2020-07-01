package ru.greenpix.civilization.objects;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ru.greenpix.civilization.groups.Group;
import ru.greenpix.civilization.player.CivPlayer;

public interface Groupable {

	public int getId();
	
	public List<Group> getGroups();
	
	default Set<CivPlayer> getPlayersWithPerm(String permission) {
		Set<CivPlayer> set = new HashSet<CivPlayer>();
		getGroupsWithPerm(permission).forEach(g -> set.addAll(g.getOnline()));
		return set;
	}
	
	default Set<CivPlayer> getPlayersWithPerms(Collection<String> permissions) {
		Set<CivPlayer> set = new HashSet<CivPlayer>();
		getGroupsWithPerms(permissions).forEach(g -> set.addAll(g.getOnline()));
		return set;
	}
	
	default Group getGroupById(int id) {
		return getGroups().stream().filter(g -> g.getId() == id).findFirst().orElse(null);
	}
	
	default Group getGroupByName(String name) {
		return getGroups().stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	default List<Group> getGroupsWithPerm(String permission) {
		return getGroups().stream().filter(g -> g.hasPermission(permission)).collect(Collectors.toList());
	}
	
	default List<Group> getGroupsWithPerms(Collection<String> permissions) {
		return getGroups().stream().filter(g -> g.hasPermission(permissions)).collect(Collectors.toList());
	}
	
	default Group getGroupOfMembers() {
		return getGroups().stream().filter(g -> g.getName().equalsIgnoreCase(Group.GROUP_OF_MEMBERS)).findFirst().orElseGet(() -> new Group(this, Group.GROUP_OF_MEMBERS));
	}
	
	default Group getGroupOfLeaders() {
		return getGroups().stream().filter(g -> g.getName().equalsIgnoreCase(Group.GROUP_OF_LEADERS)).findFirst().orElseGet(() -> new Group(this, Group.GROUP_OF_LEADERS));
	}
}
