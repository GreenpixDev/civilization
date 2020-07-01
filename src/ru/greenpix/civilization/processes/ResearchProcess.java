package ru.greenpix.civilization.processes;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.database.Tables;
import ru.greenpix.civilization.database.Tables.TableCivilizations;
import ru.greenpix.civilization.database.Tables.TableTechnologies;
import ru.greenpix.civilization.objects.Civilization;
import ru.greenpix.civilization.technologies.Era;
import ru.greenpix.civilization.technologies.Technology;
import ru.greenpix.civilization.utils.RunnableManager;
import ru.greenpix.developer.utils.protocol.title.Title;
import ru.greenpix.mysql.elements.MysqlTable;
import ru.greenpix.mysql.nbt.MysqlFields;

public class ResearchProcess extends GameProcess {
	
	private final Civilization civilization;
	
	private final Technology technology;
	
	public ResearchProcess(Civilization civ, Technology tech) {
		this.civilization = civ;
		this.technology = tech;
	}
	
	@Override
	public boolean onUpdate() {
		return true;
	}
	
	@Override
	public void onComplete() {
		Era old = getCivilization().getEra();
		getCivilization().getTechnologies().add(getTechnology());
		getCivilization().broadcast("&aИзучение технологии &2'" + getTechnology().getDisplayName() + "'&a завершено на 100%!");
		Era era = getCivilization().getEra();
		if(old != era) {
			CivCore.broadcast("&fЦивилизация " + getCivilization().getName() + " достигла эры " + era.getColor() + "'" + era.getDisplayName() + "'");
			getCivilization().getGroupOfMembers().getOnline().forEach(e -> {
				Title.sendTitles(e.toBukkit(), era.getColor() + "&l" + era.getDisplayName(), "&fВаша цивилизация достигла новой эры!", 10, 60, 10);
				e.asyncUpdateTag();
			});
		}
		RunnableManager.async(() -> Tables.getTable(TableTechnologies.class).add(new MysqlFields().put(TableTechnologies.CIVILIZATION, getCivilization().getId()).put(TableTechnologies.TECHNOLOGY, getTechnology().getName())));
	}
	
	@Override
	public double perTick() {
		return civilization.getBeakersPerMinute() / 1200D;
	}

	@Override
	public double getMaxValue() {
		return technology.getBeakers();
	}

	public Civilization getCivilization() {
		return civilization;
	}

	public Technology getTechnology() {
		return technology;
	}

	@Override
	public int getId() {
		return getCivilization().getId();
	}

	@Override
	public MysqlTable getSqlTable() {
		return getCivilization().getSqlTable();
	}

	@Override
	public MysqlFields getSqlRecord() {
		return new MysqlFields()
				.put(TableCivilizations.TECHNOLOGY_PROGRESS, isCompleted() ? 100 : getProgress())
				.put(TableCivilizations.TECHNOLOGY_TASK, isCompleted() ? null : getTechnology().getName());
	}
}
