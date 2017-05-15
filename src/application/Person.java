package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Person implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Assignment> completed=new ArrayList<Assignment>();
	private String fn;
    private String ln;
    private String ps;
	
    public Person(String fn, String ln){
		this.fn=fn;
		this.ln=ln;
    }
    
	public Person(String fn, String ln, String ps) {
		this(fn,ln);
		this.ps=ps;
	}
	
	public String toString(){
		return getFullName()+" - "+completed.size();
	}
	
	public String getFullName(){
		return fn+" "+ln;
	}

	public String getFn() {
		return fn;
	}
	
	public String getLn() {
		return ln;
	}

	public void setFn(String fn) {
		this.fn = fn;
	}
	
	public void setLn(String ln) {
		this.ln = ln;
	}
	
	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	public boolean didAsnmt(Assignment asnmt){
		return completed.contains(asnmt);
	}
	
	public int getDone(){
		return completed.size();
	}
	
	public void completed(Assignment todo) {
		completed.add(todo);
	}

	public void toggleComplete(Assignment asnmt) {
		if(completed.contains(asnmt))completed.remove(asnmt);
		else completed.add(asnmt);
	}

	public void removeAsnmt(Assignment asnmt) {
		completed.remove(asnmt);
		
	}
}
