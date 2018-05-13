package io.github.ar4093.ttcsedit;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class MtrlFileDesc {
	private File f;
	private boolean empty = true;
	private String descr = "";
	private boolean dummy = false;
	
	public MtrlFileDesc ( String path ) {
		this(new File(path));
	}
	
	public MtrlFileDesc ( File file ) {
		this.f = file;
		if (!f.isDirectory())
			descr = FileNameUtils.getShortDescription(f.getName());
	}
	
	public MtrlFileDesc() {
		dummy = true;
	}
	
	@Override
	public String toString () {
		if(dummy)
			return "No colourset files found. Use 'Change Folder' if you have them in a different place.";
		if (f.isDirectory())
			return f.getName();
		else
			return descr;
	}
	
	public String getTooltip () {
		if(dummy)
			return null;
		if (!f.isDirectory())
			return f.getName();
		return null;
	}
	
	public String getPath () {
		return dummy?"":f.getAbsolutePath();
	}
	
	public boolean isDirectory () {
		return dummy?true:f.isDirectory();
	}
	
	public boolean isEmpty () {
		return dummy?true:empty;
	}
	
	public void setEmpty ( boolean e ) {
		empty = e;
	}
	
	public String getTabTitle () {
		return dummy?"":FileNameUtils.getAbbreviation(f.getName());
	}
	
	public String getFullTitle () {
		if(dummy)
			return "";
		HashMap<String, String> data = FileNameUtils.getData(f.getName());
		if(data.get("type").equals("Error")) {
			return "ERROR ["+f.getName()+"]";
		}
		if (data.get("type").equals("Gear"))
			return getFolderName() + " (" + FileNameUtils.getRace(data.get("race")) + " " + data.get("gender") + ") Part "+data.get("part")+" [" + f.getName() + "]";
		if(data.get("type").equals("Weapon"))
			return getFolderName() + " Part "+data.get("part")+" ["+f.getName()+"]";
		if(data.get("type").equals("Monster"))
			 return getFolderName() + " Variant #"+data.get("variant")+" Part "+data.get("part")+" ["+f.getName()+"]";
		return FileNameUtils.getFullDescription(f.getName());
	}
	
	public String getFolderName () {
		if(dummy)
			return "";
		String[] pathparts = f.getAbsolutePath().split("[/\\\\]");
		int savedindex = Arrays.asList(pathparts).indexOf("Saved");
		return pathparts[savedindex + 2];
	}
	
	public String getDatPath () {
		if(dummy)
			return "";
		String p = f.getAbsolutePath();
		p = p.substring(0, p.length() - 3) + "dat";
		if (!(new File(p).exists()))
			return null;
		else
			return p;
	}
	
	public boolean isDummy() {
		return dummy;
	}
}
