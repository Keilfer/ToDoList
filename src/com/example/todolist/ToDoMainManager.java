package com.example.todolist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ToDoMainManager extends Vector<ToDoListManager>{

	public void addItem(String name){
		this.add(new ToDoListManager(name));
	}
	
	public String getListName(int index){
		return this.get(index).getName();
	}
	
	public ArrayList<ToDoListManager> getItems(){
		ToDoListManager items[] = new ToDoListManager[this.size()];
		return new ArrayList<ToDoListManager>(Arrays.asList(this.toArray(items)));
	}

}