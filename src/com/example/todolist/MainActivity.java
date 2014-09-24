// http://windrealm.org/tutorials/android/android-listview.php
// http://androidresearch.wordpress.com/2013/04/07/caching-objects-in-android-internal-storage/
// http://www.mikeplate.com/2010/01/21/show-a-context-menu-for-long-clicks-in-an-android-listview/

package com.example.todolist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
import android.widget.TextView;

public class MainActivity extends Activity {

	private ListView toDoMainView;
	private ListView toDoListView;
	
	private ToDoMainAdapter mainAdapter;
	private ToDoMainManager mainManager;
	
	private ToDoListPageAdapter listAdapter;
	private int listPosition;
	
	private int contextMenuTypeID;//0 = main_page_view, 1 = list_page_list_view
	private int contextMenuActionTarget;
	
	private ArrayList<ToDoItem> archive;
	
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
		
		try {
			archive = (ArrayList<ToDoItem>) readObject(this, "archiveStorage");
		} catch (IOException e) {
			archive = new ArrayList<ToDoItem>();
			try {
				writeObject(this, "archiveStorage", archive);
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
		
	    registerForContextMenu(toDoMainView);
		
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
		
		registerForContextMenu(toDoListView);
		
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
        case R.id.stats:
			LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View popupView = layoutInflater.inflate(
					R.layout.stats_popup, null);
			
			int checkedToDo = 0;
			int uncheckedToDo = 0;
			int numberOfLists = 0;
			
			for(Iterator<ToDoListManager> i = mainManager.iterator(); i.hasNext();){
				numberOfLists += 1;
				for(Iterator<ToDoItem> j = i.next().iterator(); j.hasNext();){
					if(j.next().getDone()) checkedToDo += 1;
					else uncheckedToDo += 1;
				}
			}
			TextView numDoneItems = new TextView(this);
			numDoneItems = (TextView) popupView.findViewById(R.id.number_done_items);
			numDoneItems.setText("Number of checked items: " + checkedToDo);
			
			TextView numUndoneItems = new TextView(this);
			numUndoneItems = (TextView) popupView.findViewById(R.id.number_undone_items);
			numUndoneItems.setText("Number of unchecked items: " + uncheckedToDo);
			
			TextView numLists = new TextView(this);
			numLists = (TextView) popupView.findViewById(R.id.number_lists);
			numLists.setText("Number of lists: " + numberOfLists);
			
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
					layoutInflater.inflate(R.layout.activity_main, null),
					Gravity.CENTER, 0, 0);
		
			popupWindow.setFocusable(true);
			popupWindow.update();
			popupView.requestLayout();
			return true;
        case R.id.archive:
        	Intent intent = new Intent(this, ArchiveActivity.class);
        	startActivity(intent);
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

	public void onCreateContextMenu(ContextMenu menu, View v,
		    ContextMenuInfo menuInfo) {
		  if (v.getId()==R.id.main_list_view) {
		    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		    menu.setHeaderTitle(mainManager.get(info.position).getName());
		    String[] menuItems = {"Delete"};
		    for (int i = 0; i<menuItems.length; i++) {
		      menu.add(Menu.NONE, i, i, menuItems[i]);
		    }
		    
		    contextMenuTypeID = 0;
		    contextMenuActionTarget = info.position;
		  }
		  else if (v.getId()==R.id.list_page_list_view) {
		    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		    menu.setHeaderTitle(mainManager.get(listPosition).get(info.position).getText());
		    String[] menuItems = {"Archive", "Delete"};
		    for (int i = 0; i<menuItems.length; i++) {
		      menu.add(Menu.NONE, i, i, menuItems[i]);
		    }
		    
		    contextMenuTypeID = 1;
		    contextMenuActionTarget = info.position;
		  }
		}

	public boolean onContextItemSelected(MenuItem item) {
		  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		  int menuPosition = item.getItemId();
		  
		  if(contextMenuTypeID == 0){
			  if(menuPosition == 0){
				  mainManager.remove(contextMenuActionTarget);
				  try {
						writeObject(MainActivity.this, "listStorage", mainManager);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					mainAdapter.clear();
					mainAdapter.addAll(mainManager.getItems());
					mainAdapter.notifyDataSetChanged();
			  }
		  }
		  else if(contextMenuTypeID == 1){
			  if(menuPosition == 0){
				  archive.add(mainManager.elementAt(listPosition).get(contextMenuActionTarget));
				  mainManager.elementAt(listPosition).remove(contextMenuActionTarget);
				  
				  try {
						writeObject(MainActivity.this, "listStorage", mainManager);
						writeObject(MainActivity.this, "archiveStorage", archive);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					listAdapter.clear();
					listAdapter.addAll(mainManager.get(listPosition).getItems());
					listAdapter.notifyDataSetChanged();
				  
			  }
			  if(menuPosition == 1){
				  System.out.println(mainManager);
				  System.out.println(mainManager.elementAt(listPosition));
				  mainManager.elementAt(listPosition).remove(contextMenuActionTarget);
				  try {
						writeObject(MainActivity.this, "listStorage", mainManager);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					listAdapter.clear();
					listAdapter.addAll(mainManager.get(listPosition).getItems());
					listAdapter.notifyDataSetChanged();
			  }
		  }
		  return true;
		}
}
