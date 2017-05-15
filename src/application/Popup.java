package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Popup extends Stage {
	private VBox poproot = new VBox();
	private Scene popup=new Scene(poproot,300,176);
	private List<TextField> tf=new ArrayList<TextField>();
	private Button cl;
	private Button sb;
	private HBox bottom;
	
	Popup(){
		this.setResizable(false);
		this.setScene(popup);
		this.initModality(Modality.APPLICATION_MODAL);
		popup.getStylesheets().add(getClass().getResource("popup.css").toExternalForm());
		poproot.getChildren().addAll(tf);
		cl=new Button("Clear");
		sb=new Button("Submit");
		sb.setDisable(true);		
		cl.setPrefWidth(150);
		sb.setPrefWidth(150);
		
		this.setOnShowing(e->{
			if(tf.get(0)!=null)
			tf.get(0).requestFocus();
		});
		
		popup.setOnKeyPressed(e->{
			if(e.getCode().equals(KeyCode.ENTER) && !sb.isDisabled()){
				sb.fire();
			}
		});
		
		cl.setOnAction(e->{
			clear();
		});		
		
		bottom=new HBox();
		bottom.getChildren().addAll(sb,cl);
	}
	
	Popup(String title){
		this();
		this.setTitle(title);
	}
	
	Popup(String title, Class<?> T){
		this(title);
				
		TextField fn=new TextField();
		TextField ln=new TextField();
		TextField ps=new TextField();
		
		if(T.equals(Person.class)){
		fn.setPromptText("First Name");
		ln.setPromptText("Last Name");
		ps.setPromptText("Notes");
		}else if(T.equals(Assignment.class)){
		fn.setPromptText("Title");
		ln.setPromptText("Due Date");
		ps.setPromptText("Description");
		}
		
		tf.add(fn);
		tf.add(ln);
		tf.add(ps);
		
		fn.textProperty().addListener((o,ov,nv)->{
			sb.setDisable(fn.getText().isEmpty()||ln.getText().isEmpty());
		});
		ln.textProperty().addListener((o,ov,nv)->{
			sb.setDisable(fn.getText().isEmpty()||ln.getText().isEmpty());
		});
		
		poproot.getChildren().addAll(fn,ln,ps,bottom);
		
		sb.setOnAction(e->{
			if(T.equals(Person.class))
			Main.people.add(new Person(fn.getText(),ln.getText(),ps.getText()));
			else if(T.equals(Assignment.class)){
			Main.asnmts.add(new Assignment(fn.getText(),ln.getText(),ps.getText()));
			}
			Main.assignTable();
			Main.refreshTable();
			Main.changeFile();
			this.hide();
		});
	}
	
	

	public Popup(String string, Person row) {
		this(string,row.getClass());
		
		tf.get(0).setText(row.getFn());
		tf.get(1).setText(row.getLn());
		tf.get(2).setText(row.getPs());
		
		sb.setOnAction(e->{
			row.setFn(tf.get(0).getText());
			row.setLn(tf.get(1).getText());
			row.setPs(tf.get(2).getText());
			Main.assignTable();
			Main.refreshTable();
			Main.changeFile();
			this.hide();
		});
	}
	
	public Popup(String string, Assignment asnmt) {
		this(string,asnmt.getClass());
		
		tf.get(0).setText(asnmt.getTitle());
		tf.get(1).setText(asnmt.getDueDate());
		tf.get(2).setText(asnmt.getDescription());
		
		sb.setOnAction(e->{
			asnmt.setTitle(tf.get(0).getText());
			asnmt.setDueDate(tf.get(1).getText());
			asnmt.setDescription(tf.get(2).getText());
			Main.assignTable();
			Main.refreshTable();
			Main.changeFile();
			this.hide();
		});
	}

	public void clear(){
		Iterator<TextField> it=tf.iterator();
		while(it.hasNext()){
			it.next().clear();
		}
	}
}
