package ppl.ionmartv3.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.adapter.BrowseProductAdapter;
import ppl.ionmartv3.activity.helper.Global;
import ppl.ionmartv3.activity.session.Product;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddProductActivity extends Activity {
	private List<Product>  mListProduct;
	private BrowseProductAdapter adapter;
	private EditText searchText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);
		GridView myView =  (GridView) findViewById(R.id.gridview);
		searchText = (EditText) findViewById(R.id.cariText);
		mListProduct = new ArrayList<Product>();
		new BrowseTask().execute(Global.server+"services.php?ct=browse_product");
		adapter = new BrowseProductAdapter(this,mListProduct);
		
		myView.setAdapter(adapter);
		myView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getApplicationContext(), DetailProductActivity.class);
	            intent.putExtra("mode", "addtoschedule");
	            intent.putExtra("selectedid", ((Product)mListProduct.get(arg2)).id+"");
	            startActivity(intent);
			}
		});
		searchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				mListProduct.clear();
				Toast.makeText(getApplicationContext(),"this",Toast.LENGTH_SHORT).show();
				new BrowseTask().execute(Global.server+"services.php?ct=search_product&key="+searchText.getText().toString());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_add_product, menu);
		return true;
	}
	//ASYNC TASK TO AVOID CHOKING UP UI THREAD DOWNLOAD STRING
	private class BrowseTask extends AsyncTask<String, String, JSONObject> {

	    @Override
	    protected void onPreExecute() {
	    }

	    protected JSONObject doInBackground(String... param) {
	        try {
	            JSONObject o = Global.getJSONFromUrl(param[0]);
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
				arr = ret.getJSONArray("products");
	        	for(int ii = 0 ; ii < arr.length();ii++) 
	        	{
	        		JSONObject o = arr.getJSONObject(ii);
	            	String id = o.getString("idProduk");
	            	String name = o.getString("nama");
	            	double price = Double.parseDouble(o.getString("harga"));
	            	int stock = Integer.parseInt(o.getString("stock"));
	            	mListProduct.add(new Product(id,name,price,stock,adapter));
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
