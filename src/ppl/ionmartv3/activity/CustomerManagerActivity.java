package ppl.ionmartv3.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.adapter.IONMartDBAdapter;
import ppl.ionmartv3.activity.helper.Global;
import ppl.ionmartv3.activity.session.*;

public class CustomerManagerActivity extends Activity {
	Context context = this;
	List<Customer> model = new ArrayList<Customer>();
	CustomerAdapter adapter = null;
	Customer edited = null;
	IONMartDBAdapter db;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_customer_manager);
		db = new IONMartDBAdapter(this);
		ListView list = (ListView) findViewById(R.id.listCustomer);
		adapter = new CustomerAdapter();
		list.setAdapter(adapter);
		new ViewUserTask().execute();
		registerForContextMenu(list);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.product:
			Intent intent = new Intent(this, StockManagerActivity.class);
			this.startActivity(intent);
			this.finish();
			return true;

		case R.id.home:
			Intent intent2 = new Intent(this, StoreManagerHomeActivity.class);
			this.startActivity(intent2);
			this.finish();
			return true;

		case R.id.logout:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerManagerActivity.this);
	        alertDialogBuilder
	        .setTitle("Log out")
	        .setMessage("Are you sure")
	        .setCancelable(true)
	        .setNegativeButton("Yes",new DialogInterface.OnClickListener() {
	            @Override
				public void onClick(DialogInterface dialog,int id) 
	            {
	            	db.open();
	            	Cursor c = db.getActiveSession();
	            	c.moveToFirst();
	            	db.logout(c.getString(0), "M");
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
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		   super.onCreateContextMenu(menu, v, menuInfo);
		   AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo; 
		   	   edited = adapter.getItem(aInfo.position);
		       menu.setHeaderTitle(edited.getUsername());  
		       menu.add(0, v.getId(), 0, "Add Money");  
		       menu.add(0, v.getId(), 0, "Banned");
		       menu.add(0, v.getId(), 0, "Unbanned");
		   }
	   
	   @Override
	public boolean onContextItemSelected(MenuItem item) {  
	        if(item.getTitle()=="Add Money"){editMoney();}  
	        else if(item.getTitle()=="Banned"){bannedCustomer();} 
	        else if(item.getTitle()=="Unbanned"){unbannedCustomer();}
	        else {return false;}  
	    return true;  
	    }

	private void bannedCustomer() {
		// TODO Auto-generated method stub
		Global.sendCommand(Global.server+"services.php?ct=ban_user&username="+edited.getUsername());
		new ViewUserTask().execute();
	}
	
	private void unbannedCustomer() {
		// TODO Auto-generated method stub
		Global.sendCommand(Global.server+"services.php?ct=unban_user&username="+edited.getUsername());
		new ViewUserTask().execute();
	}

	private void editMoney() {
		
		// TODO Auto-generated method stub
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.prompt, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    @Override
				public void onClick(DialogInterface dialog,int id) {
			    	if(!userInput.getText().toString().equals("")){
			    	double add = Double.parseDouble(userInput.getText().toString());
				    	if((edited.getMoney()+add)>=0){
				    		Global.sendCommand(Global.server+"services.php?ct=add_saldo&username="+edited.getUsername()+"&amount="+add);
				    		new ViewUserTask().execute();
				    	}
				    	else{
				    		Toast.makeText(getApplicationContext(), edited.getUsername()+"'s money now is :" + edited.getMoney() + "\n Total money couldn't be negative!",Toast.LENGTH_SHORT).show();
				    	}
			    	}else{
			    		Toast.makeText(getApplicationContext(), "Input invalid",Toast.LENGTH_SHORT).show();
			    	}
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    @Override
				public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	class CustomerAdapter extends ArrayAdapter<Customer> {
		CustomerAdapter() {
			super(CustomerManagerActivity.this, R.layout.customer_row, model);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			CustomerLog holder = null;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.customer_row, parent, false);
				holder = new CustomerLog(row);
				row.setTag(holder);

			} else {
				holder = (CustomerLog) row.getTag();
			}

			holder.populateFrom(model.get(position));

			return (row);
		}
	}

	static class CustomerLog {
		private TextView username = null;
		private TextView saldo = null;
		private TextView status = null;

		CustomerLog(View row) {
			username = (TextView) row.findViewById(R.id.username);
			saldo = (TextView) row.findViewById(R.id.saldo);
			status = (TextView) row.findViewById(R.id.status);
		}

		void populateFrom(Customer r) {
			username.setText(r.getUsername());
			saldo.setText("Money  : " + r.getMoney());
			status.setText("Status : " + r.getStatus());
		}
	}
	
	//ASYNC TASK TO AVOID CHOKING UP UI THREAD DOWNLOAD STRING
		private class ViewUserTask extends AsyncTask<String, String, JSONObject> {

		    @Override
		    protected void onPreExecute() {
		    }

		    protected JSONObject doInBackground(String... param) {
		        try {
		            JSONObject o = Global.getJSONFromUrl(Global.server+"services.php?ct=browse_user");
		            return o;
		        } catch (Exception e) {
		            e.printStackTrace();
		            return null;
		        }
		    }

		    protected void onProgressUpdate(String... progress) {
		    }

		    protected void onPostExecute(JSONObject ret) {
		        if (ret != null) {
		        	JSONArray arr = null;
					try {
		            adapter.clear();
					arr = ret.getJSONArray("persons");
		        	for(int ii = 0 ; ii < arr.length();ii++) 
		        	{
		        		JSONObject o = arr.getJSONObject(ii);
		        		Customer customer = new Customer(o.getString("username"), "");
		            	customer.setMoney(Long.parseLong(o.getString("saldo")));
		            	customer.setStatus(o.getString("status"));
		            	customer.setAddress(o.getString("alamat"));
		            	adapter.add(customer);
					}
		            if (adapter != null) {
		                adapter.notifyDataSetChanged();
		                //Toast.makeText(BrowseFragment.this.getActivity().getApplicationContext(), mListProduct.size()+"",Toast.LENGTH_SHORT).show();
		            }

					} catch (JSONException e) {
						e.printStackTrace();
					}
		        } else {
		            Log.e("ImageLoadTask", "Failed to load data");
		        }
		    }
		}
}
