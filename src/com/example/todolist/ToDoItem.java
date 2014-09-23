package com.example.todolist;

import java.io.Serializable;

public class ToDoItem implements Serializable{
	
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
