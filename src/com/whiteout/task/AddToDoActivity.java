package com.whiteout.task;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.whiteout.task.ToDoItem.Priority;
import com.whiteout.task.ToDoItem.Status;

public class AddToDoActivity extends Activity {

	private static final int SEVEN_DAYS = 604800000;

	private static final String TAG = "Whiteout-Debug";

	private static String timeString;
	private static String dateString;
	private static TextView dateView;
	private static TextView timeView;

	private Date mDate;
	private RadioGroup mPriorityRadioGroup;
	private EditText mTitleText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_todo);

		// Initialize from layout
		mTitleText = (EditText) findViewById(R.id.title);
		mPriorityRadioGroup = (RadioGroup) findViewById(R.id.priorityGroup);
		dateView = (TextView) findViewById(R.id.date);
		timeView = (TextView) findViewById(R.id.time);
		log("Initialized vars");

		// Set default date and time

		setDefaultDateTime();

		// OnClickListener for the Date button, calls showDatePickerDialog()
		// This shows the Date dialog

		final Button datePickerButton = (Button) findViewById(R.id.date_picker_button);
		datePickerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDatePickerDialog();
			}
		});

		// OnClickListener for the Time button, calls showTimePickerDialog()
		// This displays the Time dialog and sets the text

		final Button timePickerButton = (Button) findViewById(R.id.time_picker_button);
		timePickerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showTimePickerDialog();
			}
		});

		final Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				log("Entered cancelButton.onClick");

				// Set result to CANCELLED, finish activity
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});

		// Listener for the reset button, sets default values
		// Leave title the same (preference)
		final Button resetButton = (Button) findViewById(R.id.resetButton);
		resetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				log("Entered resetButton.onClick");

				// mTitleText.setText("");
				mPriorityRadioGroup.check(R.id.medPriority);
				setDefaultDateTime();
			}
		});

		// onClickListener for the submit button
		final Button submitButton = (Button) findViewById(R.id.submitButton);
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				log("Entered submitButton.onClick()");

				// Get all necessary data
				Priority priority = getPriority();
				Status status = Status.NOTDONE;
				String titleString = mTitleText.getText().toString();
				String date = dateString + " " + timeString;

				// Package data
				Intent intent = new Intent();
				ToDoItem.packageIntent(intent, titleString, priority, status, date);
				log("intent is packaged");

				// set result and finish
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		log("Submit button set");

	}

	private void setDefaultDateTime() {

		// Default date is current time + seven days
		mDate = new Date();
		mDate = new Date(mDate.getTime() + SEVEN_DAYS);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);

		setDateString(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));

		dateView.setText(dateString);

		setTimeString(calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.MILLISECOND));

		timeView.setText(timeString);
	}

	private static void setDateString(int year, int monthOfYear, int dayOfMonth) {
		// Increment monthOfYear for Calendar/Date Time format setting
		monthOfYear++;
		String mon = "" + monthOfYear;
		String day = "" + dayOfMonth;

		if (monthOfYear < 10)
			mon = "0" + monthOfYear;
		if (dayOfMonth < 10)
			day = "0" + dayOfMonth;

		dateString = year + "-" + mon + "-" + day;
	}

	private static void setTimeString(int hourOfDay, int minute, int milli) {
		String hour = "" + hourOfDay;
		String min = "" + minute;

		if (hourOfDay < 10)
			hour = "0" + hourOfDay;
		if (minute < 10)
			min = "0" + minute;

		// You could optionally include milli, not necessary here
		timeString = hour + ":" + min + ":00";

	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			// Use the current date as the default date in the picker

			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			setDateString(year, monthOfYear, dayOfMonth);

			dateView.setText(dateString);
		}

	}

	public static class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			// Current time is default time for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int min = c.get(Calendar.MINUTE);

			// Return the instance of TimePickerDialog
			return new TimePickerDialog(getActivity(),
					TimePickerDialog.THEME_HOLO_DARK, this, hour, min, true);

		}

		public void onTimeSet(TimePicker view, int hour, int min) {
			setTimeString(hour, min, 0);

			timeView.setText(timeString);
		}
	}

	private void showDatePickerDialog() {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	private void showTimePickerDialog() {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}

	private Priority getPriority() {

		int priority = mPriorityRadioGroup.getCheckedRadioButtonId();
		if (priority == R.id.lowPriority)
			return Priority.LOW;
		else if (priority == R.id.highPriority)
			return Priority.HIGH;
		else
			return Priority.MED;
	}

	private void log(String msg) {
		Log.i(TAG, msg);
	}
}
