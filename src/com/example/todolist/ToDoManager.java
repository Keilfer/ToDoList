package com.example.todolist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/*
 * Class containing a set of ToDoList objects, used as the main
 * set of data accessed by the application.
 */
public class ToDoManager extends Vector<ToDoList>{

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