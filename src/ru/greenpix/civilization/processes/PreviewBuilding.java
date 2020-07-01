package ru.greenpix.civilization.processes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import ru.greenpix.civilization.Prefixs;
import ru.greenpix.civilization.buildings.Direction;
import ru.greenpix.civilization.buildings.Region;
import ru.greenpix.civilization.buildings.Structure;
import ru.greenpix.civilization.buildings.Style;
import ru.greenpix.civilization.clipboard.BaseBlock;
import ru.greenpix.civilization.clipboard.CachedClipboard;
import ru.greenpix.civilization.clipboard.Clipboard;
import ru.greenpix.civilization.utils.RunnableManager;

public class PreviewBuilding {
	
	public final static Map<Integer, Byte> ID_COLOR_MAP;
	public final static Map<BaseBlock, Byte> DATA_COLOR_MAP;
	
	private static void putColor(int id, int color) {
		ID_COLOR_MAP.put(id, (byte) color);
	}
	
	private static void putColor(int id, int data, int color) {
		DATA_COLOR_MAP.put(new BaseBlock(id, (byte) data, new Vector(0, 0, 0)), (byte) color);
	}
	
	static {
		ID_COLOR_MAP = new HashMap<Integer, Byte>();
		DATA_COLOR_MAP = new HashMap<BaseBlock, Byte>();
		// Белый
		putColor(1, 3, 0);
		putColor(1, 4, 0);
		putColor(155, 0);
		putColor(156, 0);
		putColor(44, 7, 0);
		putColor(44, 15, 0);
		putColor(42, 0);
		putColor(80, 0);
		putColor(216, 0);
		putColor(153, 0);
		putColor(30, 0);
		// Оранжевый
		putColor(12, 1, 1);
		putColor(86, 1);
		putColor(91, 1);
		putColor(179, 1);
		putColor(180, 1);
		putColor(182, 1);
		putColor(213, 1);
		// Пурпурный
		putColor(201, 2);
		putColor(202, 2);
		putColor(203, 2);
		putColor(205, 2);
		// Голубой
		putColor(56, 3);
		putColor(57, 3);
		putColor(79, 3);
		putColor(174, 3);
		// Желтый
		putColor(12, 0, 4);
		putColor(14, 4);
		putColor(19, 4);
		putColor(41, 4);
		putColor(24, 4);
		putColor(44, 1, 4);
		putColor(44, 9, 7);
		putColor(89, 4);
		putColor(128, 4);
		putColor(121, 4);
		putColor(206, 4);
		// Зеленый
		putColor(2, 5);
		putColor(129, 5);
		putColor(133, 5);
		putColor(18, 0, 5);
		putColor(18, 2, 5);
		putColor(18, 3, 5);
		putColor(161, 5);
		putColor(81, 5);
		putColor(161, 5);
		putColor(165, 5);
		// Розовый
		// Темно серый
		putColor(1, 0, 7);
		putColor(1, 5, 7);
		putColor(1, 6, 7);
		putColor(4, 7);
		putColor(7, 7);
		putColor(13, 7);
		putColor(44, 3, 7);
		putColor(44, 11, 7);
		putColor(44, 5, 7);
		putColor(44, 13, 7);
		putColor(67, 7);
		putColor(109, 7);
		putColor(98, 7);
		putColor(145, 7);
		putColor(139, 7);
		putColor(101, 7);
		putColor(97, 7);
		putColor(61, 7);
		// Серый
		putColor(15, 8);
		putColor(44, 0, 8);
		putColor(44, 8, 7);
		putColor(82, 8);
		// Бирюзовый
		putColor(168, 9);
		putColor(169, 9);
		// Фиолетовый
		// Темно синий
		putColor(21, 11);
		putColor(22, 11);
		// Коричневый
		putColor(1, 1, 12);
		putColor(1, 2, 12);
		putColor(216, 12);
		putColor(3, 12);
		putColor(60, 12);
		putColor(5, 12);
		putColor(17, 12);
		putColor(126, 12);
		putColor(32, 12);
		putColor(53, 12);
		putColor(134, 12);
		putColor(135, 12);
		putColor(136, 12);
		putColor(162, 12);
		putColor(163, 12);
		putColor(164, 12);
		putColor(88, 12);
		putColor(110, 12);
		putColor(172, 12);
		putColor(47, 12);
		putColor(25, 12);
		putColor(146, 12);
		putColor(84, 12);
		putColor(58, 12);
		putColor(54, 12);
		putColor(85, 12);
		putColor(188, 12);
		putColor(189, 12);
		putColor(190, 12);
		putColor(191, 12);
		putColor(192, 12);
		// Темно зеленый
		putColor(48, 13);
		putColor(103, 13);
		putColor(18, 1, 13);
		// Красный
		putColor(73, 14);
		putColor(45, 14);
		putColor(44, 4, 14);
		putColor(44, 12, 7);
		putColor(108, 14);
		putColor(87, 14);
		putColor(112, 14);
		putColor(114, 14);
		putColor(44, 6, 14);
		putColor(44, 14, 7);
		putColor(214, 14);
		putColor(215, 14);
		putColor(40, 14);
		putColor(152, 14);
		putColor(113, 14);
		// Черный
		putColor(16, 15);
		putColor(49, 15);
		putColor(173, 15);
		putColor(130, 15);
		putColor(116, 15);
	}
	
