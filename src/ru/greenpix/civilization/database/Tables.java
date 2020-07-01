package ru.greenpix.civilization.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.greenpix.mysql.api.MysqlAPI;
import ru.greenpix.mysql.elements.MysqlDatabase;
import ru.greenpix.mysql.elements.MysqlTable;
import ru.greenpix.mysql.nbt.BooleanColumn;
import ru.greenpix.mysql.nbt.ByteColumn;
import ru.greenpix.mysql.nbt.Column;
import ru.greenpix.mysql.nbt.DecimalColumn;
import ru.greenpix.mysql.nbt.DoubleColumn;
import ru.greenpix.mysql.nbt.FloatColumn;
import ru.greenpix.mysql.nbt.IntColumn;
import ru.greenpix.mysql.nbt.KeyColumn;
import ru.greenpix.mysql.nbt.LongColumn;
import ru.greenpix.mysql.nbt.MediumIntColumn;
import ru.greenpix.mysql.nbt.ShortColumn;
import ru.greenpix.mysql.nbt.StringColumn;
import ru.greenpix.mysql.nbt.TimestampColumn;

public class Tables {
	
	public static MysqlDatabase database;
	
	@TableMeta("towns")
	public static class TableTowns {
		
		@ColumnType(KeyColumn.class)
		public static final String ID = "id";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(16)
		public static final String TOWN_NAME = "name";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(16)
		public static final String CREATOR = "creator";
		
		@ColumnType(IntColumn.class)
		public static final String MOTHER_CIVILIZATION = "mother";
		
		@ColumnType(IntColumn.class)
		public static final String CURRENT_CIVILIZATION = "civilization";
		
		@ColumnType(IntColumn.class)
		public static final String TOWNHALL = "townhall";
		
		@ColumnType(DoubleColumn.class)
		public static final String BALANCE = "balance";
		
		@ColumnType(TimestampColumn.class)
		public static final String TIME_CREATION = "created";
		
		@ColumnType(DecimalColumn.class)
		@ColumnMeta({5,2})
		public static final String BUILDING_PROGRESS = "progress";
		
		@ColumnType(IntColumn.class)
		public static final String BUILDING_TASK = "task";
		
	}
	
	@TableMeta("civilizations")
	public static class TableCivilizations {
		
		@ColumnType(KeyColumn.class)
		public static final String ID = "id";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(16)
		public static final String CIVILIZATION_NAME = "name";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(16)
		public static final String CREATOR = "creator";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(4)
		public static final String CIVILIZATION_TAG = "tag";
		
		@ColumnType(IntColumn.class)
		public static final String CAPITAL = "capital";
		
		@ColumnType(DoubleColumn.class)
		public static final String BALANCE = "balance";
		
		@ColumnType(TimestampColumn.class)
		public static final String TIME_CREATION = "created";
		
		@ColumnType(DecimalColumn.class)
		@ColumnMeta({5,2})
		public static final String TECHNOLOGY_PROGRESS = "progress";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(32)
		public static final String TECHNOLOGY_TASK = "task";
		
	}
	
	@TableMeta("buildings")
	public static class TableBuildings {
		
		@ColumnType(KeyColumn.class)
		public static final String ID = "id";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(32)
		public static final String STRUCTURE = "structure";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(16)
		public static final String STYLE = "style";
		
		@ColumnType(IntColumn.class)
		public static final String TOWN = "town";
		
		@ColumnType(IntColumn.class)
		public static final String LOCATION_X = "x";
		
		@ColumnType(IntColumn.class)
		public static final String LOCATION_Y = "y";
		
		@ColumnType(IntColumn.class)
		public static final String LOCATION_Z = "z";
		
		@ColumnType(ByteColumn.class)
		@ColumnMeta(1)
		public static final String ROTATION = "rotation";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(16)
		public static final String WORLD = "world";
		
