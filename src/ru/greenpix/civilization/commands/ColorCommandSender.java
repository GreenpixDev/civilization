package ru.greenpix.civilization.commands;

import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import ru.greenpix.developer.Utils;

public class ColorCommandSender implements CommandSender {

	private CommandSender handle;
	
	public ColorCommandSender(CommandSender handle) {
		this.handle = handle;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		return handle.addAttachment(arg0);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return handle.addAttachment(arg0, arg1);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
		return handle.addAttachment(arg0, arg1, arg2);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
		return handle.addAttachment(arg0, arg1, arg2, arg3);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return handle.getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(String arg0) {
		return handle.hasPermission(arg0);
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		return handle.hasPermission(arg0);
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		return handle.isPermissionSet(arg0);
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		return handle.isPermissionSet(arg0);
	}

	@Override
	public void recalculatePermissions() {
		handle.recalculatePermissions();
	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		handle.removeAttachment(arg0);
	}

	@Override
	public boolean isOp() {
		return handle.isOp();
	}

	@Override
	public void setOp(boolean arg0) {
		handle.setOp(arg0);
	}

	@Override
	public String getName() {
		return handle.getName();
	}

	@Override
	public Server getServer() {
		return handle.getServer();
	}

	@Override
	public void sendMessage(String arg0) {
		handle.sendMessage(Utils.color(arg0));
	}

	@Override
	public void sendMessage(String[] arg0) {
		handle.sendMessage(Stream.of(arg0).map(e -> Utils.color(e)).toArray(String[]::new));
	}

	@Override
	public Spigot spigot() {
		return handle.spigot();
	}
	
	public CommandSender getHandle() {
		return handle;
	}
	
	public boolean isPlayer() {
		return handle instanceof Player;
	}
	
	public Player toPlayer() {
		return (Player) handle;
	}
}
