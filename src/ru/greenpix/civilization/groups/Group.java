package ru.greenpix.civilization.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import ru.greenpix.civilization.database.Stored;
import ru.greenpix.civilization.database.Tables;
import ru.greenpix.civilization.database.Tables.TableGroupMembers;
import ru.greenpix.civilization.database.Tables.TableGroupPermissions;
import ru.greenpix.civilization.database.Tables.TableGroups;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.objects.Groupable;
import ru.greenpix.civilization.objects.Named;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.utils.RunnableManager;
import ru.greenpix.mysql.api.Result;
import ru.greenpix.mysql.api.ResultList;
import ru.greenpix.mysql.elements.MysqlTable;
import ru.greenpix.mysql.nbt.MysqlFields;
import ru.greenpix.mysql.nbt.Where;
import ru.greenpix.mysql.nbt.WhereList;

@SuppressWarnings("serial")
public class Group extends ArrayList<String> implements Stored, Named {
	
	public final static String GROUP_OF_MEMBERS = "members";
	
	public final static String GROUP_OF_LEADERS = "leaders";
	
	public final static String GROUP_OF_CRIMINAL = "criminal";
	
	private final List<String> permissions = new ArrayList<String>();

	private final Groupable parent;
	
	private final int id;
	
	private String name;
	
	public Group(Groupable parent, String name) {
		this.id = lastId() + 1;
		this.parent = parent;
		this.name = name;
		writeSql();
		this.parent.getGroups().add(this);
		defaultPermissions();
	}
	
	public Group(Result result) {
		this.id = result.getInt(TableGroups.ID);
		this.name = result.getString(TableGroups.GROUP_NAME);
		switch (result.getByte(TableGroups.TYPE)) {
		case 0:
			this.parent = Civilization.getById(result.getInt(TableGroups.PARENT));
			break;
		case 1:
			this.parent = Town.getById(result.getInt(TableGroups.PARENT));
			break;
		default:
			throw new IllegalArgumentException("Invalid 'type' value");
		}
		this.parent.getGroups().add(this);
		defaultPermissions();
	}
	
	public void defaultPermissions() {
		switch (name) {
		case GROUP_OF_MEMBERS:
			if(parent instanceof Town) {
				getPermissions().add(Perms.BUILDINGS_BUILD_ALL);
				getPermissions().add(Perms.BUILDINGS_DESTROY);
				getPermissions().add(Perms.UNITS_CREATE);
			}
			break;
		case GROUP_OF_LEADERS:
			getPermissions().add("*");
			break;
		default:
			break;
		}
	}
	
	public boolean isDefault() {
		return name.equalsIgnoreCase(GROUP_OF_MEMBERS) || name.equalsIgnoreCase(GROUP_OF_LEADERS) || name.equalsIgnoreCase(GROUP_OF_CRIMINAL);
	}
	
	public List<String> getPermissions() {
		return permissions;
	}
	
	public List<CivPlayer> getOnline() {
		return stream().map(p -> CivPlayer.getByName(p)).filter(p -> p != null).collect(Collectors.toList());
	}
	
	public int online() {
		return getOnline().size();
	}
	
	@Override
	public boolean contains(Object o) {
		String name;
		if(o instanceof CivPlayer) {
			name = ((CivPlayer) o).getName();
		} else {
			name = o.toString();
		}
		return super.contains(name);
	}
	
	public boolean containsIgnoreCase(String o) {
		for(String str : this) if(str.equalsIgnoreCase(o)) return true;
		return false;
	}
	
	@Override
	public boolean add(String member) {
		RunnableManager.async(() -> addSqlMember(member));
		return super.add(member);
	}
	
	@Override
	public boolean remove(Object member) {
		String name;		
		if(member instanceof String) {
			name = (String) member;
		} else if(member instanceof CivPlayer) {
			name = ((CivPlayer) member).getName();
		} else if(member instanceof Player) {
			name = ((Player) member).getName();
		} else return false;
		RunnableManager.async(() -> removeSqlMember(name));
		return super.remove(name);
	}
	
