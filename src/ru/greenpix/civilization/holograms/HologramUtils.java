package ru.greenpix.civilization.holograms;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class HologramUtils {
	
	public static void registerAllPlaceholders(Plugin owner) {
		for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			HologramsAPI.registerPlaceholder(owner, "{plugin_version:"+plugin.getName()+"}", Integer.MAX_VALUE, () -> plugin.getDescription().getVersion());
			HologramsAPI.registerPlaceholder(owner, "{plugin_depend:"+plugin.getName()+"}", Integer.MAX_VALUE, () -> plugin.getDescription().getDepend().toString());
			HologramsAPI.registerPlaceholder(owner, "{plugin_softdepend:"+plugin.getName()+"}", Integer.MAX_VALUE, () -> plugin.getDescription().getSoftDepend().toString());
			HologramsAPI.registerPlaceholder(owner, "{plugin_authors:"+plugin.getName()+"}", Integer.MAX_VALUE, () -> plugin.getDescription().getAuthors().toString());
			HologramsAPI.registerPlaceholder(owner, "{plugin_desc:"+plugin.getName()+"}", Integer.MAX_VALUE, () -> plugin.getDescription().getDescription());
		}
	}
	
}
