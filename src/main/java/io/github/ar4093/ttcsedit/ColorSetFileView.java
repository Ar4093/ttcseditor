package io.github.ar4093.ttcsedit;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.Optional;

public class ColorSetFileView extends HBox {
	private ColorView regular, metallic, glow, four;
	//private Spinner<Integer> colormod, spec1, spec2, spec;
	//private ChoiceBox<String> modtype;
	private ChoiceBox<String> mod_type, mod_color;
	private ColorsetFile file;
	private ColorsetDatFile datfile;
	private Canvas canvas;
	private double LINE_WIDTH = 2;
	private double CELL_SIZE = 30;
	private int currentSet = -1;
	private boolean updating = false;
	
	public ColorSetFileView ( ColorsetFile file, ColorsetDatFile datfile ) {
		setAlignment(Pos.CENTER);
		regular = new ColorView(0, this);
		metallic = new ColorView(1, this);
		glow = new ColorView(2, this);
		four = new ColorView(3, this);
		this.file = file;
		this.datfile = datfile;
		VBox cset = new VBox();
		cset.getChildren().addAll(regular, metallic, glow, four);
		cset.setAlignment(Pos.CENTER_LEFT);
		if (datfile != null) {
			GridPane datview = new GridPane();
			Label label1 = new Label("Color Modifier");
			Label label2 = new Label("Mod Type");
			Label label3 = new Label("Specularity");
			label1.setPadding(new Insets(5));
			label1.setMinWidth(100);
			label2.setPadding(new Insets(5));
			label2.setMinWidth(100);
			label3.setPadding(new Insets(5));
			label3.setMinWidth(100);
			datview.add(label1, 0, 0);
			datview.add(label2, 0, 1);
			datview.add(label3, 0, 2);
			Button btnCopyDat = new Button("Copy Dye Modifiers");
			Button btnPasteDat = new Button("Paste Dye Modifiers");
			btnPasteDat.setDisable(true);
			btnCopyDat.setOnAction(e -> {
				if (!Clipboard.hasColor())
					btnPasteDat.setDisable(false);
				Clipboard.setDatgroup(getCurrentDatGroup());
			});
			btnPasteDat.setOnAction(e -> {
				setDatGroup(Clipboard.getDatGroup());
				System.out.println("Pasted Dat");
			});
			/*colormod = new Spinner<>(0, 15, 0, 1);
			colormod.setMaxWidth(80);
			colormod.getEditor().setAlignment(Pos.CENTER_RIGHT);
			colormod.setEditable(true);
			modtype = new ChoiceBox<>();
			modtype.getItems().addAll("0 - Unaffected", "B - Diffuse", "D - Glow");
			spec1 = new Spinner<>(0, 15, 0, 1);
			spec2 = new Spinner<>(0, 15, 0, 1);
			spec = new Spinner<>(0, 255, 0, 1);
			spec.setMaxWidth(80);
			spec.setEditable(true);
			spec.getEditor().setAlignment(Pos.CENTER_RIGHT);
			datview.add(colormod, 1, 1);
			datview.add(modtype, 1, 0);
			datview.add(spec, 1, 2);*/
			datview.add(btnCopyDat, 2, 1);
			datview.add(btnPasteDat, 2, 2);
			datview.setAlignment(Pos.CENTER_LEFT);
			datview.setPadding(new Insets(5, 0, 0, 0));
			datview.setHgap(10);
			datview.setVgap(10);
			//colormod.setTooltip(new Tooltip("Known values:\n0: Uses default dye colour\n2: Darker than normal\n5: Brighter than normal"));
			cset.getChildren().add(datview);
		}
		initCanvas();
		activateSet(-1, 0);
		if (datfile != null) {
			/*colormod.valueProperty().addListener(( ov, o, n ) -> {
				if (!updating) {
					datfile.getGroupModifiable(currentSet).setColorModifier(n);
				}
			});
			spec.valueProperty().addListener(( ov, o, n ) -> {
				if (!updating) {
					datfile.getGroupModifiable(currentSet).setSpecularity(n);
				}
			});
			modtype.getSelectionModel().selectedItemProperty().addListener(( ov, o, n ) -> {
				if (!updating) {
					int val = Integer.parseInt(n.substring(0, 1), 16);
					datfile.getGroupModifiable(currentSet).setModType(val);
				}
			});*/
			
		}
		getChildren().add(canvas);
		getChildren().add(cset);
		
	}
	
	private void setDatGroup ( DatGroup dg ) {
		if (dg == null)
			return;
		updating = true;
		datfile.setGroup(currentSet, dg);
		colormod.getValueFactory().setValue(dg.getColorModifier());
		spec.getValueFactory().setValue(dg.getSpecularity());
		int i = dg.getModType();
		switch (i) {
			case 0:
				modtype.getSelectionModel().select(0);
				break;
			case 11:
				modtype.getSelectionModel().select(1);
				break;
			case 13:
				modtype.getSelectionModel().select(2);
				break;
			default:
				String a = String.format("%X", i);
				boolean found = false;
				for (int k = 0; !found && k < modtype.getItems().size(); k++) {
					if (modtype.getItems().get(k).startsWith(a)) {
						modtype.getSelectionModel().select(k);
						found = true;
					}
				}
				if (!found) {
					modtype.setTooltip(new Tooltip("If you want to help out with understanding this value, let the TexTools discord know about this. Include this value and what piece of gear you were editing. Thanks!"));
					modtype.getItems().add(String.format("%s - Unknown (see tooltip)", a));
					modtype.getSelectionModel().select(modtype.getItems().indexOf(String.format("%s - Unknown (see tooltip)", a)));
				}
				break;
		}
		updating = false;
	}
	
	
	public void setColorGroup(ColorGroup cg) {
		if(cg == null)
			return;
		updating = true;
		file.setGroup(currentSet, cg);
		regular.setColor(cg.getColorAt(0));
		metallic.setColor(cg.getColorAt(1));
		glow.setColor(cg.getColorAt(2));
		four.setColor(cg.getColorAt(3));
		renderColors();
		updating = false;
	}
	
