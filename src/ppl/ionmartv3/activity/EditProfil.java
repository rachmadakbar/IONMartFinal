package ppl.ionmartv3;

import ppl.ionmartv3.session.Customer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;

public class EditProfil extends Activity {

	Customer customer;
	IONMartDBAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profil);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		db = CustomerHomeActivity.db;
		customer = CustomerHomeActivity.customer;
		db.open();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_profil, menu);
		return true;
	}

}
