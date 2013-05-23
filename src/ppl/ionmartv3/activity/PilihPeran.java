package ppl.ionmartv3;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PilihPeran extends Activity {
	
	Button sm,customer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pilih_peran);
		
		sm = (Button) findViewById(R.id.buttonSM);
		customer = (Button) findViewById(R.id.buttonCus);
		
		customer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intentx = new Intent(PilihPeran.this.getApplicationContext(), CustomerHomeActivity.class);
				PilihPeran.this.startActivity(intentx);
				
			}
		});
		
		sm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intenty = new Intent(PilihPeran.this.getApplicationContext(), StoreManagerHomeActivity.class);
				PilihPeran.this.startActivity(intenty);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_pilih_peran, menu);
		return true;
	}

}
