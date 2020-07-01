package ru.greenpix.civilization.groups;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.objects.Economical;
import ru.greenpix.civilization.objects.Town;

public interface Perms {
	
	/*
	 *  Общие
	 */
	
	@PermData(clazz = Economical.class, desc = "Возможность покупать за счет казны")
	String TREASURE_USE = "treasure.use";
	
	@PermData(clazz = Economical.class, desc = "Возможность забирать деньги из казны")
	String TREASURE_WITHDRAW = "treasure.withdraw";
	
	@PermData(clazz = Economical.class, desc = "Возможность вносить деньги в казну")
	String TREASURE_DEPOSIT = "treasure.deposit";
	
	@PermData(clazz = Economical.class, desc = "Все операции с казной")
	String TREASURE_ALL = "treasure.*";
	
	/*
	 *  Цивилизации
	 */
	
	@PermData(clazz = Civilization.class, desc = "Возможность изучать технологии")
	String RESEARCH = "research";
	
	@PermData(clazz = Civilization.class, desc = "Возможность заключать нейтралитет")
	String REQUEST_NEUTRAL = "diplomacy.request.neutral";
	
	@PermData(clazz = Civilization.class, desc = "Возможность заключать мир")
	String REQUEST_PEACE = "diplomacy.request.peace";
	
	@PermData(clazz = Civilization.class, desc = "Возможность заключать союз")
	String REQUEST_ALLY = "diplomacy.request.ally";
	
	//@PermData(clazz = Civilization.class, desc = "Возможность отвечать на запросы о нейтралитете, мире и союзе")
	//String RESPOND_REQUEST = "diplomacy.request.respond";
	
	@PermData(clazz = Civilization.class, desc = "Возможность объявлять войну")
	String DECLARE_WAR = "diplomacy.declare.war";
	
	@PermData(clazz = Civilization.class, desc = "Возможность объявлять войну")
	String DECLARE_HOSTILE = "diplomacy.declare.hostile";
	
	@PermData(clazz = Civilization.class, desc = "Все операции с дипломатией")
	String DIPLOMACY_ALL = "diplomacy.*";
	
	/*
	 *  Города
	 */
	
	@PermData(clazz = Town.class, desc = "Возможность строить постройку, где <type> - тип постройки")
	String BUILDINGS_BUILD = "buildings.build.<type>";
	
	@PermData(clazz = Town.class, desc = "Возможность строить что угодно, кроме чудес света")
	String BUILDINGS_BUILD_ALL = "buildings.build.*";
	
	@PermData(clazz = Town.class, desc = "Возможность строить чудеса света")
	String BUILDINGS_BUILD_WONDERS = "buildings.wonders";
	
	@PermData(clazz = Town.class, desc = "Возможность удалять свои постройки")
	String BUILDINGS_DESTROY = "buildings.destroy";
	
	@PermData(clazz = Town.class, desc = "Возможность удалять чужие постройки")
	String BUILDINGS_DESTROY_OTHERS = "buildings.destroy.others";
	
	@PermData(clazz = Town.class, desc = "Все операции с постройками")
	String BUILDINGS_ALL = "buildings.*";
	
	@PermData(clazz = Town.class, desc = "Возможность нанимать юнитов")
	String UNITS_CREATE = "units.hire";
	
	@PermData(clazz = Town.class, desc = "Возможность управлять юнитами")
	String UNITS_CONTROL = "units.control";
	
	@PermData(clazz = Town.class, desc = "Возможность приглашать игроков в город")
	String MEMBERS_INVITE = "members.invite";
	
	@PermData(clazz = Town.class, desc = "Возможность выгонять игрокок из города")
	String MEMBERS_KICK = "members.kick";
	
	static Perm find(String permission) {
		for(Perm perm : values()) {
			if(perm.permission.equalsIgnoreCase(permission)) return perm;
		}
		return null;
	}
	
	static Collection<Perm> find(Class<?> clazz) {
		List<Perm> set = new ArrayList<>();
		for(Perm perm : values()) {
			if(perm.clazz.isAssignableFrom(clazz)) set.add(perm);
		}
		return set;
	}
	
	static Perm[] values() {
		Perm[] res = new Perm[Perms.class.getFields().length];
		for(int i = 0; i < res.length; i++) {
			try {
				res[i] = new Perm(Perms.class.getFields()[i]);
			} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	
	class Perm {
		
		public final String permission;
		
		public final Class<?> clazz;
		
		public final String description;
		
		public Perm(Field field) throws IllegalArgumentException, IllegalAccessException {
			PermData ann = field.getAnnotation(PermData.class);
			this.permission = (String) field.get(null);
			this.clazz = ann.clazz();
			this.description = ann.desc();
		}
	}
}
