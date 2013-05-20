package ppl.ionmartv3.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.adapter.IONMartDBAdapter;
import ppl.ionmartv3.activity.adapter.BrowsePagerAdapter;
import ppl.ionmartv3.activity.session.Customer;
import ppl.ionmartv3.activity.session.LineItem;
import ppl.ionmartv3.activity.session.Product;

public class CustomerHomeActivity extends FragmentActivity {
	private Button scanButton = null;
	private PagerAdapter mPagerAdapter;
	public static List<LineItem> mShoppingCart = new ArrayList<LineItem>();
	static IONMartDBAdapter db;
	static Customer customer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new IONMartDBAdapter(this);
		db.open();
		Cursor c  = db.getActiveUser();
		c.moveToFirst();
		customer = new Customer(c.getString(0), c.getString(1));
		customer.setMoney(Long.parseLong(c.getString(8)));
		customer.setAddress(c.getString(2));
		db.close();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.setContentView(R.layout.customerhome_view);
		scanButton = (Button) findViewById(R.id.button2);
		scanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CustomerHomeActivity.this
						.getApplicationContext(), QRScanActivity.class);
				CustomerHomeActivity.this.startActivity(intent);
			}
		});

		// initialize the pager
		this.initialisePaging();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return true;
	}
	public static void getShoppingcart(){
		db.open();
		mShoppingCart.clear();
		Cursor c = db.getShoppingCart(customer.getUsername());
		while(c.moveToNext()){
			String idProduct = c.getString(1);
			Product p = new Product(idProduct,CartFragment.adapterCart);
			mShoppingCart.add(new LineItem(p,Integer.parseInt(c.getString(2))));
		}
		CartFragment.adapterCart.notifyDataSetChanged();
		db.close();
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.topup:
			intent = new Intent(this, ReloadActivity.class);
			this.startActivity(intent);
			return true;

		case R.id.history:
			intent = new Intent(this, HistoryActivity.class);
			this.startActivity(intent);
			return true;
		case R.id.schedule:
			intent = new Intent(this, ScheduleActivity.class);
			this.startActivity(intent);
			return true;
		case R.id.logout:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerHomeActivity.this);
	        alertDialogBuilder
	        .setTitle("Log out")
	        .setMessage("Are you sure")
	        .setCancelable(true)
	        .setNegativeButton("Yes",new DialogInterface.OnClickListener() {
	            @Override
				public void onClick(DialogInterface dialog,int id) 
	            {
	            	db.open();
	    			db.loggedOut(customer.getUsername());
	    			Intent xintent = new Intent(getApplicationContext(), AuthorizationActivity.class);
	    			startActivity(xintent);
	    			finish();
	    			db.close();
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
			
			
			
			
		case R.id.checkout:
			if(mShoppingCart.size()>0)
			{
				if(getTotal()>customer.getMoney()){
					 Toast.makeText(CustomerHomeActivity.this.getApplicationContext(),"Not enough money!\nSaldo : " + customer.getMoney()+ "\nTotal transaction : " + getTotal(), Toast.LENGTH_LONG).show();
				}else{
					intent = new Intent(this, CheckOutActivity.class);
					this.startActivity(intent);
				}
			}
			else
			{
				Toast.makeText(CustomerHomeActivity.this.getApplicationContext(),"No items remains", Toast.LENGTH_LONG).show();
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	static double getTotal() {
		double total = 0;
		for (LineItem line : CustomerHomeActivity.mShoppingCart) {
			total += line.getSubTotal();
		}
		return total;
	}

	/**
	 * Initialize the fragments to be paged
	 */
	private void initialisePaging() {

		List<Fragment> fragments = new Vector<Fragment>();
		fragments
				.add(Fragment.instantiate(this, BrowseFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, CartFragment.class.getName()));
		this.mPagerAdapter = new BrowsePagerAdapter(
				super.getSupportFragmentManager(), fragments);

		ViewPager pager = (ViewPager) findViewById(R.id.awesomepager);
		pager.setAdapter(this.mPagerAdapter);
	}

}