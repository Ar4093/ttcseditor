package io.github.ar4093.ttcsedit;

public class ColorGroup {
	private ColorRGBA regular, metallic, glow, four;
	
	public ColorGroup ( ColorRGBA regular, ColorRGBA metallic, ColorRGBA glow, ColorRGBA four ) {
		this.regular = regular;
		this.metallic = metallic;
		this.glow = glow;
		this.four = four;
	}
	
	public ColorGroup ( ColorGroup g ) {
		this(g.getColorAt(0), g.getColorAt(1), g.getColorAt(2), g.getColorAt(3));
	}
	
	public String toString () {
		return "ColorGroup: [\n  Regular: " + regular + "\n  Metallic:" + metallic +
			       "\n  Glow:    " + glow + "\n  ????:    " + four + "\n]";
	}
	
	public byte[] toBytes () {
		byte[] outs = new byte[32];
		byte[] reg = regular.toBytes();
		byte[] met = metallic.toBytes();
		byte[] glo = glow.toBytes();
		byte[] f = four.toBytes();
		for (int i = 0; i < 8; i++) {
			outs[i] = reg[i];
			outs[i + 8] = met[i];
			outs[i + 16] = glo[i];
			outs[i + 24] = f[i];
		}
		return outs;
	}
	
	public ColorRGBA getColorAt ( int i ) {
		switch (i) {
			case 1:
				return metallic;
			case 2:
				return glow;
			case 3:
				return four;
			default:
				return regular;
		}
	}
	
	public void setColorAt ( int i, ColorRGBA newColor ) {
		switch (i) {
			case 1:
				metallic = newColor;
				break;
			case 2:
				glow = newColor;
				break;
			case 3:
				four = newColor;
				break;
			default:
				regular = newColor;
				break;
		}
	}
}
