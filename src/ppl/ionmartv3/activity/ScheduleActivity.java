package ppl.ionmartv3.activity;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.adapter.IONMartDBAdapter;
import ppl.ionmartv3.activity.session.Customer;
import ppl.ionmartv3.activity.session.LineItem;
import ppl.ionmartv3.activity.session.Product;
import ppl.ionmartv3.activity.session.Schedule;



public class ScheduleActivity extends TabActivity implements
		View.OnClickListener {
	List<Schedule> model = new ArrayList<Schedule>();
	ScheduleAdapter adapter = null;
	EditText nama = null;
	Spinner type = null;
	Button save;
	static Schedule edited = null;
	boolean edit = false;
	IONMartDBAdapter db;
	Customer customer;
	DatePicker date;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_schedule);
		db = new IONMartDBAdapter(this);
		nama = (EditText) findViewById(R.id.scheduleName);
		type = (Spinner) findViewById(R.id.spinnerType);
		save = (Button) findViewById(R.id.saveSchedule);
		save.setOnClickListener(this);
		customer = CustomerHomeActivity.customer;
		date = (DatePicker) findViewById(R.id.dateStart);
		ListView list = (ListView) findViewById(R.id.listSchedule);
		adapter = new ScheduleAdapter();
		this.getAllSchedule();
		list.setAdapter(adapter);

		TabSpec spec = getTabHost().newTabSpec("tag1");

		spec.setContent(R.id.listSchedule);
		spec.setIndicator("Schedule Manager");// getResources().getDrawable(R.drawable.list));
		getTabHost().addTab(spec);

		spec = getTabHost().newTabSpec("tag2");
		spec.setContent(R.id.detailSchedule);
		spec.setIndicator("Add Schedule");// ,
											// getResources().getDrawable(R.drawable.alamat));
		getTabHost().addTab(spec);

		getTabHost().setCurrentTab(0);

		getTabHost().setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (tabId.equals("tag1")) {					
					clearForm();
					TabWidget vTabs = getTabWidget();
					LinearLayout rLayout = (LinearLayout) vTabs.getChildAt(1);
					((TextView) rLayout.getChildAt(1)).setText("Add Schedule");
					edited = null;
				} 
			}
		});
		registerForContextMenu(list);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				edited = adapter.getItem(arg2);
				Log.e("edited", edited.getName());
				Intent intent = new Intent(ScheduleActivity.this, ListProductInScheduleActivity.class);
				ScheduleActivity.this.startActivity(intent);
			}
		});
		
	}
	
	public void showProductList(Schedule s){
		Intent intent = new Intent(this, ListProductInScheduleActivity.class);
        this.startActivity(intent);
	}
	public void getAllSchedule(){
		db.open();
		Cursor c = db.getAllSchedule(customer.getUsername());
		//Toast.makeText(ScheduleActivity.this.getApplicationContext(), ""+c.getCount(), Toast.LENGTH_LONG).show();
		String idPembelian = null;
		Schedule s = null;
		while(c.moveToNext()){
			idPembelian = c.getString(0);
			//Toast.makeText(ScheduleActivity.this.getApplicationContext(), c.getString(0)+" - "+c.getColumnCount(), Toast.LENGTH_LONG).show();
			
			s = new Schedule(c.getString(3), c.getString(1), Integer.parseInt(idPembelian), c.getString(2));
			Cursor l = db.getSchedule(Integer.parseInt(idPembelian));
				while(l.moveToNext()){
					//<----- ini minta data ke server buat bikin product--->
					String idProduk = l.getString(0);
					Product p = new Product(idProduk,adapter);
					s.addLineItem(new LineItem(p,Integer.parseInt(l.getString(1))));
				}
				adapter.add(s);			
		}
		db.close();
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;
		edited = adapter.getItem(aInfo.position);
		menu.setHeaderTitle(edited.getName());
		menu.add(0, v.getId(), 0, "Edit");
		menu.add(0, v.getId(), 0, "Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Edit") {
			editSchedule();
		} else if (item.getTitle() == "Delete") {
			deleteSchedule();
		} else {
			return false;
		}
		return true;
	}
	
	public void clearForm() {
		nama.setText("");
		type.clearFocus();
		Calendar cNow= Calendar.getInstance();
        cNow.setLenient(true); 
        date.updateDate(cNow.get(Calendar.YEAR), cNow.get(Calendar.MONTH), cNow.get(Calendar.DAY_OF_MONTH));
		edit = false;
	}
	
	public void editSchedule() {

		TabWidget vTabs = getTabWidget();
		LinearLayout rLayout = (LinearLayout) vTabs.getChildAt(1);
		((TextView) rLayout.getChildAt(1)).setText("Edit Schedule");
		
		edit = true;
		nama.setText(edited.getName());
		int typeID = 4;
		if(edited.getType().equals("Once")){
			typeID = 0;
		}else if(edited.getType().equals("Daily")){
			typeID = 1;
		}else if(edited.getType().equals("Weekly")){
			typeID = 2;
		}else if(edited.getType().equals("Monthly")){
			typeID = 3;
		}
		type.setSelection(typeID);
		String [] temp = edited.getDate().split("-");
		date.updateDate(Integer.parseInt(temp[2]), (Integer.parseInt(temp[1])-1), Integer.parseInt(temp[0]));
		getTabHost().setCurrentTab(1);
	}

	public void deleteSchedule() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ScheduleActivity.this);
		alertDialogBuilder
				.setMessage("Are you sure to delete this schedule?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								//Log.e(customer.getUsername(),edited.getName());
								db.open();
								db.deleteSchedule(customer.getUsername(),edited.getName());
								db.close();
								adapter.remove(edited);
								
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}

	class ScheduleAdapter extends ArrayAdapter<Schedule> {
		ScheduleAdapter() {
			super(ScheduleActivity.this, R.layout.schedule_row, model);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ScheduleManager holder = null;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.schedule_row, parent, false);
				holder = new ScheduleManager(row);
				row.setTag(holder);

			} else {
				holder = (ScheduleManager) row.getTag();
			}

			holder.populateFrom(model.get(position));

			return (row);
		}
	}

	static class ScheduleManager {
		private TextView name = null;
		private TextView type = null;
		private TextView scheduleDate = null;

		ScheduleManager(View row) {
			
			name = (TextView) row.findViewById(R.id.NameScheduleView);
			type = (TextView) row.findViewById(R.id.typeScheduleView);
			scheduleDate = (TextView) row.findViewById(R.id.startScheduleView);
			
		}

		void populateFrom(Schedule r) {
			name.setText(r.getName());
			type.setText("Type : " + r.getType());
			scheduleDate.setText("Start Date : "+r.getDate());
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
        case R.id.saveSchedule:
        	db.open();
        	boolean error = false;
    		String errorField = "";
    		Calendar cNow= Calendar.getInstance();
            cNow.setLenient(true); 
    		if (nama.getText().toString().equals("")) {
    			error = true;
    			errorField = "Name is required";
			}else if (date.getYear() < cNow.get(Calendar.YEAR)) {
				error = true;
				errorField = "Time is invalid";
			}else if (date.getYear() == cNow.get(Calendar.YEAR)){
					if(date.getMonth() < cNow.get(Calendar.MONTH)) {
						error = true;
						errorField = "Time is invalid";
					}else if(date.getMonth() == cNow.get(Calendar.MONTH)){
						if (date.getDayOfMonth() < cNow.get(Calendar.DAY_OF_MONTH)) {
							error = true;
							errorField = "Time is invalid";
						}
					}
			}
    		if(!edit){
	    		Cursor c = db.getScheduleByName(customer.getUsername(), nama.getText().toString());
	    		if(c.moveToFirst()){
	    			error = true;
					errorField = "Name is already exist";
	    		}
    		}else {
    			if(!edited.getName().equalsIgnoreCase(nama.getText().toString())){
	    			Cursor c = db.getScheduleByName(customer.getUsername(), nama.getText().toString());
		    		if(c.moveToFirst()){
		    			error = true;
						errorField = "Name is already exist";
		    		}
    			}
    		}
    		if (error) {
    			Toast.makeText(ScheduleActivity.this.getApplicationContext(), errorField, Toast.LENGTH_LONG).show();
    		} else {
    			
    			Schedule r;
    			if (edit == true){
    				String  temp =  date.getDayOfMonth()+"-"+(date.getMonth()+1)+"-"+date.getYear();
    				r = edited;
    				r.setName(nama.getText().toString());
    				r.setType(type.getSelectedItem().toString());
    				r.setDate(temp);
    				Toast.makeText(ScheduleActivity.this.getApplicationContext(), r.getId()+" "+type.getSelectedItem().toString()+" "+nama.getText().toString()+" "+temp, Toast.LENGTH_LONG).show();
    				db.updateSchedule(r.getId(), type.getSelectedItem().toString(), nama.getText().toString(),temp);
    				
    			}
    			else{
    				String  temp =  date.getDayOfMonth()+"-"+(date.getMonth()+1)+"-"+date.getYear();
    				
    				db.createSchedule(customer.getUsername(), type.getSelectedItem().toString(),  nama.getText().toString(), temp);
    				r = new Schedule(temp, type.getSelectedItem().toString(), db.getLastPembelianID(customer.getUsername()),  nama.getText().toString());
    				
    				
    			}
    			if (edit == false)	adapter.add(r);
    			clearForm();
    			edit = false;
    			adapter.notifyDataSetChanged();
    			getTabHost().setCurrentTab(0);
    			
    		}
    		db.close();
        break;
		}
	}
}
