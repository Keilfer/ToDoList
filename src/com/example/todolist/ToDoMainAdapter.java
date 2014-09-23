package com.example.todolist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter

import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ToDoMainAdapter extends ArrayAdapter<ToDoListManager>{

    Context context; 
    int layoutResourceId;    
    ArrayList<ToDoListManager> data = null;
    
    public ToDoMainAdapter(Context context, int layoutResourceId, ArrayList<ToDoListManager> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);
        
        TextView txtTitle = (TextView)row.findViewById(R.id.ListItemTextView);
        
        txtTitle.setText((CharSequence) data.get(position).getName());
        
        row.setTag(txtTitle);
        
        return row;
    }
    
}