// http://windrealm.org/tutorials/android/android-listview.php
// http://androidresearch.wordpress.com/2013/04/07/caching-objects-in-android-internal-storage/

package com.example.todolist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

public class MainActivity extends Activity {

	private ListView toDoMainView;
	private ListView toDoListView;
	
	private ToDoMainAdapter mainAdapter;
	private ToDoMainManager mainManager;
	
	private ToDoListPageAdapter listAdapter;
	private int listPosition;
	
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			mainManager = (ToDoMainManager) readObject(this, "listStorage");
		} catch (IOException e) {
			mainManager = new ToDoMainManager();
			try {
				writeObject(this, "listStorage", mainManager);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		actionBar = getActionBar();
		initMainView();
	}
	
	public void initMainView(){
		setContentView(R.layout.activity_main);
		actionBar.setDisplayHomeAsUpEnabled(false);

		mainAdapter = new ToDoMainAdapter(this, R.layout.list_item,
				mainManager.getItems());
		
		toDoMainView = (ListView) findViewById(R.id.main_list_view);
		toDoMainView.setAdapter(mainAdapter);
		
		toDoMainView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            	listPosition = position;
        		initListView();
            }
        });

		final Button button = (Button) findViewById(R.id.main_add_button);
		button.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View popupView = layoutInflater.inflate(
						R.layout.add_list_popup, null);
				final PopupWindow popupWindow = new PopupWindow(popupView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				Button addButton = (Button) popupView
						.findViewById(R.id.add_list_add);
				final EditText editText = (EditText) popupView
						.findViewById(R.id.add_list_field);
				
				addButton.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						String newList = editText.getText().toString();
						mainManager.addItem(newList);
						try {
							writeObject(MainActivity.this, "listStorage", mainManager);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						
						mainAdapter.clear();
						mainAdapter.addAll(mainManager.getItems());
						mainAdapter.notifyDataSetChanged();
						popupWindow.dismiss();
					}
				});

				Button cancelButton = (Button) popupView
						.findViewById(R.id.add_list_cancel);
				cancelButton.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						popupWindow.dismiss();
					}
				});

				popupWindow.showAtLocation(
						layoutInflater.inflate(R.layout.activity_main, null),
						Gravity.CENTER, 0, 0);

				popupWindow.setFocusable(true);
				popupWindow.update();
			}
		});
	}
	
	public void initListView(){
		setContentView(R.layout.activity_to_do_list_page);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		listAdapter = new ToDoListPageAdapter(this, R.layout.check_list_item,
				mainManager.get(listPosition).getItems());

		toDoListView = (ListView) findViewById(R.id.list_page_list_view);
		toDoListView.setAdapter(listAdapter);
		
		toDoListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            	ListView lv = (ListView) parent;
            	
            	CheckBox box = (CheckBox)view.findViewById(R.id.list_check_box);
            	box.setChecked(lv.isItemChecked(position));
            	
            	mainManager.elementAt(listPosition).elementAt(position).setDone(lv.isItemChecked(position));
            	try {
					writeObject(MainActivity.this, "listStorage", mainManager);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        });

		final Button button = (Button) findViewById(R.id.to_do_list_add);
		button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View popupView = layoutInflater.inflate(
						R.layout.add_list_popup, null);
				final PopupWindow popupWindow = new PopupWindow(popupView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				Button addButton = (Button) popupView
						.findViewById(R.id.add_list_add);
				final EditText editText = (EditText) popupView
						.findViewById(R.id.add_list_field);
				
				addButton.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						String newList = editText.getText().toString();
						mainManager.get(listPosition).addItem(newList);
						try {
							writeObject(MainActivity.this, "listStorage", mainManager);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						
						listAdapter.clear();
						listAdapter.addAll(mainManager.get(listPosition).getItems());
						listAdapter.notifyDataSetChanged();
						popupWindow.dismiss();
					}
				});

				Button cancelButton = (Button) popupView
						.findViewById(R.id.add_list_cancel);
				cancelButton.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						popupWindow.dismiss();
					}
				});

				popupWindow.showAtLocation(
						layoutInflater.inflate(R.layout.activity_main, null),
						Gravity.CENTER, 0, 0);

				popupWindow.setFocusable(true);
				popupWindow.update();
			}
		});
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
		switch (id) {
        case android.R.id.home:
            // app icon in action bar clicked; go home
        	initMainView();
            return true;
        default:
            return super.onOptionsItemSelected(item); 
		}
	}
	
	public static void writeObject(Context context, String key, Object object) throws IOException {
	   FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
	   ObjectOutputStream oos = new ObjectOutputStream(fos);
	   oos.writeObject(object);
	   oos.close();
	   fos.close();
	}
	 
	public static Object readObject(Context context, String key) throws IOException,
	   ClassNotFoundException, FileNotFoundException {
	   FileInputStream fis = context.openFileInput(key);
	   ObjectInputStream ois = new ObjectInputStream(fis);
	   Object object = ois.readObject();
	   return object;
	}

}
