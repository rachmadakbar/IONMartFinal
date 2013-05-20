package ppl.ionmartv3.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.*;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ppl.ionmartv3.activity.adapter.IONMartDBAdapter;
import ppl.ionmartv3.activity.helper.*;
import ppl.ionmartv3.activity.session.Customer;
import ppl.ionmartv3.R;

public class AuthorizationActivity extends Activity {
	
	EditText usernameEdit = null;
	EditText passwordEdit = null;
	Button loginButton = null;
	Button register = null;
	IONMartDBAdapter db = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new IONMartDBAdapter(this);
		db.open();
		//db.dropDatabase();
		Cursor c = db.getActiveUser();
		
		if(c.moveToFirst()){
			Intent intent = null;
			if(c.getString(7).equalsIgnoreCase("C")){
				intent = new Intent(AuthorizationActivity.this.getApplicationContext(), CustomerHomeActivity.class);
			}else intent = new Intent(AuthorizationActivity.this.getApplicationContext(), StoreManagerHomeActivity.class);
			db.close();
			AuthorizationActivity.this.startActivity(intent);
			finish();
		}
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.authorization_view);
		usernameEdit = (EditText) findViewById(R.id.username);
		passwordEdit = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.sign_in_button);
		register = (Button) findViewById(R.id.sign_up_button);
		
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intentx = new Intent(AuthorizationActivity.this.getApplicationContext(), RegisterActivity.class);
				AuthorizationActivity.this.startActivity(intentx);
				
			}
		});
		
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String username = usernameEdit.getText().toString();
				String password = passwordEdit.getText().toString();
				//validasi username
				if(!username.matches("[a-zA-Z0-9]*")){
					Toast.makeText(AuthorizationActivity.this.getApplicationContext(), "Username is empty or not valid",Toast.LENGTH_LONG).show();
				//validasi password
				}else if (!password.matches("[a-zA-Z0-9]*")){
					Toast.makeText(AuthorizationActivity.this.getApplicationContext(), "Password is empty or not valid",Toast.LENGTH_LONG).show();
				}else{
					//validasi berhasil
					XMLParser parser = new XMLParser();
					JSONObject o = Global.getJSONFromUrl(Global.server+"services.php?ct=login&username="+username+"&password="+password);
					if(o!=null)
					{
						try {
							db.open();
							Cursor c = db.getUser(o.getString("username"));
							Intent intent = null;
			            	String role = o.getString("role");
							if(c.moveToFirst()){
								db.updateAkun(o.getString("username"), o.getString("password"), Double.parseDouble(o.getString("saldo")));
								db.loggedIn(o.getString("username"));
							}else{
								if(role.contains("C"))
									db.insertAkun(o.getString("username"), o.getString("password"), o.getString("alamat"), "", "","", true, "C", Double.parseDouble(o.getString("saldo")));
								else
									db.insertAkun(o.getString("username"), o.getString("password"),  o.getString("alamat"), "", "","", true, "M", Double.parseDouble(o.getString("saldo")));
							}
							db.close();
			            	if(role.contains("C"))
							{
			            		intent = new Intent(AuthorizationActivity.this.getApplicationContext(), CustomerHomeActivity.class);
							}
			            	else if(role.contains("M"))
			            	{
			            		intent = new Intent(AuthorizationActivity.this.getApplicationContext(), StoreManagerHomeActivity.class);
			            	}
							AuthorizationActivity.this.startActivity(intent);
							finish();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else Toast.makeText(AuthorizationActivity.this.getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		
		
	}
}
