package ru.greenpix.civilization.buildings.common;

import java.util.Arrays;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.civilization.buildings.Building;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.holograms.UpdateHologram;
import ru.greenpix.civilization.objects.Town;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.civilization.utils.StringUtils;
import ru.greenpix.developer.Placeholder;
import ru.greenpix.developer.utils.protocol.sounds.FixedSound;
import ru.greenpix.developer.utils.protocol.title.Title;
import ru.greenpix.mysql.api.Result;

public class Townhall extends Building {
	
	public static final Vector[] CONTROL_BLOCKS = new Vector[6];
	
	{
		CONTROL_BLOCKS[0] = new Vector(12, 2, 21);
		CONTROL_BLOCKS[1] = new Vector(19, 2, 21);
		CONTROL_BLOCKS[2] = new Vector(23, 8, 27);
		CONTROL_BLOCKS[3] = new Vector(8, 8, 5);
		CONTROL_BLOCKS[4] = new Vector(21, 14, 14);
		CONTROL_BLOCKS[5] = new Vector(10, 14, 18);
	}
	
	private final UpdateHologram[] holograms = new UpdateHologram[6];
	
	public Townhall(Town town, Location location, Style style) {
		super(town, location, style);
		createHolograms();
	}

	public Townhall(Result result, Structure str) {
		super(result, str);
		createHolograms();
	}
	
	private void createHolograms() {
		Block[] a = getControlBlocks();
		for(int i = 0; i < holograms.length; i++) {
			if(getControlBlockDurability(i) == 0) continue;
			final int index = i;
			holograms[i] = new UpdateHologram(a[i].getLocation().clone().add(0.5, 1.6, 0.5), 0, 
					Arrays.asList("&7&l%durability%"), 
					new Placeholder("%durability%", () -> getControlBlockDurability(index) + "/15"));
		}
	}
	
	public boolean regenIfAlive(int health) {
		for(int i = 0; i < 6; i++) {
			int d = getControlBlockDurability(i);
			if(d == 0 || d == 15) continue;
			setControlBlockDurability(i, Math.min(15, d + health));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isActive() {
		return true;
	}
	
	@Override
	public int getMaxDurability() {
		return (int) (Math.pow(2, 24) - 1);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void damage(CivPlayer damager, Block block) {
		if(isBuilding()) return;
		for(int i = 0; i < 6; i++) {
			if(block.equals(getControlBlock(i))) {
				int d = getControlBlockDurability(i);
				if(d == 0) return;
				setControlBlockDurability(i, d -= 1);
				damager.sendMessage("&7" + d + "/15 прочности");
				if(d != 0) {
					FixedSound.ANVIL_USE.playSound(block.getLocation(), 2f, 1f);
					getWorld().playEffect(block.getLocation(), Effect.FLAME, 10);
					getTown().broadcast("&6&lВаш контрольный блок &4&lАТТАКУЮТ&6&l!\n Координаты: &4&l" + StringUtils.formatLocation(block.getLocation()));
					holograms[i].update();
				} else {
					FixedSound.ANVIL_LAND.playSound(block.getLocation(), 2f, .5f);
					FixedSound.EXPLODE.playSound(block.getLocation(), 2f, .5f);
					getWorld().playEffect(block.getLocation(), Effect.EXPLOSION_HUGE, 100);
					getTown().getGroupOfMembers().getOnline().forEach(e -> {
						Title.sendTitle(e.toBukkit(), "&4Ваш контрольный блок разрушили!", 10, 60, 10);
						FixedSound.WITHER_SPAWN.playSound(e.toBukkit(), .8f, 2f);
					});
					block.setType(Material.AIR);
					holograms[i].remove();
					if(isDestroyed()) {
						setDurability(getMaxDurability());
						restoreAll();
						CivCore.broadcast("&lГород &b&l'" + getTown().getName() + "'&f&l был захвачен цивилизацией '" + damager.getCivilization().getName() + "'");
						CivCore.broadcastTitle("&lГород " + getTown().getName() + " захвачен!", "Цивилизацией: &e" + damager.getCivilization().getName() + "&f, Игроком: &e" + damager.getName(), 10, 80, 10);
						if(getTown().getCivilization().getTowns().size() == 1) {
							CivCore.broadcast("&6&lЦивилизация &4&l'" + getTown().getName() + "'&6&l была уничтожена цивилизацией &c&l'" + damager.getCivilization().getName() + "'&6&l");
							CivCore.broadcastTitle("&6&lЦивилизация &4&l" + getTown().getName() + " &6&lуничтожена!", "&eЦивилизацией: &c" + damager.getCivilization().getName() + "&e, Игроком: &c" + damager.getName(), 10, 80, 10);
						}
						getTown().capture(damager.getCivilization());
						for(Block bl : getControlBlocks()) bl.setType(Material.OBSIDIAN);
					} else {
						getTown().broadcast("&4&lВаш контрольный блок был разрушен! У вас осталось &c&l" + getAliveControlBlocks() + "/6");
					}
				}
			}
		}
	}
	
	public void restoreAll() {
		setDurability(getMaxDurability());
		for(int i = 0; i < 6; i++) {
			getControlBlock(i).setType(Material.OBSIDIAN);
			holograms[i].remove();
		}
		createHolograms();
	}
	
	public int getAliveControlBlocks() {
		int a = 0;
		for(int i = 0; i < 6; i++) {
			if(getControlBlockDurability(i) > 0) a++;
		}
		return a;
	}
	
	public int getControlBlockDurability(int index) {
		Preconditions.checkArgument(index >= 0 && index < 6, "Control block index must be in range 0-5");
		return (getDurability() >> (index * 4)) & 15;
	}
	
	public void setControlBlockDurability(int index, int d) {
		Preconditions.checkArgument(index >= 0 && index < 6, "Control block index must be in range 0-5");
		Preconditions.checkArgument(d >= 0 && d < 16, "Control block durability must be in range 0-15");
		int b = getDurability();
		b &= ~(1 << (index * 4)); 
		b &= ~(1 << (index * 4) + 1);  
		b &= ~(1 << (index * 4) + 2);  
		b &= ~(1 << (index * 4) + 3);  
		setDurability(b + (d << (index * 4)));
	}
	
	public Block[] getControlBlocks() {
		Block[] blocks = new Block[6];
		for(int i = 0; i < 6; i++) {
			blocks[i] = getControlBlock(i);
		}
		return blocks;
	}
	
	public Block getControlBlock(int index) {
		Preconditions.checkArgument(index >= 0 && index < 6, "Control block index must be in range 0-5");
		return getRegion().getPos1().clone().add(getClipboard().rotateVector(CONTROL_BLOCKS[index], getRotation())).getBlock();
	}
}
