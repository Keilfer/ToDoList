package com.example.todolist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class ToDoListManager extends Vector<ToDoItem> {
	
	private String listName;
	
	ToDoListManager(String n){
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
	
	public ArrayList<ToDoItem> getItems(){
		ToDoItem items[] = new ToDoItem[this.size()];
		return new ArrayList<ToDoItem>(Arrays.asList(this.toArray(items)));
	}

}
