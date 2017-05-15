package application;
	
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Main extends Application {
	
	protected static Set<Person> people=new HashSet<Person>();
	protected static Set<Assignment> asnmts=new HashSet<Assignment>();
	private static TableView<Person> table = new TableView<Person>();
	private static String fileName;
	protected static boolean changed=false;
	static Stage primaryStage;
	static MenuItem saveFile;
	static MenuItem saveAsFile;
	
	@Override
	public void start(Stage primaryStage) {
		Main.primaryStage=primaryStage;
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root,400,400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		Popup popStage = new Popup("Add Person",Person.class);
		Popup apStage = new Popup("Add Assignment",Assignment.class);
							
        MenuBar menuBar = new MenuBar();
        final FileChooser fileChooser = new FileChooser();
        
        fileChooser.getExtensionFilters().addAll(
        		new FileChooser.ExtensionFilter("DAT", "*.dat"),
                new FileChooser.ExtensionFilter("All Files", "*")
        );
        
        Menu menuFile = new Menu("File");
        Menu menuEdit = new Menu("Edit");
                
        saveAsFile = new MenuItem("Save As...");
        saveAsFile.setDisable(true);
        saveAsFile.setOnAction(e->{
        	File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                saveFile(file);
            }
        });
        
        MenuItem newFile = new MenuItem("New");
        newFile.setOnAction(e->{
        	asnmts.clear();
			people.clear();
			assignTable();
			refreshTable();
			fileName=null;
			changed=false;
			primaryStage.setTitle("Track 'Em");
			saveFile.setDisable(true);
			saveAsFile.setDisable(true);
        });
        
        MenuItem openFile = new MenuItem("Open File...");
        openFile.setOnAction(e->{
        	File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                loadFile(file);
                fileName=file.getName();
                changed=false;
                primaryStage.setTitle("Track 'Em - "+fileName);
                assignTable();
                refreshTable();
                saveAsFile.setDisable(false);
            }
        });
        
        saveFile = new MenuItem("Save");
        saveFile.setDisable(true);
        saveFile.setOnAction(e->{
            if (fileName != null) {
                saveFile(new File("fileName"));
            }
            else saveAsFile.fire();
        });
        
        
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e->{
           	System.exit(0);
        });
                
        MenuItem addPerson = new MenuItem("Add Person...");
        addPerson.setOnAction(e->{
        	popStage.clear();
        	popStage.showAndWait();     	
        	table.refresh();
        });
        MenuItem addAssignment = new MenuItem("Add Assignment...");
        addAssignment.setOnAction(e->{
        	apStage.clear();
        	apStage.showAndWait();
        });
     
        menuFile.getItems().addAll(newFile, openFile, saveFile, saveAsFile, exit);
        menuEdit.getItems().addAll(addPerson,addAssignment);
        
        menuBar.getMenus().addAll(menuFile, menuEdit);
		
		Button addAsnmt=new Button("Add Assignment");
		addAsnmt.setOnAction(e->{
			addAssignment.fire();
		});
		Button addPrsn=new Button("Add Person");
		addPrsn.setOnAction(e->{
			addPerson.fire();
		});
		
		HBox aphb=new HBox();
		aphb.setStyle("-fx-background-color:#fff;-fx-spacing:1px");
		addAsnmt.setMaxWidth(Double.MAX_VALUE);
		addPrsn.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(addPrsn, Priority.ALWAYS);
		HBox.setHgrow(addAsnmt, Priority.ALWAYS);
		aphb.getChildren().addAll(addPrsn,addAsnmt);
		
		VBox cvb=new VBox();
		Button tph=new Button("Open File...");
		tph.setId("dark");
		tph.setOnAction(e->{
			openFile.fire();
		});
		Button np=new Button("Add Person");
		np.setId("dark");
		np.setOnAction(e->{
			addPerson.fire();
		});
		Button na=new Button("Add Assignment");
		na.setId("dark");
		na.setOnAction(e->{
			addAssignment.fire();
		});
		
		tph.setPrefWidth(150);
		np.setPrefWidth(150);
		na.setPrefWidth(150);
		
		cvb.setStyle("-fx-spacing:.4em;-fx-alignment:CENTER");
		cvb.getChildren().addAll(tph,np,na);
		
		table.setPlaceholder(cvb);
		
		root.setBottom(aphb);
		root.setTop(menuBar);
		root.setCenter(table);		
		
		primaryStage.setTitle("Person-Task-Tracker");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	protected static void assignTable() {
		table.getColumns().clear();
		
		if(people.size()>0){
		TableColumn<Person, String> names = new TableColumn<Person, String>("Names");
		names.setMinWidth(100);
		names.setCellValueFactory(new PropertyValueFactory<Person, String>(
                "FullName"));
		
		names.setCellFactory(new Callback<TableColumn<Person,String>,TableCell<Person,String>>(){				
				public TableCell<Person,String> call(TableColumn<Person,String> p){
					TableCell<Person,String> cell=new TableCell<Person,String>(){
						
						HBox test=new HBox();
				    	Label label=new Label();
				    	Label text=new Label();
				    	Tooltip tt=new Tooltip();
				    	
				    	{
				    	text.setMaxWidth(Double.MAX_VALUE);
						HBox.setHgrow(text, Priority.ALWAYS);
						
						text.setStyle("-fx-text-fill:#eaeaea;-fx-padding:0 0.5em 0 0");
				    	label.setId("cellLabel");						    	 
				    	test.getChildren().addAll(text,label);				    	
				    	
				    	label.setVisible(false);
				    	
				    	setGraphic(test);
				    	}
						
						protected void updateItem(String item, boolean empty) {
						     super.updateItem(item, empty);

						     if (this.getTableRow()==null) {
						         setText(null);
						     } else {
						    	 Person row=(Person) this.getTableRow().getItem();
						    	 if(row!=null){
						    	 label.setText(String.valueOf(row.getDone()));
						    	 text.setText(row.getFullName());								 
								 tt.setText(row.getFullName());
						    	 label.setVisible(true);
						    	 text.setTooltip(tt);
						    	 }
						    	 else{
						    		 label.setVisible(false);
						    		 text.setTooltip(null);
						    	 }
						     }
						 }
					};
					
					ContextMenu cm = new ContextMenu();
					
					Label editLabel=new Label("Edit");
					editLabel.setOnMouseClicked(e->{
						if (e.getButton() != MouseButton.PRIMARY) {
		                    return;
		                }
						if(cell.getTableRow() != null){
							Person row=(Person) cell.getTableRow().getItem();
							if(row!=null){
								Popup tempop = new Popup("Edit Person",row);
								tempop.showAndWait();
							}
						}
					});
					MenuItem editPerson=new CustomMenuItem(editLabel);
					
					Label deleteLabel=new Label("Remove");
					deleteLabel.setOnMouseClicked(e->{
						if (e.getButton() != MouseButton.PRIMARY) {
		                    return;
		                }
						if(cell.getTableRow() != null){
							Person row=(Person) cell.getTableRow().getItem();
							if(row!=null){
								people.remove(row);
								changeFile();
								assignTable();
								refreshTable();
							}
						}
					});
					
					MenuItem deletePerson=new CustomMenuItem(deleteLabel);
								
					cm.getItems().addAll(editPerson,deletePerson);						
					cell.setContextMenu(cm);
							
					return cell;
				}
			});
		table.getColumns().add(names);
	    }
		
		Iterator<Assignment> it=asnmts.iterator();
		while(it.hasNext()){
			Assignment asnmt=it.next();
			TableColumn<Person, String> temp=new TableColumn<Person, String>();
			
			temp.setCellValueFactory(cd->				
			new SimpleStringProperty(cd.getValue().didAsnmt(asnmt)?"0":"1")
			);
			
			Label tempLabel=new Label(asnmt.getTitle());
			tempLabel.setOnMouseClicked(e->{
				System.out.println("ayy");
			});
			temp.setGraphic(tempLabel);
			
			ContextMenu cm=new ContextMenu();
			
			Label editLabel=new Label("Edit");
			editLabel.setOnMouseClicked(e->{
				if (e.getButton() != MouseButton.PRIMARY) {
                    return;
                }
				Popup tempop=new Popup("Edit Assignment",asnmt);
				tempop.showAndWait();
			});
			MenuItem editAsnmt=new CustomMenuItem(editLabel);
			
			Label deleteLabel=new Label("Remove");
			deleteLabel.setOnMouseClicked(e->{
				if (e.getButton() != MouseButton.PRIMARY) {
                    return;
                }
				asnmts.remove(asnmt);
				Iterator<Person> pit=people.iterator();
				while(pit.hasNext()){
					pit.next().removeAsnmt(asnmt);
				}
				assignTable();
				changeFile();
			});
			
			MenuItem deleteAsnmt=new CustomMenuItem(deleteLabel);
						
			cm.getItems().addAll(editAsnmt,deleteAsnmt);
			
			temp.setContextMenu(cm);
			
			temp.setPrefWidth(100);	
			temp.setCellFactory(new Callback<TableColumn<Person,String>,TableCell<Person,String>>(){
				
				public TableCell<Person,String> call(TableColumn<Person,String> p){
					TableCell<Person,String> cell=new TableCell<Person,String>(){						
						protected void updateItem(String item, boolean empty) {
						     super.updateItem(item, empty);

						     if (this.getTableRow() != null && this.getTableRow().getItem() != null) {
						    	 boolean did=((Person) this.getTableRow().getItem()).didAsnmt(asnmt);
						         setText(did?"\u2713":"");
						         setId(did?"cellCheck":"");
						     }
						 }
					};			
					cell.setOnMousePressed(e->{
						if(e.isPrimaryButtonDown()){
						Person row=(Person) cell.getTableRow().getItem();
						if(row!=null){
					    changeFile();
						row.toggleComplete(asnmt);
						table.refresh();
						}
						}
					});
					return cell;
				}
			});
			table.getColumns().add(temp);
		}
	}

	@SuppressWarnings("unchecked")
	private static void loadFile(File file){
		try{
			FileInputStream fi=new FileInputStream(file);
			ObjectInputStream in=new ObjectInputStream(fi);
			asnmts = (HashSet<Assignment>) in.readObject();
			people = (HashSet<Person>) in.readObject();
			in.close();
			fi.close();
		}catch(IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	protected static void changeFile(){
		if(!changed){
		primaryStage.setTitle("*"+primaryStage.getTitle());
		changed=true;
		saveFile.setDisable(false);
		saveAsFile.setDisable(false);
		}
	}

	protected static void refreshTable(){
		table.setItems(FXCollections.observableArrayList(people));
	}
	
	private static void saveFile(File file) {
		try{
			FileOutputStream fo=new FileOutputStream(file);
			ObjectOutputStream out=new ObjectOutputStream(fo);
			out.writeObject(asnmts);
			out.writeObject(people);
			out.close();
			fo.close();
		}catch(IOException e){
			e.printStackTrace();			
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
