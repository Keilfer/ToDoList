package com.example.todolist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
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
import android.widget.Toast;

/*
 * This activity acts as a locked-down version of MainActivity,
 * only used to display to do list items archived by the user.
 * They cannot be modified.
 */
public class ArchiveActivity extends Activity {

	private ListView toDoListView;
	private ToDoListAdapter listAdapter;
	private ArrayList<ToDoItem> archive;

	/*
	 * Reads archive data from memory, and sets up fiews and adapters.
	 */
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
		listAdapter = new ToDoListAdapter(this, R.layout.check_list_item, archive);
		toDoListView.setAdapter( listAdapter );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.archive, menu);
		return true;
	}

	/*
	 * Handles an item being selected from the actionbar menu. The
	 * viewSet class variable is used to keep track of the context
	 * in which the various items were clicked.
	 * home: sends the user back to MainActivity
	 * stats: calculates and displays various statistics to the user
	 *    in a popup window.
	 * send: generates an email containing the contents of the current
	 *    page, and passes it to sendEmail().
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	    	finish();
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    case R.id.archive_send:
        	String message = "";
        	String subject = "";
    		message = "Archived To Do Items\n\n";
    		subject = "Archived To Do Items";
			for(Iterator<ToDoItem> i = archive.iterator(); i.hasNext();){
				ToDoItem currentItem = i.next();
				if(currentItem.getDone()) message = message.concat("   [X]\t" + currentItem.getText() + "\n");
				else message = message.concat("   [ ]\t" + currentItem.getText() + "\n");
			}
        	sendEmail(subject, message);
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
	
	/*
	 * Code borrowed from Veaceslav Grec
	 * http://androidresearch.wordpress.com/2013/04/07/caching-objects-in-android-internal-storage/
	 * 
	 * Reads an object from a file.
	 */
	public static Object readObject(Context context, String key) throws IOException,
	   ClassNotFoundException, FileNotFoundException {
	   FileInputStream fis = context.openFileInput(key);
	   ObjectInputStream ois = new ObjectInputStream(fis);
	   Object object = ois.readObject();
	   return object;
	}
	
	/*
	 * Borrowed code by user fiXedd on stackoverflow
	 * http://stackoverflow.com/questions/2197741/how-can-i-send-emails-from-my-android-application
	 * 
	 * Opens an email client with the provided subject line and message pre-written.
	 */
	public boolean sendEmail(String subject, String content){
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822"); 
		i.putExtra(Intent.EXTRA_SUBJECT, subject);
		i.putExtra(Intent.EXTRA_TEXT   , content);
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(ArchiveActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		    return false;
		}
		return true;
	}
}

