package io.github.ar4093.ttcsedit;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ColorsetDatFile {
	//private byte[] data;
	private DatGroup[] groups;
	private boolean changed = false;
	private MtrlFileDesc description;
	
	public ColorsetDatFile ( MtrlFileDesc desc ) throws IOException {
		FileInputStream in = null;
		groups = new DatGroup[16];
		try {
			in = new FileInputStream(desc.getDatPath());
			for (int i = 0; i < 16; i++)
				groups[i] = new DatGroup((byte) in.read(), (byte) in.read());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				in.close();
		}
		description = desc;
	}
	
	public boolean isChanged () {
		return changed;
	}
	
	public void write () throws IOException {
		write(description.getDatPath());
	}
	
	public void write ( String path ) throws IOException {
		//System.out.println("write: datfile");
		FileOutputStream out = null;
		try {
			out = new FileOutputStream((path == null || path.isEmpty()) ? this.description.getDatPath() : path);
			for (int i = 0; i < 16; i++) {
				//System.out.format("written:%02X %02X\n", groups[i].toBytes()[0], groups[i].toBytes()[1]);
				out.write(groups[i].toBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
				changed = false;
			}
		}
	}
	
	public DatGroup getGroup ( int i ) {
		return new DatGroup(groups[i]);
	}
	
	public DatGroup getGroupModifiable(int i) {
		return groups[i];
	}
	
	public void setGroup ( int i, DatGroup dg ) {
		groups[i] = new DatGroup(dg);
		changed = true;
	}
}