	public boolean closeRequested ( ColorSetEditor window ) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Closing File");
		alert.setHeaderText("Save before closing?");
		ButtonType btnSave = new ButtonType("Save");
		ButtonType btnDiscard = new ButtonType("Discard");
		alert.getButtonTypes().setAll(btnSave, btnDiscard, ButtonType.CANCEL);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == btnSave)
			return save(window);
		else
			return result.get() == btnDiscard;
	}
	
	public boolean save ( ColorSetEditor window ) {
		return save(window, true);
	}
	
	public boolean save ( ColorSetEditor window, boolean displaySuccess ) {
		try {
			file.write();
			if (datfile != null)
				datfile.write();
			if (displaySuccess)
				window.showInfo("Success", "File saved successfully.", "");
			return true;
		} catch (Exception e) {
			window.showError("Error Saving File", "There was an error while trying to save the file.", e.getMessage());
			return false;
		}
	}
	
	public boolean isChanged () {
		return file.isChanged() || (datfile != null && datfile.isChanged());
	}
	
	private void initCanvas () {
		canvas = new Canvas(5 * CELL_SIZE + 5 * LINE_WIDTH, 16 * CELL_SIZE + 17 * LINE_WIDTH);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		renderColors();
		gc.setStroke(Color.LIGHTGRAY);
		gc.setLineWidth(LINE_WIDTH);
		gc.setFill(Color.BLACK);
		gc.setTextAlign(TextAlignment.CENTER);
		for (int i = 0; i < 5; i++) {
			gc.strokeLine(LINE_WIDTH * (i + 0.5) + CELL_SIZE * (i + 1), 0, LINE_WIDTH * (i + 0.5) + CELL_SIZE * (i + 1), 16 * CELL_SIZE + 17 * LINE_WIDTH);
		}
		for (int i = 0; i < 17; i++) {
			gc.strokeLine(CELL_SIZE + LINE_WIDTH * 0.5, LINE_WIDTH * (i + 0.5) + CELL_SIZE * i, 5 * CELL_SIZE + 5 * LINE_WIDTH, LINE_WIDTH * (i + 0.5) + CELL_SIZE * i);
			if (i != 16)
				gc.fillText("" + (i + 1), CELL_SIZE / 2, (CELL_SIZE + LINE_WIDTH) * (i + 0.7));
		}
		canvas.setOnMouseClicked(this::canvasClicked);
	}
	
	private void renderColors () {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 4; j++) {
				gc.setFill(file.getGroup(i).getColorAt(j).getOpaqueColor());
				gc.fillRect(LINE_WIDTH * (j + 1) + CELL_SIZE * (j + 1), LINE_WIDTH * (i + 1) + CELL_SIZE * i, CELL_SIZE, CELL_SIZE);
			}
		}
	}
	
	private void canvasClicked ( MouseEvent e ) {
		int old = currentSet;
		int current = Math.max(0, (int) Math.floor((e.getY() - LINE_WIDTH) / (CELL_SIZE + LINE_WIDTH)));
		activateSet(old, current);
	}
	
	public ColorGroup getCurrentGroup () {
		return file.getGroup(currentSet);
	}
	
	public DatGroup getCurrentDatGroup () {
		return datfile.getGroup(currentSet);
	}
	
	private void activateSet ( int old, int current ) {
		currentSet = current;
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (old != -1) {
			gc.setStroke(Color.LIGHTGRAY);
			gc.setLineWidth(LINE_WIDTH);
			gc.strokeRect(LINE_WIDTH / 2 + CELL_SIZE, LINE_WIDTH * (old + 0.5) + CELL_SIZE * old, 4 * (CELL_SIZE + LINE_WIDTH), CELL_SIZE + LINE_WIDTH);
		}
		gc.setStroke(Color.BLACK);
		gc.strokeRect(LINE_WIDTH / 2 + CELL_SIZE, LINE_WIDTH * (current + 0.5) + CELL_SIZE * current, 4 * (CELL_SIZE + LINE_WIDTH), CELL_SIZE + LINE_WIDTH);
		
		
		ColorGroup g = file.getGroup(current);
		setColorGroup(g);
		if (datfile != null) {
			updating = true;
			setDatGroup(datfile.getGroup(currentSet));
			updating = false;
		}
	}
	
	public void colorChanged ( int type ) {
		if (!updating) {
			switch (type) {
				case 0:
					file.getGroup(currentSet).setColorAt(0, regular.getColor());
					break;
				case 1:
					file.getGroup(currentSet).setColorAt(1, metallic.getColor());
					break;
				case 2:
					file.getGroup(currentSet).setColorAt(2, glow.getColor());
					break;
				case 3:
					file.getGroup(currentSet).setColorAt(3, four.getColor());
					break;
			}
			file.change();
			renderColors();
		}
	}
	
	public MtrlFileDesc getFileDescription () {
		return file.getDescription();
	}
}