		@ColumnType(IntColumn.class)
		public static final String DURABILITY = "durability";
		
		@ColumnType(TimestampColumn.class)
		public static final String TIME_CREATION = "created";
		
	}
	
	@TableMeta("technologies")
	public static class TableTechnologies {
		
		@ColumnType(KeyColumn.class)
		public static final String ID = "id";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(32)
		public static final String TECHNOLOGY = "technology";
		
		@ColumnType(IntColumn.class)
		public static final String CIVILIZATION = "parent";
		
	}
	
	@TableMeta("groups")
	public static class TableGroups {
		
		@ColumnType(KeyColumn.class)
		public static final String ID = "id";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(16)
		public static final String GROUP_NAME = "name";
		
		@ColumnType(ByteColumn.class)
		@ColumnMeta(1)
		public static final String TYPE = "type"; // Цива, город или что-то другое...
		
		@ColumnType(IntColumn.class)
		public static final String PARENT = "parent";
		
	}
	
	@TableMeta("diplomacy")
	public static class TableDiplomacy {
		
		@ColumnType(KeyColumn.class)
		public static final String ID = "id";
		
		@ColumnType(IntColumn.class)
		public static final String SENDER = "sender";
		
		@ColumnType(IntColumn.class)
		public static final String RECEIVER = "receiver";
		
		@ColumnType(ByteColumn.class)
		@ColumnMeta(1)
		public static final String STATUS = "status";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(64)
		public static final String REASON = "reason";
		
		@ColumnType(TimestampColumn.class)
		public static final String TIME = "time";
		
	}
	
	@TableMeta("group_permissions")
	public static class TableGroupPermissions {
		
		@ColumnType(KeyColumn.class)
		public static final String ID = "id";
		
		@ColumnType(IntColumn.class)
		public static final String GROUP = "group";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(64)
		public static final String PERMISSION = "permission";
		
	}
	
	@TableMeta("group_members")
	public static class TableGroupMembers {
		
		@ColumnType(KeyColumn.class)
		public static final String ID = "id";
		
		@ColumnType(IntColumn.class)
		public static final String GROUP = "group";
		
