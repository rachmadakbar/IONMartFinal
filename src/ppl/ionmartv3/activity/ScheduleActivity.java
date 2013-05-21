package ppl.ionmartv3.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.session.Schedule;

public class ScheduleActivity extends Activity {
	static List<Schedule> model = new ArrayList<Schedule>();
	ScheduleAdapter adapter=null;
	static Schedule edited = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getAllSchedule();
		Schedule p = new Schedule("Gaul","Harian");
		p.addDate("2-05-2013");
		p.addDate("12-05-2013");
		p.addDate("23-05-2013");
		p.addDate("2-05-2013");
		p.addDate("12-05-2013");
		p.addDate("23-05-2013");
		p.addDate("2-05-2013");
		p.addDate("12-05-2013");
		p.addDate("23-05-2013");
		p.addDate("2-05-2013");
		p.addDate("12-05-2013");
		p.addDate("23-05-2013");
		model.add(p);
		model.add(p);
		setContentView(R.layout.activity_schedule);	
		Button create = (Button) findViewById(R.id.create_schedule);
		create.setOnClickListener(createSchedule);
		ListView list=(ListView)findViewById(R.id.list);
		adapter=new ScheduleAdapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				edited = adapter.getItem(arg2);
				Intent intent = new Intent(ScheduleActivity.this, ListProductInScheduleActivity.class);
				ScheduleActivity.this.startActivity(intent); 
			}
		});
	}
	private void getAllSchedule(){
		//get all schedule
		//make Line item for each schedule
		/*
		 * Schedule s = new Schedule
		 * model.add(s)
		 */
	}
	
	private View.OnClickListener createSchedule = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//Toast.makeText(ScheduleActivity.this.getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();
			Intent xintent = new Intent(getApplicationContext(), ScheduleFormActivity.class);
			startActivity(xintent);
			
		}
	};
	 	
	class ScheduleAdapter extends ArrayAdapter<Schedule> {
		ScheduleAdapter() {
			super(ScheduleActivity.this, R.layout.schedule_row, model);
		}
		
		@Override
		public View getView(int position, View convertView,ViewGroup parent) {
			View row=convertView;
			ListLog holder=null;
			
			if (row==null) {													
				LayoutInflater inflater=getLayoutInflater();	
				row=inflater.inflate(R.layout.schedule_row, parent, false);
				holder=new ListLog(row);
				row.setTag(holder);
				
			}
			else {
				holder=(ListLog)row.getTag();
			}
			
			holder.populateFrom(model.get(position));
			
			return(row);
		}
	}
	
	static class ListLog {
		private TextView rowId=null;
		private TextView rowType=null;
		private TextView rowDate=null;
		
		ListLog(View row) {
			rowId=(TextView)row.findViewById(R.id.rowId);
			rowType=(TextView)row.findViewById(R.id.rowType);
			rowDate=(TextView)row.findViewById(R.id.rowDate);
		}
		
		void populateFrom(Schedule r) {
			rowId.setText(r.getName());
			rowType.setText("Jenis : "+r.getType());
			rowDate.setText("Date : "+r.getDate());
		}
	}	
}
