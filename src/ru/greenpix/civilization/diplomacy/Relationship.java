package ru.greenpix.civilization.diplomacy;

import java.util.Date;

import ru.greenpix.civilization.database.Tables;
import ru.greenpix.civilization.database.Tables.TableDiplomacy;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.mysql.api.Result;
import ru.greenpix.mysql.elements.MysqlTable;
import ru.greenpix.mysql.nbt.MysqlFields;
import ru.greenpix.mysql.nbt.Where;
import ru.greenpix.mysql.nbt.WhereList;

public class Relationship {

	private final Civilization receiver;
	
	private final Civilization sender;
	
	private final String comment;
	
	private final Status status;
	
	private final Date timestamp;
	
	public Relationship(Result result) {
		this.status = Status.getById(result.getByte(TableDiplomacy.STATUS));
		this.receiver = Civilization.getById(result.getInt(TableDiplomacy.RECEIVER));
		this.sender = Civilization.getById(result.getInt(TableDiplomacy.SENDER));
		this.comment = result.getString(TableDiplomacy.REASON);
		this.timestamp = result.getDate(TableDiplomacy.TIME);
	}
	
	public Relationship(Status status, Civilization receiver, Civilization sender, String comment) {
		this.status = status;
		this.receiver = receiver;
		this.sender = sender;
		this.comment = comment.replace("'", "\"");
		this.timestamp = new Date();
	}

	public Status getStatus() {
		return status;
	}

	public Civilization getReceiver() {
		return receiver;
	}

	public Civilization getSender() {
		return sender;
	}

	public String getComment() {
		return comment;
	}

	public Date getTimestamp() {
		return timestamp;
	}
	
	public boolean isWar() {
		return getStatus() == Status.WAR;
	}
	
	public boolean isHostile() {
		return getStatus() == Status.HOSTILE;
	}
	
	public boolean isNeutral() {
		return getStatus() == Status.NEUTRAL;
	}
	
	public boolean isPeace() {
		return getStatus() == Status.PEACE;
	}
	
	public boolean isAlly() {
		return getStatus() == Status.ALLY;
	}
	
	public boolean isAggressive() {
		return isWar() || isHostile();
	}
	
	public void save() {
		MysqlTable table = Tables.getTable(TableDiplomacy.class);
		WhereList where = new WhereList(
				new Where(TableDiplomacy.SENDER, getReceiver().getId()),
				new Where(TableDiplomacy.RECEIVER, getSender().getId())
		);
		table.set(null, where);
		where = new WhereList(
				new Where(TableDiplomacy.SENDER, getSender().getId()),
				new Where(TableDiplomacy.RECEIVER, getReceiver().getId())
		);
		MysqlFields fields = new MysqlFields()
				.put(TableDiplomacy.SENDER, getSender().getId())
				.put(TableDiplomacy.RECEIVER, getReceiver().getId())
				.put(TableDiplomacy.STATUS, getStatus().id)
				.put(TableDiplomacy.REASON, getComment())
				.put(TableDiplomacy.TIME, getTimestamp());
		table.set(fields, where);
	}
}