		@ColumnType(StringColumn.class)
		@ColumnMeta(16)
		public static final String MEMBER = "member";
		
	}
	
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface TableMeta {
		
		String value();
		
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ColumnType {
		
		Class<? extends Column> value();
		
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ColumnMeta {
		
		int[] value();
		
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ColumnNotNull {
		
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ColumnDefault {
		
		String value() default "NULL";
		
	}
	
	public static MysqlDatabase getDatabase() {
		return database;
	}
	
	public static MysqlTable createTable(Class<?> clazz) throws SQLException {
		TableMeta meta = getTableMeta(clazz);
		List<Column> columns = new ArrayList<Column>();
		String key = null;
		for(Field field : clazz.getFields()) {
			ColumnType type = field.getAnnotation(ColumnType.class);
			if(type == null) continue;
			String name;
			try {
				name = (String) field.get(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}
			
			if(type.value().isAssignableFrom(KeyColumn.class)) {
				columns.add(new IntColumn(name, IntColumn.NOT_NULL_AUTO_KEY));
				key = name;
			} else {
				columns.add(createColumn(name, type, field.getAnnotation(ColumnMeta.class), field.getAnnotation(ColumnDefault.class), field.getAnnotation(ColumnNotNull.class) != null));
			}
		}
		if(key != null) {
			columns.add(new KeyColumn(key));
		}
		return MysqlAPI.getAPI().connectToTable(getDatabase(), meta.value(), columns.stream().toArray(Column[]::new));
	}
	
	public static Column createColumn(String name, ColumnType type, ColumnMeta meta, ColumnDefault def, boolean notNull) {
		if(type.value() == BooleanColumn.class) {
			BooleanColumn c = new BooleanColumn(name);
			if(notNull) c.info = " " + Column.NOT_NULL + " ";
			if(def != null) c.info = " " + Column.DEFAULT + " " + def.value();
			return c;
		} else if(type.value() == ByteColumn.class) {
			ByteColumn c = new ByteColumn(name, "");
			if(meta != null) c.value = (byte) meta.value()[0];
			if(notNull) c.info = " " + Column.NOT_NULL + " ";
			if(def != null) c.info = " " + Column.DEFAULT + " " + def.value();
			return c;
		} else if(type.value() == DoubleColumn.class) {
			DoubleColumn c = new DoubleColumn(name, "");
			if(meta != null) c.value = (double) meta.value()[0];
			if(notNull) c.info = " " + Column.NOT_NULL + " ";
			if(def != null) c.info = " " + Column.DEFAULT + " " + def.value();
			return c;
		} else if(type.value() == FloatColumn.class) {
			FloatColumn c = new FloatColumn(name, "");
			if(meta != null) c.value = (float) meta.value()[0];
			if(notNull) c.info = " " + Column.NOT_NULL + " ";
			if(def != null) c.info = " " + Column.DEFAULT + " " + def.value();
			return c;
		} else if(type.value() == DecimalColumn.class) {
			DecimalColumn c = new DecimalColumn(name, 0, 0);
			if(meta != null) {
				c.lenght = meta.value()[0];
				c.decimal =  meta.value()[1];
			}
			if(notNull) c.info = " " + Column.NOT_NULL + " ";
			if(def != null) c.info = " " + Column.DEFAULT + " " + def.value();
			return c;
		} else if(type.value() == ShortColumn.class) {
			ShortColumn c = new ShortColumn(name, "");
			if(meta != null) c.value = (short) meta.value()[0];
			if(notNull) c.info = " " + Column.NOT_NULL + " ";
			if(def != null) c.info = " " + Column.DEFAULT + " " + def.value();
			return c;
		} else if(type.value() == MediumIntColumn.class) {
			MediumIntColumn c = new MediumIntColumn(name, "");
			if(meta != null) c.value = meta.value()[0];
			if(notNull) c.info = " " + Column.NOT_NULL + " ";
			if(def != null) c.info = " " + Column.DEFAULT + " " + def.value();
			return c;
		} else if(type.value() == IntColumn.class) {
			IntColumn c = new IntColumn(name, "");
			if(meta != null) c.value = meta.value()[0];
			if(notNull) c.info = " " + Column.NOT_NULL + " ";
			if(def != null) c.info = " " + Column.DEFAULT + " " + def.value();
			return c;
		} else if(type.value() == LongColumn.class) {
			LongColumn c = new LongColumn(name, "");
			if(meta != null) c.value = (long) meta.value()[0];
			if(notNull) c.info = " " + Column.NOT_NULL + " ";
			if(def != null) c.info = " " + Column.DEFAULT + " " + def.value();
			return c;
		} else if(type.value() == StringColumn.class) {
			StringColumn c = new StringColumn(name, "", 256);
			if(meta != null) c.value = meta.value()[0];
			if(notNull) c.info = " " + Column.NOT_NULL;
			if(def != null) c.info = " " + Column.DEFAULT + " " + def.value();
			return c;
		} else if(type.value() == TimestampColumn.class) {
			TimestampColumn c = new TimestampColumn(name, "", 0);
			if(meta != null) c.value = meta.value()[0];
			if(notNull) c.info = " " + Column.NOT_NULL + " ";
			if(def != null) c.info = " " + Column.DEFAULT + " " + def.value();
			return c;
		}
		return null;
	}
	
	public static TableMeta getTableMeta(Class<?> clazz) {
		return clazz.getAnnotation(TableMeta.class);
	}
	
	public static MysqlTable getTable(String name) {
		return getDatabase().getTables().stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public static MysqlTable getTable(Class<?> clazz) {
		return getTable(getTableMeta(clazz).value());
	}	
}
