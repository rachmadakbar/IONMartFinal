package ppl.ionmartv3.activity;

import java.util.Calendar;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.session.Schedule;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleFormActivity extends Activity {

	EditText name = null;
	EditText repeated = null;
	Spinner type = null;
	Button save;
	private int mYear;
	private int mMonth;
	private int mDay;

	private TextView mDateDisplay;
	private Button mPickDate;

	static final int DATE_DIALOG_ID = 0;
	static ScheduleFormActivity scheduleForm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_form);
		scheduleForm = this;
		name = (EditText) findViewById(R.id.scheduleName);
		type = (Spinner) findViewById(R.id.spinnerType);
		repeated = (EditText) findViewById(R.id.repeated);
		save = (Button) findViewById(R.id.saveSchedule);
		save.setOnClickListener(createSchedule);
		mDateDisplay = (TextView) findViewById(R.id.showMyDate);
		mPickDate = (Button) findViewById(R.id.myDatePickerButton);

		mPickDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		// get the current date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// display the current date
		updateDisplay();
	}
	private void updateDisplay() {
		this.mDateDisplay.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mDay).append("-").append(mMonth + 1).append("-")
				.append(mYear).append(" "));
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	private View.OnClickListener createSchedule = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			boolean error = false;
			String errorField = "";
			Calendar cNow = Calendar.getInstance();
			cNow.setLenient(true);
			if (name.getText().toString().equals("")) {
				error = true;
				errorField = "Name is required";
			} else if (isAlreadyExist(name.getText().toString())) {
				error = true;
				errorField = "Name is alreadyExist";
			} else if (mYear < cNow.get(Calendar.YEAR)) {
				error = true;
				errorField = "Time is invalid";
			} else if (mYear == cNow.get(Calendar.YEAR)) {
				if (mMonth < cNow.get(Calendar.MONTH)) {
					error = true;
					errorField = "Time is invalid";
				} else if (mMonth == cNow.get(Calendar.MONTH)) {
					if (mDay < cNow.get(Calendar.DAY_OF_MONTH)) {
						error = true;
						errorField = "Time is invalid";
					}
				}
			}
			if (!error) {
				if (repeated.getText().toString().equals("")) {
					error = true;
					errorField = "Repeated is required";
				} else if (!repeated.getText().toString().equals("")) {
					if (Integer.parseInt(repeated.getText().toString()) == 0) {
						error = true;
						errorField = "Reapeted minimal is once";
					}

				}
			}

			if (error) {
				Toast.makeText(
						ScheduleFormActivity.this.getApplicationContext(),
						errorField, Toast.LENGTH_LONG).show();
			} else {				
				ScheduleActivity.edited = new Schedule(name.getText()
						.toString(), type.getSelectedItem().toString(),
						repeated.getText().toString(), mYear,
						mMonth, mDay);
				Intent intent = new Intent(ScheduleFormActivity.this,
						ListProductInScheduleActivity.class);
				ScheduleFormActivity.this.startActivity(intent);
			}
		}
	};

	public boolean isAlreadyExist(String name) {
		boolean result = false;
		for (int i = 0; i < ScheduleActivity.model.size(); i++) {
			if (ScheduleActivity.model.get(i).getName().equalsIgnoreCase(name)) {
				result = true;
				break;
			}
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_schedule_form, menu);
		return true;
	}

	public static ScheduleFormActivity getInstance() {
		return scheduleForm;
	}
}
