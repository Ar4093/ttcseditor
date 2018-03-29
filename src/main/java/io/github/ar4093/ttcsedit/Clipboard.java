package io.github.ar4093.ttcsedit;

public class Clipboard {
	private static Clipboard instance;
	private ColorGroup group;
	private DatGroup datgroup;
	private ColorRGBA color;
	private ColorsetFile file;
	
	public Clipboard () {
		group = null;
		color = null;
		file = null;
		datgroup = null;
		instance = this;
	}
	
	public static Clipboard getInstance () {
		return instance;
	}
	
	public static ColorGroup getGroup () {
		return instance != null ? new ColorGroup(instance.group) : null;
	}
	
	public static void setGroup ( ColorGroup group ) {
		if (instance != null)
			instance.group = new ColorGroup(group);
	}
	
	public static ColorRGBA getColor () {
		return instance != null ? instance.color : null;
	}
	
	public static void setColor ( ColorRGBA color ) {
		if (instance != null) {
			instance.color = new ColorRGBA(color);
		}
	}
	
	public static ColorsetFile getFile () {
		return instance != null ? instance.file : null;
	}
	
	public static void setFile ( ColorsetFile file ) {
		if (instance != null)
			instance.file = file;
	}
	
	public static boolean hasGroup () {
		return instance != null && instance.group != null;
	}
	
	public static boolean hasColor () {
		return instance != null && instance.color != null;
	}
	
	public static boolean hasFile () {
		return instance != null && instance.file != null;
	}
	
	public static DatGroup getDatGroup () {
		return instance != null ? new DatGroup(instance.datgroup) : null;
	}
	
	public static void setDatgroup ( DatGroup datgroup ) {
		if (instance != null) {
			instance.datgroup = new DatGroup(datgroup);
			System.out.println("Clipboard contents [Datgroup]: " + instance.datgroup);
		}
	}
	
	public static boolean hasDatGroup () {
		return instance != null && instance.datgroup != null;
	}
}
