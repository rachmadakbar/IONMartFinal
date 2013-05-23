package ppl.ionmartv3;

import ppl.ionmartv3.session.Customer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;

public class LihatProfil extends Activity {

	Customer customer;
	IONMartDBAdapter db;
	TextView nama, ttl, alamat, nohp, email, cuscode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profil);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		db = CustomerHomeActivity.db;
		customer = CustomerHomeActivity.customer;
		db.open();
		
		nama = (TextView) findViewById(R.id.nama);
		nama.setText(customer.getName());
		
		ttl = (TextView) findViewById(R.id.ttl);
		//get tgl lahir
		
		alamat = (TextView) findViewById(R.id.Alamat);
		alamat.setText(customer.getAddress());
		
		nohp = (TextView) findViewById(R.id.nohp);
		nohp.setText(customer.getPhone());
		
		email = (TextView) findViewById(R.id.email);
		email.setText(customer.getEmail());
		
		cuscode = (TextView) findViewById(R.id.cuscode);
		//madafaka cuscode.setText(customer.g);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_lihat_profil, menu);
		return true;
	}

}
