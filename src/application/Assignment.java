package application;

import java.io.Serializable;

public class Assignment implements Serializable {

	private static final long serialVersionUID = 1L;
	public String title;
	public String description;
	public String dueDate;
	
	public Assignment(String title, String dueDate, String description){
		this.title=title;
		this.description=description;
		this.dueDate=dueDate;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title){
		this.title=title;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String description){
		this.description=description;
	}
	
	public void setDueDate(String dueDate){
		this.dueDate=dueDate;
	}
	
	public String getDueDate(){
		return dueDate;
	}
	
	public boolean equals(Object o) {
	      return (o instanceof Assignment) && (((Assignment) o).getTitle()).equals(this.getTitle());
	}
	@Override
    public int hashCode() {
        return title.hashCode();
    }
}
