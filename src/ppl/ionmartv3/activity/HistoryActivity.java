package ppl.ionmartv3.activity;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.adapter.IONMartDBAdapter;
import ppl.ionmartv3.activity.session.Customer;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HistoryActivity extends Activity {

	Customer customer;
	IONMartDBAdapter db;
	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		customer = CustomerHomeActivity.customer;
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_history);
		TableLayout tableview = (TableLayout) findViewById(R.id.tableview);
	    tableview.setPadding(0, 0, 0, 0);
	    
	    /*<---- ini ambil dari server -->
	    while(c.moveToNext()){
	            TableRow row = new TableRow(this);
	            TableRow row2 = new TableRow(this);
	            TableRow row3 = new TableRow(this);
	            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(
	                    TableLayout.LayoutParams.FILL_PARENT,
	                    TableLayout.LayoutParams.WRAP_CONTENT);
	            row.setLayoutParams(lp);
	            row2.setLayoutParams(lp);
	            row3.setLayoutParams(lp);
	            row.setPadding(0, 0, 0, 0);
	            row2.setPadding(0, 0, 0, 0);
	            row3.setPadding(0, 0, 0, 0);
	            row.setBackgroundColor(Color.parseColor("#E5E5E5"));
	            row2.setBackgroundColor(Color.parseColor("#E5E5E5"));
	            row3.setBackgroundColor(Color.parseColor("#E5E5E5"));

	            TextView Header = new TextView(this);
	            TextView Dalem = new TextView(this);
	            ImageView iv = new ImageView(this);

	            //Header.setGravity(Gravity.CENTER);
	            Header.setText("RP: "+ c.getString(1) + ",-");
	            Header.setTextSize(20.0f);
	            Header.setPadding(3, 3, 3, 3);
	            Header.setTextColor(Color.parseColor("#000000"));
	            Header.setTypeface(null, Typeface.BOLD);
	            Dalem.setText(c.getString(0));
	            Dalem.setTextSize(21.0f);
	            Dalem.setPadding(3, 3, 3, 3);
	            Dalem.setTextColor(Color.parseColor("#0099E5"));
	            Dalem.setTypeface(null, Typeface.BOLD);
	            iv.setImageResource(R.drawable.line);
	            
	            
	            

	            row.addView(Header);
	            row2.addView(Dalem);
	            row3.addView(iv);
	            tableview.addView(row2);
	            tableview.addView(row);
	            tableview.addView(row3);
	            
	    }*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_history, menu);
		return true;
	}

}
