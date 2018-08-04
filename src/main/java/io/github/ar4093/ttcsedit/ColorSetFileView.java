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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ColorSetFileView extends HBox {
	private ColorView regular, metallic, glow, four;
	//private Spinner<Integer> colormod, spec1, spec2, spec;
	//private ChoiceBox<String> modtype;
	private ChoiceBox<String> mod_type, mod_color;
	private TextField manual_value;
	private Label lbl_row;
	private ColorsetFile file;
	private ColorsetDatFile datfile;
	private Canvas canvas;
	private double LINE_WIDTH = 2;
	private double CELL_SIZE = 30;
	private int currentSet = -1;
	private boolean updating = false;
	private List<String> known_modvalues = Arrays.asList(
		"0B19", "4B1A", "0B32", "4B33", "8B3E", "CB3F", "0B41", "0B4B",
		"2B19", "2B1A", "AB25", "2B32", "6B33", "AB3E", "2B41", "2B4B",
		"4B19", "8B1A", "CB25", "4B32", "8B33", "CB3E", "4B41", "4B4B",
		"0F40", "0F41", "CF3F", "4F41", "CB44", "3B41", "891A", "8B43",
		"0000"
	);
	private List<String> other_desc = Arrays.asList("A lot lighter, glowy", "Lighter, glowy", "Lighter, glowy", "A lot lighter, glowy", "Light Gold", "Black or a lot darker", "Lighter", "Silver");
	
	private int[] colrows = {0, 25, 49, 71, 89, 108, 124, 140, 156, 172, 187, 200, 216, 227, 243, 255};
	
	public ColorSetFileView ( ColorsetFile file, ColorsetDatFile datfile ) {
		setAlignment(Pos.CENTER);
		regular = new ColorView(0, this);
		metallic = new ColorView(1, this);
		glow = new ColorView(2, this);
		four = new ColorView(3, this);
		this.file = file;
		this.datfile = datfile;
		lbl_row = new Label();
		lbl_row.setTextAlignment(TextAlignment.CENTER);
		lbl_row.setTextFill(Color.DARKBLUE);
		HBox hb = new HBox();
		hb.getChildren().add(lbl_row);
		hb.setAlignment(Pos.CENTER);
		VBox cset = new VBox();
		cset.getChildren().addAll(hb,regular, metallic, glow, four);
		cset.setAlignment(Pos.CENTER_LEFT);
		if (datfile != null) {
			GridPane datview = new GridPane();
			Label label1 = new Label("Dye Modifier");
			label1.setPadding(new Insets(5));
			label1.setMinWidth(100);
			datview.add(label1, 0, 0);
			Button btnCopyDat = new Button("Copy Dye Modifier");
			Button btnPasteDat = new Button("Paste Dye Modifier");
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
			mod_type = new ChoiceBox<>();
			mod_color = new ChoiceBox<>();
			manual_value = new TextField();
			mod_type.setMinWidth(120);
			mod_color.setMinWidth(120);
			manual_value.setMinWidth(120);
			mod_type.setMaxWidth(120);
			mod_color.setMaxWidth(120);
			manual_value.setMaxWidth(120);
			manual_value.setVisible(false);
			datview.add(mod_type, 1, 0);
			datview.add(mod_color, 2, 0);
			datview.add(manual_value, 2, 0);
			datview.add(btnCopyDat, 1, 1);
			datview.add(btnPasteDat, 2, 1);
			datview.setAlignment(Pos.CENTER_LEFT);
			datview.setPadding(new Insets(5, 0, 0, 0));
			datview.setHgap(10);
			datview.setVgap(10);
			cset.getChildren().add(datview);
			mod_color.getItems().setAll("-");
			mod_type.getItems().setAll("Default", "Lighter", "Darker", "Undyed", "Other", "Manual");
			mod_type.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
				manual_value.setVisible(false);
				mod_color.setVisible(true);
				mod_color.setDisable(false);
				switch(n) {
					case "Default":
						mod_color.getItems().setAll(known_modvalues.subList(0,8));
						mod_color.setTooltip(null);
						break;
					case "Darker":
						mod_color.getItems().setAll(known_modvalues.subList(8,16));
						mod_color.setTooltip(null);
						break;
					case "Lighter":
						mod_color.getItems().setAll(known_modvalues.subList(16,24));
						mod_color.setTooltip(null);
						break;
					case "Other":
						mod_color.getItems().setAll(known_modvalues.subList(24,32));
						StringBuilder sb = new StringBuilder();
						sb.append("Known values: ");
						for(int i=0; i<mod_color.getItems().size(); i++)
							sb.append("\n").append(known_modvalues.get(24+i)).append(" - ").append(other_desc.get(i));
						mod_color.setTooltip(new Tooltip(sb.toString()));
						break;
					case "Undyed":
						mod_color.getItems().setAll("0000");
						mod_color.setDisable(true);
						setDatGroup(new DatGroup("0000"));
						mod_color.setTooltip(null);
						break;
					case "Manual":
						mod_color.getItems().setAll(" ");
						mod_color.setVisible(false);
						manual_value.setVisible(true);
						mod_color.setTooltip(null);
						break;
				}
			});
			mod_color.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
				setDatGroup(new DatGroup(n));
			});
			manual_value.focusedProperty().addListener((ov,o,n) -> {
				if(!n) {
					if (manual_value.getText().length() != 4)
						manual_value.setStyle("-fx-text-fill: red");
					else {
						manual_value.setStyle("-fx-text-fill: black");
						setDatGroup(new DatGroup(manual_value.getText()));
					}
				}
			});
			manual_value.setOnAction(e -> {
				if (manual_value.getText().length() != 4)
					manual_value.setStyle("-fx-text-fill: red");
				else {
					manual_value.setStyle("-fx-text-fill: black");
					setDatGroup(new DatGroup(manual_value.getText()));
				}
			});
		}
		initCanvas();
		activateSet(-1, 0);
		getChildren().add(canvas);
		getChildren().add(cset);
		
	}
	
	private void setDatGroup ( DatGroup dg ) {
		if (dg == null)
			return;
		updating = true;
		datfile.setGroup(currentSet, dg);
		String ds = dg.asString();
		if(!known_modvalues.contains(ds)) {
			mod_type.getSelectionModel().select("Manual");
			manual_value.setText(ds);
			System.out.println("Not found: "+ds);
		}
		else {
			int g = known_modvalues.indexOf(ds) / 8;
			switch(g) {
				case 0:
					mod_type.getSelectionModel().select("Default");break;
				case 1:
					mod_type.getSelectionModel().select("Darker");break;
				case 2:
					mod_type.getSelectionModel().select("Lighter");break;
				case 3:
					mod_type.getSelectionModel().select("Other");break;
				case 4:
					mod_type.getSelectionModel().select("Undyed");break;
			}
			mod_color.getSelectionModel().select(ds);
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
		lbl_row.setText(String.format("      Editing row #%d – Corresponding normal map alpha: %d (%d%%)", current+1, colrows[current],(int)((100f*colrows[current])/255f)));
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
