package com.example.todolist;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class ToDoListPage extends Activity {

	private ListView toDoListView;
	private ToDoListPageAdapter listAdapter;
	private ToDoListManager listManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_to_do_list_page);
		
		toDoListView = (ListView) findViewById( R.id.list_page_list_view );
		listManager = new ToDoListManager("placeholder");
		
		// 1. get passed intent 
        Intent intent = getIntent();
 
        // 2. get person object from intent
        listManager = (ToDoListManager) intent.getSerializableExtra("START_LIST");
		
		listAdapter = new ToDoListPageAdapter(this, R.layout.check_list_item, listManager.getItems());
		
		toDoListView.setAdapter( listAdapter );
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}
	
}
