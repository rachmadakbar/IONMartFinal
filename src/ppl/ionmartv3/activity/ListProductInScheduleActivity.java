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
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.adapter.IONMartDBAdapter;
import ppl.ionmartv3.activity.session.LineItem;


public class ListProductInScheduleActivity extends Activity  {
	
	List<LineItem> model = new ArrayList<LineItem>();
	static LineItemAdapter adapter = null;
	LineItem clicked = null;
	static TextView total = null;
	Context context = this;
	static IONMartDBAdapter db;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_product_in_schedule);
		//ListProductInScheduleActivity.this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ListView list = (ListView) findViewById(R.id.listProductInSchedule);
		total = (TextView) findViewById(R.id.totalSchedulePrice);
		adapter = new LineItemAdapter();
		list.setAdapter(adapter);
		registerForContextMenu(list);
		db = CustomerHomeActivity.db;
		getAllLineItem();
		if(adapter.getCount()>0)total.setText("Total Price : "+ScheduleActivity.edited.getTotalPrice());
		//Toast.makeText(ListProductInScheduleActivity.this.getApplicationContext(), ""+ScheduleActivity.edited.getName(), Toast.LENGTH_LONG).show();
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.prompt_edit_quantity, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);

				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUser2);

				// set dialog message
				alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("OK",
					  new DialogInterface.OnClickListener() {
					    @Override
						public void onClick(DialogInterface dialog,int id) {
					    	int quantity = Integer.parseInt(userInput.getText().toString());
					    	clicked.setQuantity(quantity);
					    	db.open();
					    	db.editKuantitas(ScheduleActivity.edited.getId(), clicked.getIdProduct(), quantity);
							total.setText("Total Price : "+ScheduleActivity.edited.getTotalPrice());
							db.close();
					    	adapter.notifyDataSetChanged();
					    }
					  })
					.setNegativeButton("Cancel",
					  new DialogInterface.OnClickListener() {
					    @Override
						public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					    }
					  });

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}
		});
	}
	
	private void getAllLineItem(){
		ArrayList<LineItem> list = ScheduleActivity.edited.getAllLineItem();
		for(int i = 0; i < list.size();i++){
			adapter.add(list.get(i));
		}
	}
	public static void insertLineItem(LineItem line){
		db.open();
		if(!db.isExist(line.getIdProduct(), ScheduleActivity.edited.getId())){
			ScheduleActivity.edited.addLineItem(line);
			adapter.add(line);
		}
		db.insertProductTerkaitPembelian(line.getIdProduct(), ScheduleActivity.edited.getId(), line.getQuantity());
		db.insertProduk(line.getIdProduct());
		total.setText("Total Price : "+ScheduleActivity.edited.getTotalPrice());
		db.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_list_product_in_schedule, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.AddToSchedule:
			Intent intent = new Intent(this, AddProductActivity.class);
			intent.putExtra("mode", "addtoschedule");
			this.startActivity(intent);
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
		clicked = adapter.getItem(aInfo.position);
		menu.setHeaderTitle(clicked.getProductName());
		menu.add(0, v.getId(), 0, "Remove");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Remove") {
			deleteLineItemFromSchedule();
		}else {
			return false;
		}
		return true;
	}

	

	private void deleteLineItemFromSchedule() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ListProductInScheduleActivity.this);
		alertDialogBuilder
				.setMessage("Are you sure to remove this product?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								db.open();
								db.deleteProductFromPembelian(clicked.getIdProduct(), ScheduleActivity.edited.getId());
								adapter.remove(clicked);
								ScheduleActivity.edited.deleteLineItem(clicked);
								total.setText("Total Price : "+ScheduleActivity.edited.getTotalPrice());
								db.close();
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


	class LineItemAdapter extends ArrayAdapter<LineItem> {
		
		LineItemAdapter() {
			super(ListProductInScheduleActivity.this, R.layout.product_scheduled_row, model);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ListLineItem holder = null;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.product_scheduled_row, parent, false);
				holder = new ListLineItem(row);
				row.setTag(holder);
			} else {
				holder = (ListLineItem) row.getTag();
			}

			holder.populateFrom(model.get(position));
			return (row);
		}

		
	}

	static class ListLineItem {
		private TextView name = null;
		private TextView price = null;
		private TextView quantity = null;
		private TextView subTotal = null;
		private ImageView icon = null;
		
				
		ListLineItem(View row) {
			
			name = (TextView) row.findViewById(R.id.namaProductSchedule);
			price = (TextView) row.findViewById(R.id.priceProductSchedule);
			quantity = (TextView) row.findViewById(R.id.kuantitasProductSchedule);
			subTotal = (TextView) row.findViewById(R.id.subTotalProductSchedule);
			icon = (ImageView) row.findViewById(R.id.iconProductSchedule);
		}

		void populateFrom(LineItem r) {
			name.setText(r.getProductName());
			price.setText("Price per item : " + r.getPricePerItem());
			quantity.setText("Quantity : " + r.getQuantity());
			subTotal.setText("subTotal : " + r.getSubTotal());
			icon.setImageBitmap(r.getProduct().getImage());
		
		}
		
	}

}