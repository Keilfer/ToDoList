package com.example.todolist;
import java.util.HashSet;
import java.util.Set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/*
 * Class used to contain a set of ToDoItems and access
 * their information. This class is the basic data structure
 * representing an entire to do list in the app, and so also
 * contains the name of the list.
 */
public class ToDoList extends Vector<ToDoItem> {
/**
    * <pre>
    *           1..1     0..*
    * ToDoList ------------------------- ToDoItem
    *           toDoList        &gt;       toDoItem
    * </pre>
    */
   private Set<ToDoItem> toDoItem;
   
   public Set<ToDoItem> getToDoItem() {
      if (this.toDoItem == null) {
         this.toDoItem = new HashSet<ToDoItem>();
      }
      return this.toDoItem;
   }
   
   /**
    * <pre>
    *           0..*     1..1
    * ToDoList ------------------------- ToDoManager
    *           toDoList        &lt;       toDoManager
    * </pre>
    */
   private ToDoManager toDoManager;
   
   public void setToDoManager(ToDoManager value) {
      this.toDoManager = value;
   }
   
   public ToDoManager getToDoManager() {
      return this.toDoManager;
   }
   
	
	private String listName;
	
	ToDoList(String n){
		this.listName = n;
	}

	public void setItemName(int index, String newValue){
		this.get(index).setText(newValue);
	}
	
	public void setDone(int index, Boolean newValue){
		this.get(index).setDone(newValue);
	}
	
	public void addItem(String text, Boolean done){
		add(new ToDoItem(text, done));
	}
	
	public void addItem(String text){
		add(new ToDoItem(text, false));
	}
	
	public String getItemName(int index){
		return this.get(index).getText();
	}
	
	public Boolean getDone(int index){
		return this.get(index).getDone();
	}

	public String getName() {
		return listName;
	}
	
	/*
	 * Returns the contents of this class as an ArrayList to use
	 * in an ArrayAdapter.
	 */
	public ArrayList<ToDoItem> getItems(){
		ToDoItem items[] = new ToDoItem[this.size()];
		return new ArrayList<ToDoItem>(Arrays.asList(this.toArray(items)));
	}

}
