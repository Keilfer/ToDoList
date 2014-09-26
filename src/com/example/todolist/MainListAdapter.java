package com.example.todolist;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/*
 * Simple extension to the ArrayAdapter class used to display
 * to do list names in the MainActivity.
 * 
 * Code found in the following tutorial was used in this file:
 * http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
 */
public class MainListAdapter extends ArrayAdapter<ToDoList>{

    Context context; 
    int layoutResourceId;    
    ArrayList<ToDoList> data = null;
    
    public MainListAdapter(Context context, int layoutResourceId, ArrayList<ToDoList> data) {
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