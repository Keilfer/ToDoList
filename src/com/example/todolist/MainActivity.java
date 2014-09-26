package com.example.todolist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
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
import android.widget.Toast;

/*
 * This class provides an Activity that acts as the main interface
 * for the user. It loads and displays a main data structure containing
 * all saved to do lists. The activity switches between two views to show
 * either the list of all to do lists saved, or the actual items of a
 * selected list. This simply avoids the hassle of transferring data
 * between activities, and keeps most everything contained to a single
 * activity.
 * 
 * STUFF TO IMPROVE
 *    The method used to open and store data is not ideal, as it
 *    does nothing to prevent simultaneous access of the data by
 *    both this activity, and another, although this should not be
 *    an issue with normal use.
 *    
 *    The initMainView/initListView classes most likely do more work
 *    than they really need to. They could be split into a proper init
 *    method, and one that simply switches to whichever view, updating
 *    only what is necessary.
 * 
 * Various bits and pieces of code found in the following tutorials
 * were used in this file:
 * 
 * http://windrealm.org/tutorials/android/android-listview.php
 * http://www.mikeplate.com/2010/01/21/show-a-context-menu-for-long-clicks-in-an-android-listview/
 */
public class MainActivity extends Activity {

	private ListView toDoMainView;
	private ListView toDoListView;

	private MainListAdapter mainAdapter;
	private ToDoManager mainManager;

	private ToDoListAdapter listAdapter;
	private int listPosition;

	private int viewSet;// 0 = main_page_view, 1 = list_page_list_view
	private int contextMenuActionTarget;

	private ArrayList<ToDoItem> archive;

	private ActionBar actionBar;

	/*
	 * Loads or creates the main data file, and sets the appropriate view.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			mainManager = (ToDoManager) readObject(this, "listStorage");
		} catch (IOException e) {
			mainManager = new ToDoManager();
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

	/*
	 * Displays the main view listing the available to do lists. Sets listeners
	 * for the various buttons and clickable items.
	 */
	public void initMainView() {
		viewSet = 0;
		setContentView(R.layout.activity_main);
		actionBar.setDisplayHomeAsUpEnabled(false);

		//creating and setting the adapter for the ListView.
		mainAdapter = new MainListAdapter(this, R.layout.list_item,
				mainManager.getItems());

		toDoMainView = (ListView) findViewById(R.id.main_list_view);
		toDoMainView.setAdapter(mainAdapter);

		registerForContextMenu(toDoMainView);

		//The following sets the various listeners used.
		toDoMainView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
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
							writeObject(MainActivity.this, "listStorage",
									mainManager);
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

	/*
	 * Displays the main view listing the available to do lists. Sets listeners
	 * for the various buttons and clickable items.
	 * The main addition not included in the main view is the checkbox accompanying
	 * every item in this view's ListView.
	 */
	public void initListView() {
		viewSet = 1;
		setContentView(R.layout.activity_to_do_list_page);
		actionBar.setDisplayHomeAsUpEnabled(true);

		//creating and setting the adapter for the ListView.
		listAdapter = new ToDoListAdapter(this, R.layout.check_list_item,
				mainManager.get(listPosition).getItems());

		toDoListView = (ListView) findViewById(R.id.list_page_list_view);
		toDoListView.setAdapter(listAdapter);

		registerForContextMenu(toDoListView);

		//The following sets the various listeners used.
		toDoListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView lv = (ListView) parent;

				CheckBox box = (CheckBox) view
						.findViewById(R.id.list_check_box);
				box.setChecked(lv.isItemChecked(position));

				mainManager.elementAt(listPosition).elementAt(position)
						.setDone(lv.isItemChecked(position));
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
							writeObject(MainActivity.this, "listStorage",
									mainManager);
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						listAdapter.clear();
						listAdapter.addAll(mainManager.get(listPosition)
								.getItems());
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

	/*
	 * Handles an item being selected from the actionbar menu. The
	 * viewSet class variable is used to keep track of the context
	 * in which the various items were clicked.
	 * home: only active when the user navigates to an individual to
	 *    do list. Returns to the main list of to do lists.
	 * stats: calculates and displays various statistics to the user
	 *    in a popup window.
	 * archive: starts a new ArchiveActivity with an intent.
	 * send: generates an email containing the contents of the current
	 *    page, and passes it to sendEmail().
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			initMainView();
			return true;
		case R.id.stats:
			LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View popupView = layoutInflater.inflate(R.layout.stats_popup, null);

			int checkedToDo = 0;
			int uncheckedToDo = 0;
			int numberOfLists = 0;

			for (Iterator<ToDoList> i = mainManager.iterator(); i.hasNext();) {
				numberOfLists += 1;
				for (Iterator<ToDoItem> j = i.next().iterator(); j.hasNext();) {
					if (j.next().getDone())
						checkedToDo += 1;
					else
						uncheckedToDo += 1;
				}
			}
			TextView numDoneItems = new TextView(this);
			numDoneItems = (TextView) popupView
					.findViewById(R.id.number_done_items);
			numDoneItems.setText("Number of checked items: " + checkedToDo);

			TextView numUndoneItems = new TextView(this);
			numUndoneItems = (TextView) popupView
					.findViewById(R.id.number_undone_items);
			numUndoneItems.setText("Number of unchecked items: "
					+ uncheckedToDo);

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
		case R.id.send:
			String message = "";
			String subject = "";
			if (viewSet == 0) {
				message = "All To Do Lists\n\n";
				subject = "All To Do Lists";
				for (Iterator<ToDoList> i = mainManager.iterator(); i.hasNext();) {
					ToDoList currentList = i.next();
					message = message.concat(currentList.getName() + "\n");
					for (Iterator<ToDoItem> j = currentList.iterator(); j
							.hasNext();) {
						ToDoItem currentItem = j.next();
						if (currentItem.getDone())
							message = message.concat("   [X]\t"
									+ currentItem.getText() + "\n");
						else
							message = message.concat("   [ ]\t"
									+ currentItem.getText() + "\n");
					}
					message = message.concat("\n");
				}
			} else if (viewSet == 1) {
				message = mainManager.getListName(listPosition) + "\n";
				subject = "To Do List:" + mainManager.getListName(listPosition);
				for (Iterator<ToDoItem> i = mainManager.get(listPosition)
						.iterator(); i.hasNext();) {
					ToDoItem currentItem = i.next();
					if (currentItem.getDone())
						message = message.concat("   [X]\t"
								+ currentItem.getText() + "\n");
					else
						message = message.concat("   [ ]\t"
								+ currentItem.getText() + "\n");
				}
			}
			sendEmail(subject, message);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * Creates a touch-and-hold context menu with contents dependent
	 * on whether it is for an item in a to do list, or the to do
	 * list itself.
	 */
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.main_list_view) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(mainManager.get(info.position).getName());
			String[] menuItems = { "Delete" };
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}