	public BaseBlock color(BaseBlock block) {
		if(!replaceBlocks) return block;
		if(block.getId() == 0 || block.getId() == 8 || block.getId() == 9 || block.getId() == 10 || block.getId() == 11) return block;
		if(block.getId() == 95 || block.getId() == 20 || block.getId() == 160 || block.getId() == 102) return block;
		Byte data = null;
		if(block.getId() == 35 || block.getId() == 159 || block.getId() == 251 || block.getId() == 252) {
			data = block.getData();
			block.setId(95);
			block.setData(data);
		}
		data = ID_COLOR_MAP.get(block.getId());
		if(data == null) {
			data = DATA_COLOR_MAP.entrySet().stream()
				.filter(e -> e.getKey().getId() == block.getId() && e.getKey().getData() == block.getData())
				.map(e -> e.getValue())
				.findFirst().orElse(null);
			if(data == null) {
				block.setId(0);
				return block;
			}
		}
		if(block.getId() == 188 || block.getId() == 189 || block.getId() == 190 || block.getId() == 191 ||
				block.getId() == 192 || block.getId() == 85 || block.getId() == 113 || block.getId() == 139 ||
				block.getId() == 101 || block.getId() == 105) {
			block.setId(160);
			block.setData(data);
		} else {
			block.setId(95);
			block.setData(data);
		}
		return block;
	}
	
	private final List<Block> changedBlocks = new ArrayList<>();
	
	private final Player player;
	
	private final Style style;
	
	private final Region region;
	
	private final Direction direction;
	
	private final boolean replaceBlocks;
	
	private final Location location;
	
	private BukkitTask task = null;
	
	private boolean closed = false;
	
	public PreviewBuilding(Player player, Style style, boolean replaceBlocks) {
		this.player = player;
		this.style = style;
		this.replaceBlocks = replaceBlocks;
		this.direction = Direction.getByLocation(player.getLocation());
		this.location  = player.getLocation().clone();
		Direction.getByLocation(location).alignX(location, style.getClipboard(), false).addZ(location, 1).add(location, style.getStructure().getOffset());
		this.region = new Region(location, style.getClipboard(), direction.getRotation());
	}
	
	public PreviewBuilding start() {
		if(closed) {
			throw new UnsupportedOperationException("Preview closed");
		}
		if(task != null) return this;
		if(getClipboard() instanceof CachedClipboard && !((CachedClipboard) getClipboard()).isCached()) {
			((CachedClipboard) getClipboard()).cache(e -> {
				if(e) {
					run();
				}
				else getPlayer().sendMessage(Prefixs.ERROR + "Произошла ошибка при попытке загрузить постройку, обратитесь к администраторам!");
			});
		} else run();
		return this;
	}
	
	@SuppressWarnings("deprecation")
	public void run() {
		if(closed) return;
		Runnable fill = () -> task = RunnableManager.runGradually(style.getClipboard().iterator(), b -> {
			Location l = getRegion().getPos1().clone().add(getClipboard().rotateVector(b.getVector(), getRotation()));
			changedBlocks.add(l.getBlock());
			color(getClipboard().rotateBlock(b, getRotation())).send(getPlayer(), l);
		}, 100L, 10L, 256);
		task = RunnableManager.runGradually(getRegion().iterator(), b -> {
			if(b.getY() == getRegion().getPos1().getBlockY()) {
				changedBlocks.add(b);
				getPlayer().sendBlockChange(b.getLocation(), 7, b.getData());
			} else if(b.getY() == getRegion().getPos2().getBlockY()) {
				if(b.getX() == getRegion().getPos1().getBlockX() || b.getZ() == getRegion().getPos1().getBlockZ() ||
						b.getX() == getRegion().getPos2().getBlockX() || b.getZ() == getRegion().getPos2().getBlockZ()) {
					changedBlocks.add(b);
					getPlayer().sendBlockChange(b.getLocation(), 7, b.getData());
				}
			} else {
				if((b.getX() == getRegion().getPos1().getBlockX() && b.getZ() == getRegion().getPos1().getBlockZ()) ||
						(b.getX() == getRegion().getPos1().getBlockX() && b.getZ() == getRegion().getPos2().getBlockZ()) ||
						(b.getX() == getRegion().getPos2().getBlockX() && b.getZ() == getRegion().getPos1().getBlockZ()) ||
						(b.getX() == getRegion().getPos2().getBlockX() && b.getZ() == getRegion().getPos2().getBlockZ())) {
					changedBlocks.add(b);
					getPlayer().sendBlockChange(b.getLocation(), 7, b.getData());
				} 
			}
		}, fill, 1L, 10240);
	}
	
	public void close() {
		close(() -> {});
	}
	
	public Location getLocation() {
		return location;
	}
	
	@SuppressWarnings("deprecation")
	public void close(Runnable done) {
		closed = true;
		if(task != null && !task.isCancelled()) {
			task.cancel();
		}
		RunnableManager.runGradually(getChangedBlocks(), b -> {
			getPlayer().sendBlockChange(b.getLocation(), b.getType(), b.getData());
		}, done, 1L, 1024);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Region getRegion() {
		return region;
	}
	
	public Style getStyle() {
		return style;
	}
	
	public Clipboard getClipboard() {
		return style.getClipboard();
	}
	
	public Structure getStructure() {
		return style.getStructure();
	}
	
	public int getRotation() {
		return direction.getRotation();
	}
	
	public Direction getDirection() {
		return direction;
	}

	public List<Block> getChangedBlocks() {
		return changedBlocks;
	}
	
	public Set<Chunk> getChangedChunks() {
		Set<Chunk> set = new HashSet<Chunk>();
		for(Block b : getChangedBlocks()) {
			set.add(b.getChunk());
		}
		return set;
	}

	public BukkitTask getTask() {
		return task;
	}
}
