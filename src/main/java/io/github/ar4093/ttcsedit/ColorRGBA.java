package io.github.ar4093.ttcsedit;

import javafx.scene.paint.Color;

import java.nio.ByteBuffer;

public class ColorRGBA {
	private HFloat red, green, blue, alpha;
	
	public ColorRGBA ( int red, int green, int blue, int alpha ) {
		this.red = new HFloat((float) red / 255f);
		this.green = new HFloat((float) green / 255f);
		this.blue = new HFloat((float) blue / 255f);
		this.alpha = new HFloat((float) alpha / 255f);
	}
	
	public ColorRGBA ( byte[] bytes ) {
		this.red = new HFloat(bytes[1], bytes[0]);
		this.green = new HFloat(bytes[3], bytes[2]);
		this.blue = new HFloat(bytes[5], bytes[4]);
		this.alpha = new HFloat(bytes[7], bytes[6]);
	}
	
	public ColorRGBA ( int[] bytes ) {
		this.red = new HFloat((byte) (bytes[1]), (byte) (bytes[0]));
		this.green = new HFloat((byte) (bytes[3]), (byte) (bytes[2]));
		this.blue = new HFloat((byte) (bytes[5]), (byte) (bytes[4]));
		this.alpha = new HFloat((byte) (bytes[7]), (byte) (bytes[6]));
	}
	
	public ColorRGBA ( HFloat red, HFloat green, HFloat blue, HFloat alpha ) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public ColorRGBA ( ColorRGBA color ) {
		this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	public float getFloatAt ( int i ) {
		switch (i) {
			case 1:
				return green.floatValue();
			case 2:
				return blue.floatValue();
			case 3:
				return alpha.floatValue();
			default:
				return red.floatValue();
		}
	}
	
	public HFloat getRed () {
		return red;
	}
	
	public HFloat getGreen () {
		return green;
	}
	
	public HFloat getBlue () {
		return blue;
	}
	
	public HFloat getAlpha () {
		return alpha;
	}
	
	public int getIntAt ( int i ) {
		switch (i) {
			case 1:
				return green.intValue();
			case 2:
				return blue.intValue();
			case 3:
				return alpha.intValue();
			default:
				return red.intValue();
		}
	}
	
	public float[] floatValues () {
		float[] fv = {red.floatValue(), green.floatValue(), blue.floatValue(), alpha.floatValue()};
		return fv;
	}
	
	public int[] intValues () {
		int[] iv = {red.intValue(), green.intValue(), blue.intValue(), alpha.intValue()};
		return iv;
	}
	
	public float[] boundedFloatValues () {
		float[] fv = {
			Math.min(1, Math.max(0, red.floatValue())),
			Math.min(1, Math.max(0, green.floatValue())),
			Math.min(1, Math.max(0, blue.floatValue())),
			Math.min(1, Math.max(0, alpha.floatValue()))
		};
		return fv;
	}
	
	public boolean isNormal () {
		return (0 <= red.floatValue() && red.floatValue() <= 1) && (0 <= green.floatValue() && green.floatValue() <= 1) && (0 <= blue.floatValue() && blue.floatValue() <= 1);
	}
	
	public String toString () {
		return "ColorRGBA(" + red.intValue() + ", " + green.intValue() + ", " + blue.intValue() + ", " + alpha.intValue() + ")";
	}
	
	private int[] intToBytes ( int i ) {
		byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
		int[] ret = {bytes[3], bytes[2]};
		return ret;
	}
	
	public byte[] toBytes () {
		byte[] r = red.bytesValue();
		byte[] g = green.bytesValue();
		byte[] b = blue.bytesValue();
		byte[] a = alpha.bytesValue();
		byte[] ret = {r[1], r[0], g[1], g[0], b[1], b[0], a[1], a[0]};
		return ret;
	}
	
	public Color getOpaqueColor () {
		float[] bf = boundedFloatValues();
		return Color.rgb((int) (bf[0] * 255), (int) (bf[1] * 255), (int) (bf[2] * 255), 1.);
	}
	
	public Color getColor () {
		float[] bf = boundedFloatValues();
		return Color.rgb((int) (bf[0] * 255), (int) (bf[1] * 255), (int) (bf[2] * 255), 1.);
	}
	
}
