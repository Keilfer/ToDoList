package com.example.todolist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ArchiveActivity extends Activity {

	private ListView toDoListView;
	private ToDoListPageAdapter listAdapter;
	private ArrayList<ToDoItem> archive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_archive);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		try {
			archive = (ArrayList<ToDoItem>) readObject(this, "archiveStorage");
		} catch (IOException e) {
			archive = new ArrayList<ToDoItem>();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		toDoListView = (ListView) findViewById( R.id.archive_list );
		listAdapter = new ToDoListPageAdapter(this, R.layout.check_list_item, archive);
		toDoListView.setAdapter( listAdapter );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.archive, menu);
		return true;
	}
	
	public static Object readObject(Context context, String key) throws IOException,
	   ClassNotFoundException, FileNotFoundException {
	   FileInputStream fis = context.openFileInput(key);
	   ObjectInputStream ois = new ObjectInputStream(fis);
	   Object object = ois.readObject();
	   return object;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    case R.id.archive_stats:
	    	System.out.println("1");
			LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View popupView = layoutInflater.inflate(
					R.layout.stats_popup, null);
			
			int checkedToDo = 0;
			int uncheckedToDo = 0;
			
			for(Iterator<ToDoItem> i = archive.iterator(); i.hasNext();){
				if(i.next().getDone()) checkedToDo += 1;
				else uncheckedToDo += 1;
			}
			TextView numDoneItems = new TextView(this);
			numDoneItems = (TextView) popupView.findViewById(R.id.number_done_items);
			numDoneItems.setText("Number of checked items: " + checkedToDo);
			
			TextView numUndoneItems = new TextView(this);
			numUndoneItems = (TextView) popupView.findViewById(R.id.number_undone_items);
			numUndoneItems.setText("Number of unchecked items: " + uncheckedToDo);
			
			final PopupWindow popupWindow = new PopupWindow(popupView,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
			Button doneButton = (Button) popupView
					.findViewById(R.id.stats_done);
			
			doneButton.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					popupWindow.dismiss();
				}
			});
		
			popupWindow.showAtLocation(
					layoutInflater.inflate(R.layout.activity_archive, null),
					Gravity.CENTER, 0, 0);
		
			popupWindow.setFocusable(true);
			popupWindow.update();
			popupView.requestLayout();
			return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}

