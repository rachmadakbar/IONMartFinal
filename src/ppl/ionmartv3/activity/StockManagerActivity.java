package ppl.ionmartv3.activity;



import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.adapter.IONMartDBAdapter;
import ppl.ionmartv3.activity.helper.Global;
import ppl.ionmartv3.activity.session.Product;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.Preference;
import android.util.Config;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class StockManagerActivity extends TabActivity implements
		View.OnClickListener {
	List<Product> model = new ArrayList<Product>();
	ProductAdapter adapter = null;
	EditText id = null;
	EditText nama = null;
	EditText price = null;
	EditText stock = null;
	EditText discount = null;
	EditText description = null;
	EditText vendor = null;
	byte[] imageProduct = null;
	Button save;
	Button image;
	Product edited = null;
	boolean edit = false;
	private TextView mFilePathTextView;
	private static final int REQUEST_PICK_FILE = 1;
	IONMartDBAdapter db;
	
	public void clearForm() {
		nama.setText("");
		price.setText("");
		stock.setText("");
		id.setText("");
		discount.setText("");
		vendor.setText("");
		description.setText("");
		imageProduct = null;
		edit = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		setContentView(R.layout.main_product_manager);
		db = new IONMartDBAdapter(this);
		nama = (EditText) findViewById(R.id.nama);
		id = (EditText) findViewById(R.id.id);
		price = (EditText) findViewById(R.id.price);
		stock = (EditText) findViewById(R.id.stock);
		discount = (EditText) findViewById(R.id.discount);
		description = (EditText) findViewById(R.id.description);
		vendor = (EditText) findViewById(R.id.vendor);
		save = (Button) findViewById(R.id.save);
		image = (Button) findViewById(R.id.image);
		save.setOnClickListener(this);
		image.setOnClickListener(this);
		mFilePathTextView = (TextView)findViewById(R.id.file_path_text_view);
		ListView list = (ListView) findViewById(R.id.almag);

		adapter = new ProductAdapter();
		list.setAdapter(adapter);
		new ViewProductTask().execute(Global.server+"services.php?ct=browse_product");

		TabSpec spec = getTabHost().newTabSpec("tag1");

		spec.setContent(R.id.almag);
		spec.setIndicator("List Product");// getResources().getDrawable(R.drawable.list));
		getTabHost().addTab(spec);

		spec = getTabHost().newTabSpec("tag2");
		spec.setContent(R.id.details);
		spec.setIndicator("Add Product");// ,
											// getResources().getDrawable(R.drawable.alamat));
		getTabHost().addTab(spec);

		getTabHost().setCurrentTab(0);

		getTabHost().setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (tabId.equals("tag1")) {
					TabWidget vTabs = getTabWidget();
					LinearLayout rLayout = (LinearLayout) vTabs
							.getChildAt(1);
					((TextView) rLayout.getChildAt(1)).setText("Add Product");
					clearForm();
					edited = null;
				} else if (tabId.equals("tag2")) {

				}
			}
		});
		registerForContextMenu(list);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				edited = adapter.getItem(arg2);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						StockManagerActivity.this);
				alertDialogBuilder
						.setTitle(edited.getName())
						.setMessage(
								"Id : " + edited.getId() + "\nPrice: "
										+ edited.getPrice()
										+ "\nDescription : " + edited.getDesc())
						.setCancelable(true)
						.setPositiveButton("Edit",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										editProduct();
									}
								})
						.setNegativeButton("Delete",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										deleteProduct();
									}
								});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.home:
			Intent intent = new Intent(this, StoreManagerHomeActivity.class);
			this.startActivity(intent);
			return true;

		case R.id.customer:
			Intent intent2 = new Intent(this, CustomerManagerActivity.class);
			this.startActivity(intent2);
			return true;

		case R.id.logout:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StockManagerActivity.this);
	        alertDialogBuilder
	        .setTitle("Log out")
	        .setMessage("Are you sure")
	        .setCancelable(true)
	        .setNegativeButton("Yes",new DialogInterface.OnClickListener() {
	            @Override
				public void onClick(DialogInterface dialog,int id) 
	            {
	            	db.open();
	            	Cursor c = db.getActiveSession();
	            	c.moveToFirst();
	            	db.logout(c.getString(0), "M");
	            	db.close();
	            	Intent intent3 = new Intent(getApplicationContext(), AuthorizationActivity.class);
	                startActivity(intent3);
	                finish();

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

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;
		edited = adapter.getItem(aInfo.position);
		menu.setHeaderTitle(edited.getName());
		menu.add(0, v.getId(), 0, "Edit");
		menu.add(0, v.getId(), 0, "Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Edit") {
			editProduct();
		} else if (item.getTitle() == "Delete") {
			deleteProduct();
		} else {
			return false;
		}
		return true;
	}

	public void editProduct() {

		TabWidget vTabs = getTabWidget();
		LinearLayout rLayout = (LinearLayout) vTabs.getChildAt(1);
		((TextView) rLayout.getChildAt(1)).setText("Edit Product");

		edit = true;
		nama.setText(edited.getName());
		price.setText("" + edited.getPrice());
		stock.setText("" + edited.getStock());
		id.setText(edited.getId());
		discount.setText("" + edited.getDiscount());
		vendor.setText(edited.getVendor());
		description.setText(edited.getDesc());
		getTabHost().setCurrentTab(1);
	}

	public void deleteProduct() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				StockManagerActivity.this);
		alertDialogBuilder
				.setMessage("Are you sure to delete this product?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								Global.sendCommand(Global.server+"services.php?ct=delete_product&idProduk="+edited.getId());
								new  ViewProductTask().execute(Global.server+"services.php?ct=browse_product");
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}

	class ProductAdapter extends ArrayAdapter<Product> {
		ProductAdapter() {
			super(StockManagerActivity.this, R.layout.product_row, model);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ListProduct holder = null;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.product_row, parent, false);
				holder = new ListProduct(row);
				row.setTag(holder);

			} else {
				holder = (ListProduct) row.getTag();
			}

			holder.populateFrom(model.get(position));

			return (row);
		}
	}

	static class ListProduct {
		private TextView nama = null;
		private TextView price = null;
		private TextView stock = null;
		private ImageView icon = null;

		ListProduct(View row) {
			nama = (TextView) row.findViewById(R.id.nama);
			price = (TextView) row.findViewById(R.id.price);
			stock = (TextView) row.findViewById(R.id.stock);
			icon = (ImageView) row.findViewById(R.id.icon);
		}

		void populateFrom(Product r) {
			nama.setText(r.getName());
			price.setText("Price : " + r.getPrice());
			stock.setText("Stock : " + r.getStock());
			icon.setImageBitmap(r.getImage());
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
        case R.id.save:
        	boolean error = false;
    		String errorField = "";
    		if (id.getText().toString().equals("")) {
    			error = true;
    			errorField = "Id is required";
    		} else if (nama.getText().toString().equals("")) {
    			error = true;
    			errorField = "Name is required";
    		} else if (price.getText().toString().equals("")) {
    			error = true;
    			errorField = "Price is required";
    		} else if (price.getText().toString().charAt(0) == '-') {
    			error = true;
    			errorField = "Price must positive";
    		} else if (stock.getText().toString().equals("")) {
    			error = true;
    			errorField = "Stock is required";
    		} else if (stock.getText().toString().charAt(0) == '-') {
    			error = true;
    			errorField = "Stock must positive";
    		} else if (description.getText().toString().equals("")) {
    			error = true;
    			errorField = "Description is required";
    		} else if (!discount.getText().toString().equals("")
    				&& discount.getText().toString().charAt(0) == '-') {
    			error = true;
    			errorField = "Discount must positive";
    		}

    		if (error) {
    			Toast.makeText(StockManagerActivity.this.getApplicationContext(), errorField, Toast.LENGTH_LONG).show();
    		} else {
    			Product r;
    			if (edit == true)
    				r = edited;
    			else
    				r = new Product();
    			r.setName(nama.getText().toString());
    			r.setId(id.getText().toString());
    			r.setPrice(Double.parseDouble(price.getText().toString()));
    			if (discount.getText().toString().equals(""))
    				r.setDiscount(0.0);
    			else
	    			r.setDiscount(Double.parseDouble(discount.getText().toString()));
    			r.setVendor(vendor.getText().toString());
    			r.setDesc(description.getText().toString());
    			r.setStock(Integer.parseInt(stock.getText().toString()));
    			if (edit == false)
    			{
    				try {
						uploadProduct(imageProduct, r.id+".jpg", r,"add_product");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    			else
    			{

    				try {
						uploadProduct(imageProduct, r.id+".jpg", r,"edit_product");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    			clearForm();
    			edit = false;
    			getTabHost().setCurrentTab(0);
    			new ViewProductTask().execute(Global.server+"services.php?ct=browse_product");
    		}
        	
        break;
        case R.id.image:
        	Intent intent = new Intent(this, SelectImageActivity.class);
        	startActivityForResult(intent, REQUEST_PICK_FILE);
        break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
			case REQUEST_PICK_FILE:
				if(data.hasExtra(SelectImageActivity.EXTRA_FILE_PATH)) {
					// Get the file path
					File f = new File(data.getStringExtra(SelectImageActivity.EXTRA_FILE_PATH));
					
						FileInputStream fis = null;
				        try {
				            fis = new FileInputStream(f);
				        } catch (FileNotFoundException e) {
				            e.printStackTrace();
				        }
				        Bitmap bm = BitmapFactory.decodeStream(fis);
				        ByteArrayOutputStream stream = new ByteArrayOutputStream();
				        bm.compress(CompressFormat.JPEG, 70, stream);
				        imageProduct = stream.toByteArray();
				        mFilePathTextView.setText(f.getPath());
				}
			}
		}
	}
	


	public boolean uploadProduct(final byte[] imageData, String filename ,Product product,String action) throws Exception{


        String responseString = null;       


        PostMethod method;


        method = new PostMethod(Global.server+"services.php"); 

                org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();

                client.getHttpConnectionManager().getParams().setConnectionTimeout(
                                100000);

                FilePart photo = new FilePart("file", new ByteArrayPartSource( filename, imageData));

                photo.setContentType("image/jpg");
                photo.setCharSet(null);
                String s    =   new String(imageData);
                Part[] parts = { new StringPart("ct", action),
                                new StringPart("stock",product.getStock()+""),
                                new StringPart("diskon",product.getDiscount()+""),
                                new StringPart("idProduk",product.getId()),
                                new StringPart("deskripsi",product.getDesc()),
                                new StringPart("produsen",product.getVendor()),
                                new StringPart("nama",product.getName()+""),
                                new StringPart("harga",product.getPrice()+""),
                                photo
                                };

                method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
                client.executeMethod(method);
                responseString = method.getResponseBodyAsString();
                method.releaseConnection();

                Log.e("httpPost", "Response status: " + responseString);

        if (responseString.equals("SUCCESS")) {
                return true;
        } else {
                return false;
        }
    } 
	
	//ASYNC TASK TO AVOID CHOKING UP UI THREAD DOWNLOAD STRING
	private class ViewProductTask extends AsyncTask<String, String, JSONObject> {

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
	            adapter.clear();
				arr = ret.getJSONArray("products");
	        	for(int ii = 0 ; ii < arr.length();ii++) 
	        	{
	        		JSONObject o = arr.getJSONObject(ii);
	            	String id = o.getString("idProduk");
	            	String name = o.getString("nama");
	            	double price = Double.parseDouble(o.getString("harga"));
	            	int stock = Integer.parseInt(o.getString("stock"));
	            	String description = o.getString("deskripsi");
	            	String vendor = o.getString("produsen");
	            	adapter.add(new Product(id,name,price,stock,vendor,description,adapter));
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
	
	//ASYNC TASK TO AVOID CHOKING UP UI THREAD DOWNLOAD STRING
	private class DeleteProductTask extends AsyncTask<String, String, String> {

	    @Override
	    protected void onPreExecute() {
	    }

	    protected String doInBackground(String... param) {
	        	Global.sendCommand(Global.server+"services.php?ct=delete_product&idProduk="+param[0]);
	        	return null;
	    }

	    protected void onProgressUpdate(String... progress) {
	    }

	    protected void onPostExecute(String ret) {
	    	new ViewProductTask().execute(Global.server+"services.php?ct=browse_product");
	    }
	}
	
	
}