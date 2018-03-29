package io.github.ar4093.ttcsedit;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ColorsetFile {
	private byte[] header;
	private ColorGroup[] colors;
	private MtrlFileDesc description;
	private boolean changed = false;
	
	public ColorsetFile ( MtrlFileDesc desc ) throws IOException {
		FileInputStream in = null;
		header = new byte[128];
		colors = new ColorGroup[16];
		try {
			in = new FileInputStream(desc.getPath());
			for (int i = 0; i < 128; i++) {
				header[i] = (byte) in.read();
			}
			for (int i = 0; i < 16; i++) {
				ColorRGBA[] cols = new ColorRGBA[4];
				for (int j = 0; j < 4; j++) {
					byte[] cbytes = new byte[8];
					for (int k = 0; k < 8; k++) {
						cbytes[k] = (byte) in.read();
					}
					cols[j] = new ColorRGBA(cbytes);
				}
				colors[i] = new ColorGroup(cols[0], cols[1], cols[2], cols[3]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			this.description = desc;
			if (in != null)
				in.close();
		}
	}
	
	public void write () throws IOException {
		write(description.getPath());
	}
	
	public void write ( String path ) throws IOException {
		//System.out.println("write: file");
		FileOutputStream out = new FileOutputStream((path == null || path.isEmpty()) ? this.description.getPath() : path);
		out.write(header);
		for (int i = 0; i < 16; i++) {
			out.write(colors[i].toBytes());
		}
		out.close();
		changed = false;
	}
	
	public MtrlFileDesc getDescription () {
		return description;
	}
	
	public ColorGroup getGroup ( int i ) {
		return colors[i];
	}
	
	public void setGroup(int i, ColorGroup g) {
		colors[i] = new ColorGroup(g);
	}
	
	public void change () {
		changed = true;
	}
	
	public boolean isChanged () {
		return changed;
	}
}
