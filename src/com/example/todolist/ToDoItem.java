package com.example.todolist;

import java.io.Serializable;

/*
 * Class used to contain the basic information
 * in a single item of a to do list.
 */
public class ToDoItem implements Serializable{
/**
    * <pre>
    *           0..*     1..1
    * ToDoItem ------------------------- ToDoList
    *           toDoItem        &lt;       toDoList
    * </pre>
    */
   private ToDoList toDoList;
   
   public void setToDoList(ToDoList value) {
      this.toDoList = value;
   }
   
   public ToDoList getToDoList() {
      return this.toDoList;
   }
   
	
	private String text;
	private Boolean done;
	
	public ToDoItem(String t, Boolean d){
		this.text = t;
		this.done = d;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

}
