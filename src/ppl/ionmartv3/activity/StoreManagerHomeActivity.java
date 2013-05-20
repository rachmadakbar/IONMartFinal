package ppl.ionmartv3.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.adapter.IONMartDBAdapter;
import ppl.ionmartv3.activity.session.Log;

public class StoreManagerHomeActivity extends Activity {
	List<Log> model=new ArrayList<Log>();
	LogAdapter adapter=null;
	IONMartDBAdapter db;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		db = new IONMartDBAdapter(this);
		Log p = new Log("TopUp","Rachmad");
		model.add(p);
		p = new Log("TopUp","Raja");
		model.add(p);
		setContentView(R.layout.activity_store_manager_home);	
		ListView list=(ListView)findViewById(R.id.list);
		
		adapter=new LogAdapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				final Log temp = adapter.getItem(arg2);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StoreManagerHomeActivity.this);
		        alertDialogBuilder
		        .setTitle("Process this request?")
		        .setMessage(temp.getId()+"\n"+temp.getDescription()+"\n"+temp.getDate())
		        .setCancelable(true)
		        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		            @Override
					public void onClick(DialogInterface dialog,int id) 
		            {
		            	adapter.remove(temp);
		            	//processLog(temp);
		            }
		        })
		        .setNegativeButton("No",new DialogInterface.OnClickListener() {
		            @Override
					public void onClick(DialogInterface dialog,int id) {
		                dialog.cancel();
		            }
		        });
		        AlertDialog alertDialog = alertDialogBuilder.create();
		        alertDialog.show();
				// TODO Auto-generated method stub
				
			}
		});
	}
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) 
	    {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.main_menu, menu);
	        return true;
	    }
	 	/*
	 	public void processLog(Log a){
	 		if(a.getId().endsWith(TopUp))
	 		Intent intent = new Intent(this, StockManagerActivity.class);
	 		intent.putExtra("Log", a);
	 		//parsing = getIntent().getSerializableExtra("Log");
            this.startActivity(intent);
	 	}
	 */
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) 
	    {
	        switch (item.getItemId()) 
	        {
	            case R.id.product:
	            	Intent intent = new Intent(this, StockManagerActivity.class);
	                this.startActivity(intent);
	    			this.finish();
	                return true;

	            case R.id.customer:
	            	Intent intent2 = new Intent(this, CustomerManagerActivity.class);
	                this.startActivity(intent2);
	    			this.finish();
	                return true;

	            case R.id.logout:
	            	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StoreManagerHomeActivity.this);
	    	        alertDialogBuilder
	    	        .setTitle("Log out")
	    	        .setMessage("Are you sure")
	    	        .setCancelable(true)
	    	        .setNegativeButton("Yes",new DialogInterface.OnClickListener() {
	    	            @Override
	    				public void onClick(DialogInterface dialog,int id) 
	    	            {
	    	            	db.open();
	    	            	Cursor c = db.getActiveUser();
	    	            	c.moveToFirst();
	    	            	db.loggedOut(c.getString(0));
	    	            	db.close();
	    	            	Intent intent3 = new Intent(getApplicationContext(), AuthorizationActivity.class);
	    	                startActivity(intent3);
	    	                finish();

	    	            }
	    	        })
	    	        .setPositiveButton("No",new DialogInterface.OnClickListener() {
	    	            @Override
	    				public void onClick(DialogInterface dialog,int id) {
	    	                dialog.cancel();
	    	            }
	    	        });
	    	        AlertDialog alertDialog = alertDialogBuilder.create();
	    	        alertDialog.show();
	                return true;
	            default:
	                return super.onOptionsItemSelected(item);
	        }
	    } 
	
	
	class LogAdapter extends ArrayAdapter<Log> {
		LogAdapter() {
			super(StoreManagerHomeActivity.this, R.layout.log_row, model);
		}
		
		@Override
		public View getView(int position, View convertView,ViewGroup parent) {
			View row=convertView;
			ListLog holder=null;
			
			if (row==null) {													
				LayoutInflater inflater=getLayoutInflater();	
				row=inflater.inflate(R.layout.log_row, parent, false);
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
		private TextView rowDesc=null;
		private TextView rowDate=null;
		
		ListLog(View row) {
			rowId=(TextView)row.findViewById(R.id.rowId);
			rowDesc=(TextView)row.findViewById(R.id.rowDesc);
			rowDate=(TextView)row.findViewById(R.id.rowDate);
		}
		
		void populateFrom(Log r) {
			rowId.setText(r.getId());
			rowDesc.setText("User : "+r.getDescription());
			rowDate.setText("Date : "+r.getDate());
		}
	}	
}
