package com.example.todolist;
import java.util.HashSet;
import java.util.Set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/*
 * Class containing a set of ToDoList objects, used as the main
 * set of data accessed by the application.
 */
public class ToDoManager extends Vector<ToDoList>{
/**
    * <pre>
    *           1..1     0..*
    * ToDoManager ------------------------- ToDoList
    *           toDoManager        &gt;       toDoList
    * </pre>
    */
   private Set<ToDoList> toDoList;
   
   public Set<ToDoList> getToDoList() {
      if (this.toDoList == null) {
         this.toDoList = new HashSet<ToDoList>();
      }
      return this.toDoList;
   }
   

	public void addItem(String name){
		this.add(new ToDoList(name));
	}
	
	public String getListName(int index){
		return this.get(index).getName();
	}
	
	public ArrayList<ToDoList> getItems(){
		ToDoList items[] = new ToDoList[this.size()];
		return new ArrayList<ToDoList>(Arrays.asList(this.toArray(items)));
	}

}