package com.whiteout.task;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whiteout.task.ToDoItem.Status;

public class ToDoListAdapter extends BaseAdapter {

	// List of ToDoItems
	private final List<ToDoItem> mItems = new ArrayList<ToDoItem>();
	private final Context mContext;
	private static final String TAG = "Whiteout-Debug";

	public ToDoListAdapter(Context context) {
		mContext = context;
	}

	// Add a ToDoItem to adapter
	// Notify the data set changed

	public void add(ToDoItem item) {
		mItems.add(item);
		notifyDataSetChanged();
	}

	// Clear entire list
	public void clear() {
		mItems.clear();
		notifyDataSetChanged();
	}
	
	// Remove a single ToDoItem
	public void remove(int pos) {
		mItems.remove(pos);
		for(int i=0; i<mItems.size(); i++){
			mItems.get(i).setPosition(i);
		}
		notifyDataSetChanged();
	}

	// Get number of ToDoItems
	public int getCount() {
		return mItems.size();
	}

	// Retrieve a ToDoItem
	@Override
	public Object getItem(int pos) {
		return mItems.get(pos);
	}

	// Get ID for ToDoItem, aka:position
	@Override
	public long getItemId(int pos) {
		return pos;
	}

	// Create a View to display to ToDoItem
	// at specific position in mItems
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ToDoItem toDoItem = (ToDoItem) getItem(position);
		
		// Retrieve an inflater
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		// Inflate the entireLayout
		RelativeLayout itemLayout = (RelativeLayout) inflater.inflate(
				R.layout.todo_item, null);

		// Create the titleView
		final TextView titleView = (TextView) itemLayout
				.findViewById(R.id.titleView);
		titleView.setText(toDoItem.getTitle());

		// Create the finished checkBox
		final CheckBox statusView = (CheckBox) itemLayout
				.findViewById(R.id.statusCheckBox);
		statusView.setChecked(toDoItem.getStatus().equals(Status.DONE) ? 
				true :
				false);

		statusView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				log("Enter onCheckedChanged()");

				if (toDoItem.getStatus().equals(Status.DONE))
					toDoItem.setStatus(Status.NOTDONE);
				else
					toDoItem.setStatus(Status.DONE);
					
			}
		});
		
		// Create the delete button
		final Button deleteButton = (Button) itemLayout.findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				remove(position);
			}
		});

		// Create the priorityView
		final TextView priorityView = (TextView) itemLayout
				.findViewById(R.id.priorityView);
		priorityView.setText(toDoItem.getPriority().toString());

		// Create the dateView
		final TextView dateView = (TextView) itemLayout
				.findViewById(R.id.dateView);
		dateView.setText(ToDoItem.FORMAT.format(toDoItem.getDate()));

		// Return's the entire view just created
		return itemLayout;
		// Magical...
	}

	private void log(String msg) {
		Log.i(TAG, msg);
	}
}
