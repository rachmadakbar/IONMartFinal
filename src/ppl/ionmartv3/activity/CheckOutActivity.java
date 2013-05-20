package ppl.ionmartv3.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.adapter.IONMartDBAdapter;
import ppl.ionmartv3.activity.helper.Global;
import ppl.ionmartv3.activity.session.Customer;
import ppl.ionmartv3.activity.session.LineItem;
import ppl.ionmartv3.activity.session.Product;

import android.os.AsyncTask;
import android.os.Bundle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class CheckOutActivity extends Activity implements View.OnClickListener {

	private Button checkOut;
	private DatePicker date;
	private String addr;
	private TextView saldoView;
	private RadioButton radioAdd;
	private EditText address;
	Context context;
	IONMartDBAdapter db;
	Customer customer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		customer = CustomerHomeActivity.customer;
		db = CustomerHomeActivity.db;
		context = this;
		setContentView(R.layout.activity_check_out);
		saldoView = (TextView) findViewById(R.id.saldo);
		saldoView.setText("Your Saldo : Rp " + customer.getMoney());
		checkOut = (Button) findViewById(R.id.checkOut);
		checkOut.setOnClickListener(this);
		date = (DatePicker) findViewById(R.id.dateSend);
		radioAdd = (RadioButton) findViewById(R.id.defAdd);
		addr = customer.getAddress();
		address = (EditText)findViewById(R.id.addr);
		radioAdd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (radioAdd.isChecked()) {
					addr = customer.getAddress();
				} else {
					addr = address.getText().toString();
				}
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		boolean error = false;
		String errorField = "";
		Calendar cNow = Calendar.getInstance();
		cNow.setLenient(true);
		if (addr.equals("")) {
			error = true;
			errorField = "Address is required";
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
		if (error) {
			Toast.makeText(CheckOutActivity.this.getApplicationContext(),
					errorField, Toast.LENGTH_LONG).show();
		} else {
			checkPassword();
		}

	}

	private void checkOut() {
		db.open();
		double total = CustomerHomeActivity.getTotal();
		db.insertHistory(customer.getUsername(), (new Date()).toString(), total);
		// <-------ini belum------>
		int hargatotal = 0;
		for (LineItem line : CustomerHomeActivity.mShoppingCart) {
			hargatotal += line.getQuantity() * line.getPricePerItem();
		}
		new SendCheckout().execute(customer.getUsername(), hargatotal + "",
				addr);
		db.clearShoppingCart(customer.getUsername());
		db.close();
	}

	private void checkPassword() {

		// TODO Auto-generated method stub
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.password_prompt, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						String password = userInput.getText().toString();
						boolean match = false;
						db.open();
						Cursor c = db.getUser(customer.getUsername());
						if (c.moveToFirst()) {
							if (c.getString(1).equals(password))
								match = true;
						}
						db.close();
						if (match) {
							checkOut();
						} else {
							Toast.makeText(
									CheckOutActivity.this
											.getApplicationContext(),
									"Password is invalid", 3).show();
							checkPassword();
						}
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}

	// ASYNC TASK TO AVOID CHOKING UP UI THREAD DOWNLOAD STRING
	private class SendProductCheckout extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
		}

		protected String doInBackground(String... param) {
			for (LineItem line : CustomerHomeActivity.mShoppingCart) {
				Log.e("test1",
						Global.server
								+ "services.php?ct=product_checkout&idLog="
								+ param[0] + "&idProduk=" + line.getIdProduct()
								+ "&kuantitas=" + line.getQuantity()
								+ "&subtotal=" + line.getPricePerItem()
								* line.getQuantity());
				Global.sendCommand(Global.server
						+ "services.php?ct=product_checkout&idLog=" + param[0]
						+ "&idProduk=" + line.getIdProduct() + "&kuantitas="
						+ line.getQuantity() + "&subtotal="
						+ line.getPricePerItem() * line.getQuantity());
			}
			return null;
		}

		protected void onProgressUpdate(String... progress) {
		}

		protected void onPostExecute(String ret) {
			CustomerHomeActivity.mShoppingCart.clear();
			CartFragment.adapterCart.notifyDataSetChanged();
			Toast.makeText(CheckOutActivity.this.getApplicationContext(),
					"Check Out is already made", 3).show();
			finish();
		}
	}

	// ASYNC TASK TO AVOID CHOKING UP UI THREAD DOWNLOAD STRING
	private class SendCheckout extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
		}

		protected String doInBackground(String... param) {
			Log.e("test", Global.server + "services.php?ct=checkout&username="
					+ param[0] + "&hargaTotal=" + param[1] + "&alamat="
					+ param[2]);
			String idLog = Global.sendCommand(Global.server
					+ "services.php?ct=checkout&username=" + param[0]
					+ "&hargaTotal=" + param[1] + "&alamat=" + param[2]);
			return idLog;
		}

		protected void onProgressUpdate(String... progress) {
		}

		protected void onPostExecute(String ret) {
			new SendProductCheckout().execute(ret);
		}
	}
}
