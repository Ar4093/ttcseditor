package io.github.ar4093.ttcsedit;

import java.util.HashMap;

public class FileNameUtils {
	private static String[] races = {"Midlander", "Highlander", "Elezen", "Miqo'te", "Roegadyn", "Lalafell", "Au Ra"};
	private static String[] shortraces = {"ML", "HL", "EL", "MQ", "RO", "LF", "AU"};
	
	public static String getFullDescription ( String filename ) {
		HashMap<String, String> data = getData(filename);
		if(data.size() == 0 || data.get("type").equals("Error")) {
			return "Unknown ["+filename+"]";
		}
		if (data.get("type").equals("Gear")) {
			return races[Integer.parseInt(data.get("race"))] + " " + data.get("gender") + " Equip #" + data.get("id") + " Part " + data.get("part") + " [" + filename + "]";
		} else
			return data.get("type") + " #" + data.get("id") + " Variant #" + data.get("variant") + " Part " + data.get("part") + " [" + filename + "]";
	}
	
	public static String getShortDescription ( String filename ) {
		HashMap<String, String> data = getData(filename);
		if(data.size() == 0 || data.get("type").equals("Error")) {
			return "Unknown ["+filename+"]";
		}
		if (data.get("type").equals("Gear"))
			return races[Integer.parseInt(data.get("race"))] + " " + data.get("gender")+" "+data.get("part");
		else
			return data.get("type") + " #" + data.get("id") + " V" + data.get("variant") + " " + data.get("part");
	}
	
	public static String getAbbreviation ( String filename ) {
		HashMap<String, String> data = getData(filename);
		if(data.size() == 0 || data.get("type").equals("Error")) {
			return "Unknown ["+filename+"]";
		}
		if (data.get("type").equals("Gear"))
			return data.get("id") + shortraces[Integer.parseInt(data.get("race"))] + data.get("gender") + data.get("part");
		else
			return data.get("type").charAt(0) + data.get("id") + "V" + data.get("variant") + data.get("part");
	}
	
	public static HashMap<String, String> getData ( String filename ) {
		HashMap<String, String> data = new HashMap<>(6);
		String[] parts = filename.split("_");
		if(parts.length <= 2) {
			data.put("type", "Error");
			return data;
		}
		char ident = parts[1].charAt(0);
		char type = parts[1].charAt(5);
		if (ident == 'c') {
			int rnum = Integer.parseInt(parts[1].substring(1, 3));
			if (rnum > 0 && rnum < 15) {
				data.put("type", "Gear");
				data.put("race", "" + ((rnum - 1) / 2));
				data.put("gender", (rnum % 2 == 1) ? "♂" : "♀");
				data.put("id", parts[1].substring(6, 10));
				data.put("part", parts[parts.length-1].toUpperCase().split("\\.")[0]);
			}
		} else if (ident == 'm') {
			data.put("type", "Monster");
			data.put("id", parts[1].substring(1, 5));
			data.put("variant", parts[1].substring(6, 10));
			data.put("part", parts[parts.length-1].toUpperCase().split("\\.")[0]);
		} else if (ident == 'w') {
			data.put("type", "Weapon");
			data.put("id", parts[1].substring(1, 5));
			data.put("variant", parts[1].substring(6, 10));
			data.put("part", parts[parts.length-1].toUpperCase().split("\\.")[0]);
		}
		return data;
	}
	
	public static String getRace ( String rnum ) {
		return races[(Integer.parseInt(rnum))];
	}
}
