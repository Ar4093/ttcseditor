package io.github.ar4093.ttcsedit;

public class DatGroup {
	private int colormod, modtype, specularity;
	
	public DatGroup ( byte first, byte second ) {
		colormod = (first & 0xFF) / 16;
		modtype = (first & 0xFF) % 16;
		specularity = second & 0xFF;
	}
	
	public DatGroup ( byte[] bytes ) {
		this(bytes[0], bytes[1]);
	}
	
	public DatGroup ( int colorModifier, int modType, int specularity ) {
		this.colormod = colorModifier;
		this.modtype = modType;
		this.specularity = specularity;
	}
	
	public DatGroup ( DatGroup dg ) {
		this(dg.getColorModifier(), dg.getModType(), dg.getSpecularity());
	}
	
	public int getColorModifier () {
		return colormod;
	}
	
	public void setColorModifier ( int colormod ) {
		this.colormod = colormod;
	}
	
	public int getModType () {
		return modtype;
	}
	
	public void setModType ( int modtype ) {
		this.modtype = modtype;
	}
	
	public int getSpecularity () {
		return specularity;
	}
	
	public void setSpecularity ( int specularity ) {
		this.specularity = specularity;
	}
	
	public byte[] toBytes () {
		byte[] ret = {0, 0};
		ret[0] = (byte) ((colormod % 16) * 16 + modtype % 16);
		ret[1] = (byte) (specularity & 0xFF);
		return ret;
	}
	
	public String toString () {
		return String.format("Dat Group (%d, %d, %d | %X%X%02X)", colormod, modtype, specularity, colormod, modtype, specularity);
	}
}
