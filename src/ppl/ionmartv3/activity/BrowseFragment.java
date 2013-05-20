package ppl.ionmartv3.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.adapter.BrowseProductAdapter;
import ppl.ionmartv3.activity.helper.Global;
import ppl.ionmartv3.activity.helper.XMLParser;
import ppl.ionmartv3.activity.session.*;


public class BrowseFragment extends Fragment {
private View myFragmentView;
private List<Product>  mListProduct;
private BrowseProductAdapter adapter;
private EditText searchText;
/**
* (non-Javadoc)
*
* @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
* android.view.ViewGroup, android.os.Bundle)
*/
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
	myFragmentView = inflater.inflate(R.layout.browse_view, container, false);
	GridView myView =  (GridView) myFragmentView.findViewById(R.id.gridview);
	searchText = (EditText) myFragmentView.findViewById(R.id.cariText);
	mListProduct = new ArrayList<Product>();
	new BrowseTask().execute(Global.server+"services.php?ct=browse_product");
	adapter = new BrowseProductAdapter(this.myFragmentView.getContext(),mListProduct);
	
	myView.setAdapter(adapter);
	myView.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(BrowseFragment.this.getActivity().getApplicationContext(), DetailProductActivity.class);
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
			new BrowseTask().execute(Global.server+"services.php?ct=search_product&key="+searchText.getText().toString());
		}
	});
	return myFragmentView;
}



class MyOnClickListener implements OnClickListener
{
	private final int position;
	public MyOnClickListener(int position)
	{
		this.position = position;
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(myFragmentView.getContext(),"You click "+position, Toast.LENGTH_LONG).show();
	}
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
				mListProduct.clear();
			}
        } else {
            Log.e("ImageLoadTask", "Failed to load data");
        }
    }
}
}