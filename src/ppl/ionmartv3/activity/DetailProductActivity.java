package ppl.ionmartv3.activity;

import org.apache.http.client.utils.CloneUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.helper.Global;
import ppl.ionmartv3.activity.helper.XMLParser;
import ppl.ionmartv3.activity.session.LineItem;
import ppl.ionmartv3.activity.session.Product;

public class DetailProductActivity extends Activity {
	ImageView image;
	TextView prodName;
	TextView prodMake;
	TextView prodStock;
	TextView prodPrice;
	TextView prodDesc;
	Button add;
	Product recentProduct;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.detailproduk_view);
		// Get the message from the intent
		image = (ImageView)findViewById(R.id.imageView2);
		prodName = (TextView) findViewById(R.id.textView1);
		prodMake = (TextView) findViewById(R.id.textView2);
		prodStock = (TextView) findViewById(R.id.textView3);
		prodPrice = (TextView) findViewById(R.id.textView4);
		prodDesc = (TextView) findViewById(R.id.textView6);
		add = (Button) findViewById(R.id.button1);
		
		add.setOnClickListener(new OnClickListener() {
			
			int amount = 0;
			
			@Override
			public void onClick(View arg0) {
				final AlertDialog.Builder alert = new AlertDialog.Builder(DetailProductActivity.this);


				  final Intent intent = getIntent();
				//final Intent intent = getIntent();
				final String mode = intent.getStringExtra("mode");
				 if(mode!=null && mode.contains("addtoschedule")) {
					alert.setTitle("Add To Schedule");
				}
				else {
					alert.setTitle("Add To Cart");
				}
				alert.setMessage("Quantity :");

				// Set an EditText view to get user input 
				final EditText input = new EditText(DetailProductActivity.this);
				input.setInputType(InputType.TYPE_CLASS_PHONE);
				alert.setView(input);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String number = "^([1-9])+[0-9]*";
				  String value = input.getEditableText().toString();
				  
				    String selectedid = intent.getStringExtra("selectedid");
				if(value.matches(number))
				{
				  amount = Integer.parseInt(value);
				  if(recentProduct.getStock() < amount ){
					  Toast.makeText(DetailProductActivity.this.getApplicationContext(), "Item is not available for that amount",Toast.LENGTH_LONG).show();
					  //alert.setTitle("Warning");
					  //alert.setMessage("Item is not available for that amount");
					  //alert.show();
					  
				  }
				  else{
					//intent = getIntent();
					  String mode = "";
					  mode = intent.getStringExtra("mode");
					  if(mode!=null && mode.contains("addtoschedule")) 
						  ListProductInScheduleActivity.insertLineItem(new LineItem(recentProduct,amount));
					  else
					  {
						  CartFragment.adapterCart.notifyDataSetChanged();
						  String username = CustomerHomeActivity.customer.getUsername();
						  CustomerHomeActivity.db.open();
						  CustomerHomeActivity.db.insertProduk(recentProduct.id);
						  CustomerHomeActivity.db.insertToShoppingCart(recentProduct.id, username, amount);
						  CustomerHomeActivity.db.close();
						  CustomerHomeActivity.getShoppingcart();
						  
						  CartFragment.adapterCart.notifyDataSetChanged();
					  }
					  DetailProductActivity.this.finish();
				  }
				}
				  else if(value.length()<1) {
					  Toast.makeText(DetailProductActivity.this.getApplicationContext(), "Input invalid",Toast.LENGTH_LONG).show();
				  }
				}
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
				  }
				});
				alert.show();			
			}
		});
		
	    Intent intent = getIntent();
	    String selectedid = intent.getStringExtra("selectedid");
	    new BrowseTask().execute(Global.server+"services.php?ct=select_product&id="+selectedid);

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
	    	try{
		        if (ret != null) {
		            	String id = ret.getString("idProduk");
		            	String name = ret.getString("nama");
		            	double price = Double.parseDouble(ret.getString("harga"));
		            	int stock = Integer.parseInt(ret.getString("stock"));
		        	    String produsen = ret.getString("produsen"); // cost child value
		        	    String deskripsi = ret.getString("deskripsi");
	
		        	    prodName.setText(name);
		        	    prodMake.setText(produsen);
		        	    prodStock.setText(stock+" buah");
		        	    prodPrice.setText("Rp"+price);
		        	    prodDesc.setText(deskripsi);
		        	    recentProduct = new Product(id, name,price,stock, null);
		        	    new ImageDownloader().execute(id);
		        	    //Toast.makeText(ProductDetailActivity.this.getApplicationContext(), id, Toast.LENGTH_SHORT);
				}
	        } catch (JSONException e) {
					e.printStackTrace();
				}
	    }
	}
	private class ImageDownloader extends AsyncTask<String,String,Bitmap> {

        protected Bitmap doInBackground(String... str) {
            // TODO Auto-generated method stub
        	Bitmap bitmap =  Global.downloadBitmap(Global.server+"product_img/"+str[0]+".PNG");
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called");

        }

        protected void onPostExecute(Bitmap result) {
    			image.setImageBitmap(result);
        }
    }

}
