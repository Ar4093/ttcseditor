package io.github.ar4093.ttcsedit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

public class ColorSetEditor extends Application {
	private static final String VERSION = "2.3";
	private static final String WINDOW_TITLE_BASE = "TexTools Colour Set Editor";
	private String savePath = "";
	private Stage window;
	private TabPane tabpane;
	private TreeView<io.github.ar4093.ttcsedit.MtrlFileDesc> files;
	static boolean DEBUG = false;
	static boolean DEBUG2 = false;
	static BufferedWriter logwriter;
	
	public static void main ( String[] args ) {
		if(DEBUG || DEBUG2) {
			try {
				logwriter = new BufferedWriter(new FileWriter(new File("colorseteditor.log")));
				logwriter.write("Starting up...\nOS: "+System.getProperty("os.name")+"\n");
				logwriter.write("Home directory: "+System.getProperty("user.home")+"\n");
				logwriter.flush();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		launch(args);
	}
	
	public void showError ( String title, String header, String body ) {
		if(DEBUG) { try { logwriter.write("[ERROR] "+title+" | "+header+" | "+body+"\n");logwriter.flush(); }catch(Exception e) {e.printStackTrace();} }
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(body);
		alert.getButtonTypes().setAll(ButtonType.OK);
		alert.showAndWait();
	}
	
	public void showException(String title, String header, String body, Exception ex) {
		if(DEBUG) {try { logwriter.write("[EXCEPTION] "+title+" | "+header+" | "+body+" | "+ex.getMessage()+" | "+ex.getStackTrace().toString()+"\n");logwriter.flush(); }catch(Exception e) {e.printStackTrace();}}
		Alert alert = new Alert(Alert.AlertType.ERROR);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exText = sw.toString();
		if(header == null || header == "")
			alert.setHeaderText(ex.getMessage());
		else
			alert.setHeaderText(header);
		alert.setTitle(title);
		VBox box = new VBox();
		Label l0 = new Label(body);
		Label l = new Label("The error's stack trace was:");
		TextArea ta = new TextArea(exText);
		ta.setEditable(false);
		ta.setWrapText(true);
		ta.setMaxWidth(Double.MAX_VALUE);
		ta.setMaxHeight(Double.MAX_VALUE);
		VBox.setVgrow(ta, Priority.ALWAYS);
		VBox.setVgrow(ta, Priority.ALWAYS);
		box.getChildren().addAll(l0, l, ta);
		alert.getDialogPane().setExpandableContent(box);
		alert.getButtonTypes().setAll(ButtonType.OK);
		alert.showAndWait();
	}
	
	public void showInfo ( String title, String header, String body ) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(body);
		alert.getButtonTypes().setAll(ButtonType.OK);
		alert.showAndWait();
	}
	
	public void showAbout () {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About TexTools Colourset Editor");
		alert.setContentText("TexTools Colourset Editor, Version " + VERSION + "\n\nCreated by Ar4093\nDiscord: SifridExtan#2581\n\nThanks to Liinko and Liz for insights on file formats and values.\nFor more info about the values in this program, look in the Reference Sheet in the modding-tools channel on the TexTools discord.\n\nFor help or suggestions, message me on Discord.\n\n\n" + "Quick Help:\nThe left column displays all the colourset files found in the TexTools Saved folder. If it's empty, you need to export some files from TexTools first.\nHover over a file description (e.g. Midlander ♂) to see the file name. Double-click it to open the file.\nNon-obvious values have tooltips.\nClick on a row in the colourset to edit/show it.\nThe colour squares have a context menu to copy/paste values.\nCopy/Paste Group copies all four colours at once. Dye modifiers don't get copied.\nColours, Groups and Modifiers can be copied once each in parallel.");
		alert.getButtonTypes().setAll(ButtonType.OK);
		alert.showAndWait();
	}
	
	private void getSavedPath () {
		File cfgfile = new File("colorseteditor.cfg");
		if(cfgfile.exists()) {
			if(cfgfile.canRead()) {
				try {
					String cfgline = new BufferedReader(new InputStreamReader(new FileInputStream(cfgfile))).readLine();
					savePath = cfgline.substring(cfgline.indexOf(":")+1);
					return;
				} catch(Exception e) {
					showException("Error reading config file.", "", "", e);
				}
			}
		}
		else {
			changeSavePath();
		}
	
		if(DEBUG) {try { logwriter.write("getSavedPath: savePath = "+savePath);logwriter.flush(); }catch(Exception e){e.printStackTrace();}}
	}
	
	private void writeSavePath() {
		File cfgfile = new File("colorseteditor.cfg");
		if(!cfgfile.exists()) {
			try {
				if(!cfgfile.createNewFile()) {
					showError("Config file not writable","Can't write to the config file.", "Make sure you have write access to the directory this jar file is in or run it as administrator.");
					return;
				}
			} catch(Exception e) {
				showException("Error creating config file", "There was an error trying to creating the config file.","",e);
				return;
			}
		}
		if(cfgfile.canWrite()) {
			try {
				BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cfgfile)));
				w.write("savePath:"+savePath);
				w.flush();
				w.close();
			} catch(Exception e) {
				showException("Error writing config file", "There was an error trying to write the config file.","",e);
			}
		} else {
			showError("Config file not writable","Can't write to the config file.", "Make sure you have write access to the directory this jar file is in or run it as administrator.");
		}
	}
	
	@Override
	public void start ( Stage primaryStage ) {
		new Clipboard();
		window = primaryStage;
		window.setTitle(WINDOW_TITLE_BASE);
		getSavedPath();
		
		ToolBar toolbar = new ToolBar();
		Pane spring = new Pane();
		HBox.setHgrow(spring, Priority.ALWAYS);
		Button mbSave = new Button("Save");
		mbSave.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("media-floppy.png"))));
		mbSave.setContentDisplay(ContentDisplay.TOP);
		Button mbSaveAll = new Button("Save All");
		mbSaveAll.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("media-floppy-plus.png"))));
		mbSaveAll.setContentDisplay(ContentDisplay.TOP);
		Button mbRevertFile = new Button("Revert File");
		mbRevertFile.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("edit-undo.png"))));
		mbRevertFile.setContentDisplay(ContentDisplay.TOP);
		Button mbExit = new Button("Exit");
		mbExit.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("system-log-out.png"))));
		mbExit.setContentDisplay(ContentDisplay.TOP);
		Button mbCopyGroup = new Button("Copy Group");
		mbCopyGroup.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("edit-copy.png"))));
		mbCopyGroup.setContentDisplay(ContentDisplay.TOP);
		Button mbPasteGroup = new Button("Paste Group");
		mbPasteGroup.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("edit-paste.png"))));
		mbPasteGroup.setContentDisplay(ContentDisplay.TOP);
		Button mbAbout = new Button("About");
		mbAbout.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("help-browser.png"))));
		mbAbout.setContentDisplay(ContentDisplay.TOP);
		Button mbSetSavedPath = new Button("Change Folder");
		mbSetSavedPath.setTooltip(new Tooltip("Locate the \"Saved\" directory manually."));
		mbSetSavedPath.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("folder-new.png"))));
		mbSetSavedPath.setContentDisplay(ContentDisplay.TOP);
		toolbar.getItems().addAll(mbSave, mbSaveAll, new Separator(), mbCopyGroup, mbPasteGroup, new Separator(), mbRevertFile,mbSetSavedPath, spring, mbAbout, new Separator(), mbExit);
		
		mbSave.setOnAction(e -> {
			((io.github.ar4093.ttcsedit.ColorSetFileView) (tabpane.getSelectionModel().getSelectedItem().getContent())).save(this);
			
			if(DEBUG) {try { logwriter.write("[DEBUG] requested save by mbSave\n");logwriter.flush(); }catch(Exception ex){ex.printStackTrace();}}
		});
		mbSaveAll.setOnAction(e -> {
			boolean success = true;
			for (Tab t : tabpane.getTabs()) {
				success = success && ((io.github.ar4093.ttcsedit.ColorSetFileView) t.getContent()).save(this, false);
			}
			if (success) {
				showInfo("Success", "Files saved successfully.", "");
			}
			
			if(DEBUG) {try { logwriter.write("[DEBUG] requested save all by mbSaveAll. Succes: "+success+"\n");logwriter.flush(); }catch(Exception ex){ex.printStackTrace();}}
		});
		mbRevertFile.setOnAction(evt -> {
			
			if(DEBUG) {try { logwriter.write("[DEBUG] requested revert file by mbRevertFile\n");logwriter.flush(); }catch(Exception e){e.printStackTrace();}}
			Alert a = new Alert(Alert.AlertType.CONFIRMATION);
			a.setTitle("Revert file");
			a.setHeaderText("Discard all changes to this file?");
			a.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
			Optional<ButtonType> result = a.showAndWait();
			if(!result.isPresent() || result.get() != ButtonType.YES)
				return;
			io.github.ar4093.ttcsedit.ColorSetFileView fv = (io.github.ar4093.ttcsedit.ColorSetFileView) tabpane.getSelectionModel().getSelectedItem().getContent();
			io.github.ar4093.ttcsedit.MtrlFileDesc d = fv.getFileDescription();
			try {
				io.github.ar4093.ttcsedit.ColorsetFile cf = new io.github.ar4093.ttcsedit.ColorsetFile(d);
				io.github.ar4093.ttcsedit.ColorsetDatFile cdf = null;
				if(d.getDatPath()!=null)
					 cdf = new io.github.ar4093.ttcsedit.ColorsetDatFile(d);
				tabpane.getSelectionModel().getSelectedItem().setContent(new io.github.ar4093.ttcsedit.ColorSetFileView(cf, cdf));
			}catch(IOException e) {
				showException("Error reverting file","","",e);
			}
		});
		mbExit.setOnAction(this::closeRequest);
		window.setOnCloseRequest(this::closeRequest);
		mbCopyGroup.setOnAction(e -> {
			if(tabpane.getTabs().isEmpty())
				e.consume();
			else {
				mbPasteGroup.setDisable(false);
				io.github.ar4093.ttcsedit.ColorSetFileView fv = (io.github.ar4093.ttcsedit.ColorSetFileView) tabpane.getSelectionModel().getSelectedItem().getContent();
				io.github.ar4093.ttcsedit.Clipboard.setGroup(fv.getCurrentGroup());
			}
		});
		mbPasteGroup.setDisable(true);
		mbPasteGroup.setOnAction(e -> {
			if(!tabpane.getTabs().isEmpty()) {
				io.github.ar4093.ttcsedit.ColorSetFileView fv = (io.github.ar4093.ttcsedit.ColorSetFileView) tabpane.getSelectionModel().getSelectedItem().getContent();
				fv.setColorGroup(io.github.ar4093.ttcsedit.Clipboard.getGroup());
			}
		});
		mbAbout.setOnAction(e -> showAbout());
		mbSetSavedPath.setOnAction(e -> changeSavePath());
		
		BorderPane root = new BorderPane();
		root.setTop(toolbar);
		
		HBox mainlayout = new HBox();
		root.setCenter(mainlayout);
		
		files = new TreeView<>();
		MenuItem miRFT = new MenuItem("Reload File Tree");
		miRFT.setOnAction(e -> buildTree());
		files.setContextMenu(new ContextMenu(miRFT));
		files.setMinWidth(300);
		buildTree();
		/*if(files.getRoot().getChildren().size() == 0) {
			files.getRoot().getChildren().add(new TreeItem<>(new io.github.ar4093.ttcsedit.MtrlFileDesc()));
		}
		else {*/
			files.setCellFactory(tv -> {
				final Tooltip tooltip = new Tooltip();
				TreeCell<io.github.ar4093.ttcsedit.MtrlFileDesc> cell = new TreeCell<io.github.ar4093.ttcsedit.MtrlFileDesc>() {
					@Override
					public void updateItem (io.github.ar4093.ttcsedit.MtrlFileDesc item, boolean empty ) {
						super.updateItem(item, empty);
						if (empty) {
							setText(null);
							setTooltip(null);
						} else if (item.isDirectory()) {
							setText(item.toString());
							setTooltip(null);
						} else {
							setText(item.toString());
							tooltip.setText(item.getTooltip());
							setTooltip(tooltip);
						}
					}
				};
				cell.setOnMouseClicked(e -> {
					if (e.getClickCount() == 2 && !cell.isEmpty() && !cell.getItem().isDirectory()) {
						openFile(cell.getItem());
					}
				});
				return cell;
			});
		//}
		HBox.setHgrow(files, Priority.ALWAYS);
		mainlayout.getChildren().add(files);
		
		tabpane = new TabPane();
		tabpane.setMinWidth(650);
		tabpane.setMaxWidth(650);
		tabpane.setPrefWidth(650);
		HBox.setHgrow(tabpane, Priority.NEVER);
		tabpane.getSelectionModel().selectedItemProperty().addListener(( ov, oldTab, newTab ) -> {
			if (newTab == null)
				window.setTitle(WINDOW_TITLE_BASE);
			else
				window.setTitle(((io.github.ar4093.ttcsedit.ColorSetFileView) (newTab.getContent())).getFileDescription().getFullTitle() + " - " + WINDOW_TITLE_BASE);
		});
		mainlayout.getChildren().add(tabpane);
		
		Scene scene = new Scene(root, 950, 630);
		toolbar.getStylesheets().add(getClass().getResource("toolbar.css").toExternalForm());
		window.setScene(scene);
		window.sizeToScene();
		window.show();
	}
	
	private void changeSavePath() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Choose the\"Saved\" folder to look for files in.");
		if(!(savePath==null||savePath.isEmpty())&&(new File(savePath).exists()))
			dc.setInitialDirectory(new File(savePath));
		File f = dc.showDialog(window);
		if(f.exists()) {
			savePath = f.getAbsolutePath();
			writeSavePath();
			buildTree();
		} else {
			showError("Directory not found", "Your chosen directory doesn't exist.","The directory "+f.getAbsolutePath()+" doesn't exist. Please choose another.");
		}
	}
	
	private void closeRequest( Event evt) {
		
		if(DEBUG) {try { logwriter.write("[DEBUG] request close\n");logwriter.flush(); }catch(Exception e){e.printStackTrace();}}
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Exiting");
		alert.setHeaderText("Save changes before closing?");
		ButtonType btnSave = new ButtonType("Save");
		ButtonType btnDiscard = new ButtonType("Discard");
		alert.getButtonTypes().setAll(btnSave, btnDiscard, ButtonType.CANCEL);
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.get() == btnSave) {
			boolean success = true;
			for (Tab t : tabpane.getTabs()) {
				success = success && ((io.github.ar4093.ttcsedit.ColorSetFileView) t.getContent()).save(this, false);
			}
			if (success) {
				showInfo("Success", "Files saved successfully.", "");
			}
		}
		else if(result.get() == ButtonType.CANCEL) {
			evt.consume();
			return;
		}
		Platform.exit();
		
	}
	
	private void openFile ( io.github.ar4093.ttcsedit.MtrlFileDesc desc ) {
		try {
			
			if(DEBUG) {try { logwriter.write("[DEBUG] opening file: "+desc.getPath()+"\n");logwriter.flush(); }catch(Exception e){e.printStackTrace();}}
			Tab tab = new Tab(desc.getTabTitle());
			tab.setTooltip(new Tooltip(desc.getFolderName()));
			tab.setContent(new io.github.ar4093.ttcsedit.ColorSetFileView(new io.github.ar4093.ttcsedit.ColorsetFile(desc), desc.getDatPath() == null ? null : new io.github.ar4093.ttcsedit.ColorsetDatFile(desc)));
			tabpane.getTabs().add(tab);
			tab.setOnCloseRequest(e -> {
				if (((io.github.ar4093.ttcsedit.ColorSetFileView) tab.getContent()).isChanged()) {
					boolean close = ((io.github.ar4093.ttcsedit.ColorSetFileView) tab.getContent()).closeRequested(this);
					if (!close)
						e.consume();
				}
			});
		} catch (Exception e) {
			showException("Error opening file","", "", e);
		}
	}
	
	private void buildTree () {
		if(DEBUG) {try { logwriter.write("[DEBUG] Building tree.\n"); }catch(Exception e){e.printStackTrace();}}
		if (files == null)
			return;
		TreeItem<io.github.ar4093.ttcsedit.MtrlFileDesc> root = dirTree(new File(savePath));
		root.setExpanded(true);
		files.setRoot(root);
		if(files.getRoot().getChildren().isEmpty() || (files.getRoot().getChildren().size() == 1 && files.getRoot().getChildren().get(0).getValue().isDummy())) {
			files.getRoot().setValue(new MtrlFileDesc());
			files.getRoot().getChildren().clear();
			files.setShowRoot(true);
			
		} else
			files.setShowRoot(false);
	}
	
	private TreeItem<io.github.ar4093.ttcsedit.MtrlFileDesc> dirTree (File directory ) {
		if(DEBUG) {try { logwriter.write("[DEBUG] dirTree: current dir:"+directory.getAbsolutePath()+"\n"); }catch(Exception e){e.printStackTrace();}}
		File[] filelist = directory.listFiles();
		TreeItem<io.github.ar4093.ttcsedit.MtrlFileDesc> fileNode = new TreeItem<io.github.ar4093.ttcsedit.MtrlFileDesc>(new io.github.ar4093.ttcsedit.MtrlFileDesc(directory));
		if (filelist == null || filelist.length == 0) {
			fileNode.getValue().setEmpty(true);
			return fileNode;
		}
		fileNode.getValue().setEmpty(false);
		for (File f : filelist) {
			if (f.isDirectory()) {
				TreeItem<io.github.ar4093.ttcsedit.MtrlFileDesc> child = dirTree(f);
				if (!(child.getValue().isEmpty()||child.getValue().isDummy())) {
					fileNode.getChildren().add(child);
				}
			} else if (f.isFile()) {
				if(DEBUG) {try { logwriter.write("[DEBUG] Found file: "+f.getAbsolutePath()); }catch(Exception e){e.printStackTrace();}}
				if (f.getName().startsWith("mt_") && f.getName().endsWith(".dds")) {
					try {TreeItem<io.github.ar4093.ttcsedit.MtrlFileDesc> fileItem = new TreeItem<io.github.ar4093.ttcsedit.MtrlFileDesc>(new io.github.ar4093.ttcsedit.MtrlFileDesc(f));
					fileNode.getChildren().add(fileItem);
					if(DEBUG) {try { logwriter.write(" - added to list.\n");logwriter.flush(); }catch(Exception e){e.printStackTrace();}} }
					catch(Exception ex) {
						showException("Error adding file to list", "", "", ex);
					}
				}
				else {
					if(DEBUG) {try { logwriter.write(" - skipped.\n");logwriter.flush(); }catch(Exception e){e.printStackTrace();}}
				}
			}
		}
		if (fileNode.getChildren().size() == 0) {
			fileNode.getValue().setEmpty(true);
		}
		return fileNode;
	}
}
