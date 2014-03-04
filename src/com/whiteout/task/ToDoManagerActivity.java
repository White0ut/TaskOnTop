package com.whiteout.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.whiteout.task.ToDoItem.Priority;
import com.whiteout.task.ToDoItem.Status;

public class ToDoManagerActivity extends ListActivity {

	private static final int ADD_TODO_ITEM_REQUEST = 0;

	private static final String FILE_NAME = "TodoManagerActivityData.txt";
	private static final String TAG = "Whiteout-Debug";

	// IDs for menu items
	private static final int MENU_DELETE = Menu.FIRST;

	ToDoListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create new TodoListAdapter for this ListView
		mAdapter = new ToDoListAdapter(getApplicationContext());

		// Put divider between ToDoItems and FooterView
		getListView().setFooterDividersEnabled(true);

		LayoutInflater inflater = getLayoutInflater();

		TextView footerView = (TextView) inflater.inflate(R.layout.footer_view,
				null);

		getListView().addFooterView(footerView);

		footerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				log("Entered footerView.onClick()");

				Intent intent = new Intent(ToDoManagerActivity.this,
						AddToDoActivity.class);
				startActivityForResult(intent, ADD_TODO_ITEM_REQUEST);
			}
		});

		getListView().setAdapter(mAdapter);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		log("Entered onActivityResult()");

		if (requestCode == ADD_TODO_ITEM_REQUEST && resultCode == RESULT_OK) {
			data.putExtra(ToDoItem.POSITION, (""+(mAdapter.getCount()+1)));
			ToDoItem todo = new ToDoItem(data);
			mAdapter.add(todo);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// If necessary, load toDoItems
		if (mAdapter.getCount() == 0)
			loadItems();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Save toDoItems
		saveItems();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete All");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_DELETE) {
			mAdapter.clear();
			return true;
		} else
			return super.onOptionsItemSelected(item);
	}

	// Load ToDoItems from file
	private void loadItems() {
		BufferedReader reader = null;
		try {
			FileInputStream fis = openFileInput(FILE_NAME);
			reader = new BufferedReader(new InputStreamReader(fis));

			String title = null;
			String priority = null;
			String status = null;
			Date date = null;

			while (null != (title = reader.readLine())) {
				priority = reader.readLine();
				status = reader.readLine();
				date = ToDoItem.FORMAT.parse(reader.readLine());
				mAdapter.add(new ToDoItem(title, Priority.valueOf(priority),
						Status.valueOf(status), date, mAdapter.getCount()+1));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Save ToDoItems to file
	private void saveItems() {
		PrintWriter writer = null;
		try {
			FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					fos)));

			for (int x = 0; x < mAdapter.getCount(); x++) {

				writer.println(mAdapter.getItem(x));

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	private void log(String msg) {
		Log.i(TAG, msg);
	}
}
