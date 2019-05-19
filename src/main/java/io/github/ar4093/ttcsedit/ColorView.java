package io.github.ar4093.ttcsedit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Arrays;

public class ColorView extends HBox {
	private ColorRGBA color;
	@FXML
	private TextField red, green, blue, alpha;
	@FXML
	private Label typeText;
	@FXML
	private Label colorDisplay;
	@FXML
	private ColorPicker picker;
	private int type;
	private ColorSetFileView parent;
	
	ColorView ( int type, ColorSetFileView parent ) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("color_view.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		this.parent = parent;
		try {
			loader.load();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		this.type = type;
		if (type > 0) {
			picker.setVisible(false);
		}
		final ContextMenu cmenu = new ContextMenu();
		switch (type) {
			case 1:
				alpha.setTooltip(new Tooltip("Roughness"));
				typeText.setText("Metallic");
				break;
			case 2:
				final Tooltip t = new Tooltip("Material Type (Detailed information\nand known values in the reference information sheet (TT discord)");
				alpha.setTooltip(t);
				typeText.setText("Glow");
				break;
			case 3:
				red.setTooltip(new Tooltip("Shader/Texture Repetition (X)"));
				alpha.setTooltip(new Tooltip("Shader/Texture Repetition (Y)"));
				green.setTooltip(new Tooltip("Shader Inputs?"));
				blue.setTooltip(new Tooltip("Shader Inputs?"));
				typeText.setText("Shader Mod");
				break;
			default:
				alpha.setTooltip(new Tooltip("Unknown"));
				typeText.setText("Regular");
				break;
		}
		for(TextField tf : Arrays.asList(red, green, blue, alpha)) {
			tf.focusedProperty().addListener((ov, o, n) -> {
				if(!n)
					update();
			});
			tf.setOnAction(e -> update());
			tf.setOnInputMethodTextChanged(e -> update());
		}
		MenuItem miCopy = new MenuItem("Copy Colour");
		MenuItem miPaste = new MenuItem("Paste Colour");
		miCopy.setOnAction(e -> Clipboard.setColor(color));
		miPaste.setOnAction(e -> setColor(Clipboard.getColor()));
		miPaste.setOnAction(e -> setColor(Clipboard.getColor()));
		ContextMenu colorContextMenu = new ContextMenu();
		colorContextMenu.getItems().addAll(miCopy, miPaste);
		colorContextMenu.setOnShown(e -> miPaste.setDisable(!Clipboard.hasColor()));
		colorDisplay.setContextMenu(colorContextMenu);
	}
	
	@FXML
	private void applyColor () {
		colorDisplay.setBackground(new Background(new BackgroundFill(color.getOpaqueColor(), CornerRadii.EMPTY, Insets.EMPTY)));
		red.setText("" + color.getIntAt(0));
		green.setText("" + color.getIntAt(1));
		blue.setText("" + color.getIntAt(2));
		alpha.setText("" + color.getIntAt(3));
		if (type == 0) {
			picker.setValue(color.getColor());
		}
		parent.colorChanged(type);
	}
	
	@FXML
	private void update () {
		color = new ColorRGBA(Integer.parseInt(red.getText()), Integer.parseInt(green.getText()), Integer.parseInt(blue.getText()), Integer.parseInt(alpha.getText()));
		applyColor();
	}
	
	@FXML
	private void updateFromPicker () {
		Color c = picker.getValue();
		color = new ColorRGBA((int) Math.ceil(255.f*c.getRed()), (int) Math.ceil(255.f*c.getGreen()), (int) Math.ceil(255.f*c.getBlue()), (int) Math.ceil(255.f*c.getOpacity()));
		applyColor();
	}
	
	public ColorRGBA getColor () {
		return color;
	}
	
	@FXML
	public void setColor ( ColorRGBA c ) {
		color = c;
		applyColor();
	}
}
