package ru.greenpix.civilization.clipboard;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import ru.greenpix.civilization.CivCore;
import ru.greenpix.developer.files.ZipFile;
import ru.greenpix.developer.files.nbt.ByteArrayTag;
import ru.greenpix.developer.files.nbt.CompoundTag;
import ru.greenpix.developer.files.nbt.NamedTag;
import ru.greenpix.developer.files.nbt.ShortTag;
import ru.greenpix.developer.files.nbt.StringTag;
import ru.greenpix.developer.files.nbt.Tag;

public class SchematicReader {
	
	public static CachedClipboard cache(File file, int cacheTime) throws IOException {
		NamedTag rootTag = new ZipFile(file.getAbsolutePath()).read();
		if(!rootTag.getName().equals("Schematic")) {
			throw new IOException("Tag 'Schematic' does not exist or is not first");
		}
		CompoundTag schematicTag = (CompoundTag)rootTag.getTag();
        Map<String, Tag> schematic = schematicTag.getValue();
        if (!schematic.containsKey("Blocks")) {
            throw new IOException("Schematic file is missing a 'Blocks' tag");
        }
        String materials = requireTag(schematic, "Materials", StringTag.class).getValue();
        if (!materials.equals("Civcraft") && !materials.equals("Alpha")) {
            throw new IOException("Schematic file is not an Civcraft schematic");
        }
        int width = (short) requireTag(schematic, "Width", ShortTag.class).getValue();
        int height = (short) requireTag(schematic, "Height", ShortTag.class).getValue();
        int length = (short) requireTag(schematic, "Length", ShortTag.class).getValue();
		return new CachedClipboard(width, height, length, cacheTime, () -> {
			try {
				long ms = System.currentTimeMillis();
				ArrayClipboard clip = read(file);
				CivCore.getInstance().getLogger().info("Schematic cached '" + file.getPath() + "' for " + (System.currentTimeMillis() - ms) + " ms.");
				return clip;
			} catch (IOException e) {
				CivCore.getInstance().getLogger().warning("Schematic '" + file.getPath() + "' cannot be loaded!");
				e.printStackTrace();
				return null;
			}
		});
	}
	
	/**
	 * @author sk89q
	 */
	
	public static ArrayClipboard read(File file) throws IOException {
		NamedTag rootTag = new ZipFile(file.getAbsolutePath()).read();
		if(!rootTag.getName().equals("Schematic")) {
			throw new IOException("Tag 'Schematic' does not exist or is not first");
		}
		CompoundTag schematicTag = (CompoundTag)rootTag.getTag();
        Map<String, Tag> schematic = schematicTag.getValue();
        if (!schematic.containsKey("Blocks")) {
            throw new IOException("Schematic file is missing a 'Blocks' tag");
        }
        String materials = requireTag(schematic, "Materials", StringTag.class).getValue();
        if (!materials.equals("Civcraft") && !materials.equals("Alpha")) {
            throw new IOException("Schematic file is not an Civcraft schematic");
        }
        short width = requireTag(schematic, "Width", ShortTag.class).getValue();
        short height = requireTag(schematic, "Height", ShortTag.class).getValue();
        short length = requireTag(schematic, "Length", ShortTag.class).getValue();
        byte[] blockId = requireTag(schematic, "Blocks", ByteArrayTag.class).getValue();
        byte[] blockData = requireTag(schematic, "Data", ByteArrayTag.class).getValue();
        byte[] addId = new byte[0];
        short[] blocks = new short[blockId.length];
        if (schematic.containsKey("AddBlocks")) {
            addId = requireTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
        }
        for (int index = 0; index < blockId.length; ++index) {
            if (index >> 1 >= addId.length) {
                blocks[index] = (short)(blockId[index] & 0xFF);
            }
            else if ((index & 0x1) == 0x0) {
                blocks[index] = (short)(((addId[index >> 1] & 0xF) << 8) + (blockId[index] & 0xFF));
            }
            else {
                blocks[index] = (short)(((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
            }
        }
        ArrayClipboard clipboard = new ArrayClipboard(width, height, length);
        for (int y = 0; y < height; ++y) {
        	for (int x = 0; x < width; ++x) {
        		for (int z = 0; z < length; ++z) {
                    final int index = y * width * length + z * width + x;
                    clipboard.setBlock(x, y, z, blocks[index], blockData[index]);
                }
            }
        }
		return clipboard;
	}
	
	/**
	 * @author sk89q
	 */
	
	private static <T extends Tag> T requireTag(Map<String, Tag> items, String key, Class<T> expected) throws IOException {
		if (!items.containsKey(key)) {
			throw new IOException("Schematic file is missing a \"" + key + "\" tag");
		}
		final Tag tag = items.get(key);
		if (!expected.isInstance(tag)) {
			throw new IOException(key + " tag is not of tag type " + expected.getName());
		}
		return expected.cast(tag);
	}
}
