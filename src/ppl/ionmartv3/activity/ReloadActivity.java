package ppl.ionmartv3.activity;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.helper.Global;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
//import android.view.Menu;
//import com.wilis.array3.StockManagerActivity;

public class ReloadActivity extends Activity {
	Button   mButton;
	EditText mEdit;
	EditText mEdit2;
	EditText mEdit3;
	Spinner  mSpinner;
	String nominal;
	String rekening;
	String bank;
	String transid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//remove title bar

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reload);
		 mButton = (Button)findViewById(R.id.button1);
		 mEdit   = (EditText)findViewById(R.id.editText1);
		 mEdit2  = (EditText)findViewById(R.id.editText2);
		 mEdit3  = (EditText)findViewById(R.id.editText3);
		 mSpinner = (Spinner)findViewById(R.id.spinner1);
		 
		 mButton.setOnClickListener(
			        new View.OnClickListener()
			        {
			            @SuppressLint("ShowToast")
						public void onClick(View view)
			            {
			            	//get items after the button has been clicked
			            	
			                nominal =  mEdit.getText().toString();
			                rekening =  mEdit2.getText().toString();
			                transid =  mEdit3.getText().toString();
			                bank = mSpinner.getSelectedItem().toString();
			                boolean error = false;
			        		String errorField = "Cannot be empty!";
			        		if(nominal.equals("")){
			        			error = true;
			        			errorField="Nominal is Required";
			        		}else if(rekening.equals("")){
			        			error = true;
			        			errorField="Bank Account is required";
			        		}else if(transid.equals("")){
			        			error = true;
			        			errorField="Transfer ID is required";
			        		}
			        		if(error){
			        			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReloadActivity.this);
			        			alertDialogBuilder
			        			.setTitle("Error")
			        			.setMessage(errorField)
			        			.setNeutralButton("Close",new DialogInterface.OnClickListener() {
			        				
			        				@Override
			        				public void onClick(DialogInterface arg0, int arg1) {
			        					
			        				}
			        			}).show();
			        		}
			        		else{
			        			//<----send data ke server----->
			        			String debug =  Global.server+"services.php?ct=request_topup&username="+CustomerHomeActivity.customer.getUsername()+"&nominal="+nominal+"&namaBank="+bank+"&no_rek="+rekening+"&no_referensi=000";
				        		String response = Global.sendCommand(Global.server+"services.php?ct=request_topup&username="+CustomerHomeActivity.customer.getUsername()+"&nominal="+nominal+"&namaBank="+bank+"&no_rek="+rekening+"&no_referensi=000");
				                if(response.contains("true"))
				                	{
				                		Toast.makeText(ReloadActivity.this.getApplicationContext(),"Request has been sent!", Toast.LENGTH_LONG).show();
				                		Log.e("checkout", debug);
				                		
				                		finish();
				                	}
				                else Toast.makeText(ReloadActivity.this.getApplicationContext(),"Request is failed", Toast.LENGTH_LONG).show();
			        		}
			            }
			        });
	}

	//@Override
	//public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.activity_reload, menu);
	//	return true;
	//}

}
