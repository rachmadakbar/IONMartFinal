package ppl.ionmartv3.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.session.Product;

public class FavoriteProductActivity extends Activity {

	List<Product> model = new ArrayList<Product>();
	static ProductAdapter adapter = null;
	Product clicked = null;
	Context context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite_product);
		// ListProductInScheduleActivity.this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ListView list = (ListView) findViewById(R.id.listFavoriteProduct);
		//model.add(new Product("1", "aa", 1000, 2, adapter));
		//model.add(new Product("1", "aa", 1000, 2, adapter));
		//model.add(new Product("1", "aa", 1000, 2, adapter));
		adapter = new ProductAdapter();
		list.setAdapter(adapter);
		registerForContextMenu(list);
		getAllProduct();
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final Product temp = adapter.getItem(arg2);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FavoriteProductActivity.this);
		        alertDialogBuilder
		        .setTitle("Remove this product from favorite ?")
		        .setMessage(temp.getName())
		        .setCancelable(true)
		        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		            @Override
					public void onClick(DialogInterface dialog,int id) 
		            {
		            	adapter.remove(temp);
		            	/*
		            	 * process ke server
		            	 */
		            	adapter.notifyDataSetChanged();
		            }
		        })
		        .setNegativeButton("No",new DialogInterface.OnClickListener() {
		            @Override
					public void onClick(DialogInterface dialog,int id) {
		                dialog.cancel();
		            }
		        });
		        AlertDialog alertDialog = alertDialogBuilder.create();
		        alertDialog.show();
			}
		});
	}

	
	private void getAllProduct() {
		/*
		 * Ambil dari server
		 * 
		 * */
	}

	class ProductAdapter extends ArrayAdapter<Product> {

		ProductAdapter() {
			super(FavoriteProductActivity.this,
					R.layout.favorite_row, model);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ListProduct holder = null;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.favorite_row, parent,
						false);
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
		private TextView name = null;
		private ImageView icon = null;

		ListProduct(View row) {

			name = (TextView) row.findViewById(R.id.namaProductFavorite);
			icon = (ImageView) row.findViewById(R.id.iconProductFavorite);
		}

		void populateFrom(Product r) {
			name.setText(r.getName());
			icon.setImageBitmap(r.getImage());

		}

	}

}