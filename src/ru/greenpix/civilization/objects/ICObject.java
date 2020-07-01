package ru.greenpix.civilization.objects;

import java.util.Date;

import ru.greenpix.civilization.database.Stored;

public interface ICObject extends Stored, Countable, Groupable, Requester, Economical, Named {
	
	Date getTimestamp();
	
	String getOwner();
	
	void setName(String name);
	
	void setOwner(String owner);
}
