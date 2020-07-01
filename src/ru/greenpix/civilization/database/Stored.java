package ru.greenpix.civilization.database;

import java.util.Collection;

import ru.greenpix.mysql.api.Result;
import ru.greenpix.mysql.api.ResultList;
import ru.greenpix.mysql.elements.MysqlTable;
import ru.greenpix.mysql.nbt.MysqlFields;
import ru.greenpix.mysql.nbt.Where;
import ru.greenpix.mysql.nbt.WhereList;

public interface Stored {

	public int getId();
	
	public MysqlTable getSqlTable();
	
	public MysqlFields getSqlRecord();
	
	default void writeSql() {
		getSqlTable().set(getSqlRecord(), getSqlWhere());
	}
	
	default void deleteSql() {
		getSqlTable().set(null, getSqlWhere());
	}
	
	default WhereList getSqlWhere() {
		return new WhereList(new Where("id", getId()));
	}
	
	default int lastId() {
		Result result = getSqlTable().getMax("id");
		return result == null ? 0 : result.getInt("id");
	}
	
	public static <T extends Stored> T findById(int id, Collection<T> c, MysqlTable table, ResultWrapper<T> wrapper) {
		return c.stream().filter(e -> e.getId() == id).findFirst()
				.orElseGet(() -> {
					ResultList list = table.get(new WhereList(new Where("id", id)));
					if(list == null || list.size() == 0) return null;
					else return wrapper.wrap(list.first());
				});
	}
	
	public interface ResultWrapper<T> {
		
		public T wrap(Result result);
		
	}
}