	public boolean add(CivPlayer member) {
		return this.add(member.getName());
	}
	
	public boolean add(Player member) {
		return this.add(member.getName());
	}
	
	public boolean hasPermission(Collection<String> perms) {
		return perms.stream().anyMatch(p -> hasPermission(p));
	}
	
	public boolean hasPermission(String perm) {
		perm = perm.toLowerCase();
		for(String p : getPermissions()) {
			p = p.toLowerCase();
			if(p.equals("*")) return true;
			if(p.endsWith("*")) {
				if(p.replace(".*", "").startsWith(perm)) return true;
			}
			else if(p.equals(perm)) return true;
		}
		return false;
	}
	
	public boolean addPermission(String perm) {
		RunnableManager.async(() -> addSqlPermission(perm));
		return getPermissions().add(perm);
	}
	
	public boolean removePermission(String perm) {
		RunnableManager.async(() -> removeSqlPermission(perm));
		return getPermissions().remove(perm);
	}
	
	private void addSqlPermission(String perm) {
		MysqlFields fields = new MysqlFields()
				.put(TableGroupPermissions.GROUP, getId())
				.put(TableGroupPermissions.PERMISSION, perm);
		MysqlTable table = Tables.getTable(TableGroupPermissions.class);
		table.add(fields);
	}
	
	private void addSqlMember(String member) {
		MysqlFields fields = new MysqlFields()
				.put(TableGroupMembers.GROUP, getId())
				.put(TableGroupMembers.MEMBER, member);
		MysqlTable table = Tables.getTable(TableGroupMembers.class);
		table.add(fields);
	}
	
	private void removeSqlPermission(String perm) {
		WhereList where = new WhereList(
				new Where(TableGroupPermissions.GROUP, getId()).fix(),
				new Where(TableGroupPermissions.PERMISSION, perm));
		MysqlTable table = Tables.getTable(TableGroupPermissions.class);
		table.set(null, where);
	}
	
	private void removeSqlMember(String member) {
		WhereList where = new WhereList(
				new Where(TableGroupMembers.GROUP, getId()).fix(),
				new Where(TableGroupMembers.MEMBER, member));
		MysqlTable table = Tables.getTable(TableGroupMembers.class);
		table.set(null, where);
	}
	
	public Groupable getParent() {
		return parent;
	}
	
	public Group load() {
		{
			WhereList where = new WhereList(new Where(TableGroupMembers.GROUP, getId()).fix());
			ResultList list = Tables.getTable(TableGroupMembers.class).get(where);
			if(list != null) {
				list.forEach(res -> super.add(res.getString(TableGroupMembers.MEMBER)));
			}
		}
		{
			WhereList where = new WhereList(new Where(TableGroupPermissions.GROUP, getId()).fix());
			ResultList list = Tables.getTable(TableGroupPermissions.class).get(where);
			if(list != null) {
				list.forEach(res -> getPermissions().add(res.getString(TableGroupPermissions.PERMISSION)));
			}
		}
		return this;
	}
	
	@Override
	public final int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public MysqlTable getSqlTable() {
		return Tables.getTable(TableGroups.class);
	}

	@Override
	public MysqlFields getSqlRecord() {
		return new MysqlFields()
				.put(TableGroups.ID, getId())
				.put(TableGroups.GROUP_NAME, getName())
				.put(TableGroups.TYPE, getParent() instanceof Civilization ? 0 : 1)
				.put(TableGroups.PARENT, getParent().getId());
	}
	
	@Override
	public void deleteSql() {
		getSqlTable().set(null, new WhereList(new Where("id", getId())));
		Tables.getTable(TableGroupMembers.class).set(null, new WhereList(new Where(TableGroupMembers.GROUP, getId()).fix()));
		Tables.getTable(TableGroupPermissions.class).set(null, new WhereList(new Where(TableGroupPermissions.GROUP, getId()).fix()));
	}
}
