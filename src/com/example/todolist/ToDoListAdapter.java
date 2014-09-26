package com.example.todolist;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter

import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ToDoListAdapter extends ArrayAdapter<ToDoItem>{

    Context context; 
    int layoutResourceId;    
    ArrayList<ToDoItem> data = null;
    
    public ToDoListAdapter(Context context, int layoutResourceId, ArrayList<ToDoItem> arrayList) {
        super(context, layoutResourceId, arrayList);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = arrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);
        
        TextView txtTitle = (TextView)row.findViewById(R.id.ListItemTextView);
        
        CheckBox box = (CheckBox)row.findViewById(R.id.list_check_box);
    	box.setChecked(data.get(position).getDone());
        
        txtTitle.setText((CharSequence) data.get(position).getText());
        
        row.setTag(txtTitle);
        
        return row;
    }
}