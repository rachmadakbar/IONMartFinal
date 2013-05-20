package ppl.ionmartv3.activity;

import java.util.Timer;
import java.util.TimerTask;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.helper.Global;
import ppl.ionmartv3.activity.session.Customer;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class RegisterActivity extends Activity implements View.OnClickListener {

	EditText text;
	Button regButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		regButton = (Button) findViewById(R.id.button1);
		regButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		String warning = "warning:\n";
		String name;
		String username;
		String password;
		String address;
		String city;
		String phone;
		String email;
		boolean error = false;

		text = (EditText) findViewById(R.id.editText1);
		name = text.getText().toString();
		if (name.equals("")) {
			warning = warning + "Full Name needed! \n";
			error = true;
		}

		text = (EditText) findViewById(R.id.editText2);
		username = text.getText().toString();
		if (username.equals("")) {
			warning = warning + "username needed! \n";
			error = true;
		}

		text = (EditText) findViewById(R.id.editText3);
		password = text.getText().toString();
		if (password.equals("")) {
			warning = warning + "password needed! \n";
			error = true;
		}  else if(password.length()<3 || password.length()>20){
			warning = warning + "password length must be between 3 and 20 in length! \n";
		} else if(password.contains("\n") || password.contains(" ")){
			warning = warning + "can't conatin new line or space! \n";
		}

		text = (EditText) findViewById(R.id.editText4);
		address = text.getText().toString();
		if (address.equals("")) {
			warning = warning + "Address needed! \n";
			error = true;
		}

		text = (EditText) findViewById(R.id.editText5);
		city = text.getText().toString();
		if (city.equals("")) {
			warning = warning + "City name needed! \n";
			error = true;
		}

		text = (EditText) findViewById(R.id.editText6);
		phone = text.getText().toString();
		if (phone.equals("")) {
			warning = warning + "Phone Number needed! \n";
			error = true;
		}

		text = (EditText) findViewById(R.id.editText7);
		email = text.getText().toString();
		if (email.equals("")) {
			warning = warning + "Email address needed! \n";
			error = true;
		} else {
			String eadd = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+.[A-Z]{2,4}$";
			if ((!email.matches(eadd))) {
				warning = warning + "Email address invalid! \n";
			}
		}

		if (error) {
			Toast.makeText(RegisterActivity.this.getApplicationContext(), warning,
					Toast.LENGTH_LONG).show();
		} else {
			Customer aCustomer = new Customer(username, password);
			name = name.replace(" ", "%20");
			aCustomer.setName(name);
			address = address.replace(" ", "%20");
			aCustomer.setAddress(address);
			city = city.replace(" ", "%20");
			aCustomer.setCity(city);
			aCustomer.setPhone(phone);
			email = email.replace("@", "%40");
			aCustomer.setEmail(email);
			RadioButton rb = (RadioButton) findViewById(R.id.radio0);
			if (rb.isChecked()) {
				aCustomer.setGender("M");
			} else {
				aCustomer.setGender("F");
			}
			String registered = Global.sendCommand(Global.server+"services.php?ct=add_user&username="+aCustomer.getUsername()+"&password="+aCustomer.getPassword()+"&alamat="+aCustomer.getAddress()+"&nama="+aCustomer.getName()+"&kota="+aCustomer.getCity()+"&no_telp="+aCustomer.getPhone()+"&email="+aCustomer.getEmail()+"&jenis_kelamin="+aCustomer.getGender());
			Log.e("test", registered);
			if(registered.contains("false"))
				Toast.makeText(RegisterActivity.this.getApplicationContext(), "Username or email unavailable",Toast.LENGTH_LONG).show();
			else
			{
				this.finish();
			}
		}
	}
}
