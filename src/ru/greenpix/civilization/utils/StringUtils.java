package ru.greenpix.civilization.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;

import ru.greenpix.civilization.CivCore;

public class StringUtils {

	public static String formatDouble(double number, int arg){
		String type = "###.";
		for(int i = 0; i < arg; i++){
			type = type + "#";
		}
		DecimalFormat format = new DecimalFormat(type);
		String output = format.format(number);
		return output;
	}
	
	@Deprecated
	public static String formatTimeOld(int seconds) {
		int hours = seconds / 3600;
		int min = (seconds - (hours * 3600)) / 60;
		int s = seconds - (hours * 3600) - (min * 60);
		if(s < 0) s = 0;
		return (hours > 0 ? hours + (hours == 1 ? " час " : (hours > 4 ? " часов " : " часа ")) : "") +
				(min > 0 ? min + (min == 1 ? " минута " : (min > 4 ? " минут " : " минуты ")) : "") +
				s + (s == 0 ? " секунд " : s == 1 ? " секунда " : (s > 4 ? " секунд " : " секунды "));
	}
	
	public static String formatTime(int seconds) {
		if(seconds >= 864000000) return "∞";
		long millis = seconds * 1000L;
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
	            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
	            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
	
	public static long getVersion(String version) {
		long l = 0;
		for(String s : version.split("\\.")) {
			l <<= 12;
			l += Short.parseShort(s.replaceAll("[^0-9]", "")) & 4095;
		}
		return l;
	}
	
	public static String formatDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		if(CivCore.getCfg().contains("timezone")) {
			format.setTimeZone(TimeZone.getTimeZone(CivCore.getCfg().getString("timezone")));
		}
		return format.format(date);
	}
	
	public static String formatDateAndTime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		if(CivCore.getCfg().contains("timezone")) {
			format.setTimeZone(TimeZone.getTimeZone(CivCore.getCfg().getString("timezone")));
		}
		return format.format(date) + " по МСК";
	}
	
	public static String formatLocation(Location l) {
		return String.format("x(%s), y(%s), z(%s)", l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	public static String formatLocation2D(Location l) {
		return String.format("x(%s), z(%s)", l.getBlockX(), l.getBlockZ());
	}
	
	public static String align(String str, int max) {
		StringBuilder builder = new StringBuilder();
		int length = (max - str.length()) / 2;
		for(int i = 0; i < length; i++) builder.append(" ");
		return new String(builder.append(str));
	}
	
	public static String align(String str, int max, char c) {
		StringBuilder builder = new StringBuilder();
		int length = (max - str.length()) / 2;
		for(int i = 0; i < length; i++) builder.append(c);
		builder.append(str);
		for(int i = 0; i < length; i++) builder.append(c);
		return new String(builder);
	}
	
	public static String align(String str, int max, String c) {
		StringBuilder builder = new StringBuilder();
		int length = (max - str.length()) / 2;
		for(int i = 0; i < length; i += c.length()) builder.append(c);
		builder.append(str);
		for(int i = 0; i < length; i += c.length()) builder.append(c);
		return new String(builder);
	}
	
	public static String replaceLast(String s, String suffix) {
		StringBuilder b = new StringBuilder(s);
		b.deleteCharAt((b.length() - 1));
		b.append(suffix);
		return new String(b);
	}
}