			contextMenuActionTarget = info.position;
		} else if (v.getId() == R.id.list_page_list_view) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(mainManager.get(listPosition)
					.get(info.position).getText());
			String[] menuItems = { "Archive", "Delete" };
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}

			contextMenuActionTarget = info.position;
		}
	}

	/*
	 * Handles deleting/moving an item and re-drawing the view when
	 * an option in a context menu is selected.
	 */
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuPosition = item.getItemId();

		if (viewSet == 0) {
			if (menuPosition == 0) {
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
		} else if (viewSet == 1) {
			if (menuPosition == 0) {
				archive.add(mainManager.elementAt(listPosition).get(
						contextMenuActionTarget));
				mainManager.elementAt(listPosition).remove(
						contextMenuActionTarget);

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
			if (menuPosition == 1) {
				mainManager.elementAt(listPosition).remove(
						contextMenuActionTarget);
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

	/*
	 * Borrowed code by user fiXedd on stackoverflow
	 * http://stackoverflow.com/questions/2197741/how-can-i-send-emails-from-my-android-application
	 * 
	 * Opens an email client with the provided subject line and message pre-written.
	 */
	public boolean sendEmail(String subject, String content) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_SUBJECT, subject);
		i.putExtra(Intent.EXTRA_TEXT, content);
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(MainActivity.this,
					"There are no email clients installed.", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	/*
	 * Code borrowed from Veaceslav Grec
	 * http://androidresearch.wordpress.com/2013/04/07/caching-objects-in-android-internal-storage/
	 * 
	 * Reads an object from a file.
	 */
	public static Object readObject(Context context, String key)
			throws IOException, ClassNotFoundException, FileNotFoundException {
		FileInputStream fis = context.openFileInput(key);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object object = ois.readObject();
		return object;
	}
	
	/*
	 * Code borrowed from Veaceslav Grec
	 * http://androidresearch.wordpress.com/2013/04/07/caching-objects-in-android-internal-storage/
	 * 
	 * Writes an object to a file.
	 */
	public static void writeObject(Context context, String key, Object object)
			throws IOException {
		FileOutputStream fos = context
				.openFileOutput(key, Context.MODE_PRIVATE);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(object);
		oos.close();
		fos.close();
	}
}
