package ppl.ionmartv3.activity;

import java.util.Calendar;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.session.Schedule;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ScheduleFormActivity extends Activity {

	EditText name = null;
	EditText repeated = null;
	Spinner type = null;
	Button save;
	DatePicker date;
	static ScheduleFormActivity scheduleForm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_form);
		scheduleForm = this;
		name = (EditText) findViewById(R.id.scheduleName);
		type = (Spinner) findViewById(R.id.spinnerType);
		repeated = (EditText) findViewById(R.id.repeated);
		date = (DatePicker) findViewById(R.id.datePickerSchedule);
		save = (Button) findViewById(R.id.saveSchedule);
		save.setOnClickListener(createSchedule);
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
			} else if (date.getYear() < cNow.get(Calendar.YEAR)) {
				error = true;
				errorField = "Time is invalid";
			} else if (date.getYear() == cNow.get(Calendar.YEAR)) {
				if (date.getMonth() < cNow.get(Calendar.MONTH)) {
					error = true;
					errorField = "Time is invalid";
				} else if (date.getMonth() == cNow.get(Calendar.MONTH)) {
					if (date.getDayOfMonth() < cNow.get(Calendar.DAY_OF_MONTH)) {
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
						repeated.getText().toString(), date.getYear(),
						date.getMonth(), date.getDayOfMonth());

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
