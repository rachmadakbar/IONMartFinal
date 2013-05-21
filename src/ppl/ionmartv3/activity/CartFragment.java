package ppl.ionmartv3.activity;

import java.util.ArrayList;
import java.util.List;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.adapter.CartProductAdapter;
import ppl.ionmartv3.activity.adapter.IONMartDBAdapter;
import ppl.ionmartv3.activity.helper.Global;
import ppl.ionmartv3.activity.session.Customer;
import ppl.ionmartv3.activity.session.LineItem;
import ppl.ionmartv3.activity.session.Product;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CartFragment extends Fragment {
	private View myFragmentView;
	public static CartProductAdapter adapterCart;

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(R.layout.cart_view, container,
				false);

		ListView myView = (ListView) myFragmentView.findViewById(R.id.listView);

		if (adapterCart == null)
			adapterCart = new CartProductAdapter(this.getActivity()
					.getApplicationContext(),
					CustomerHomeActivity.mShoppingCart);
		myView.setAdapter(adapterCart);
		CustomerHomeActivity.getShoppingcart();
		registerForContextMenu(myView);
		adapterCart.notifyDataSetChanged();
		return myFragmentView;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.listView) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(CustomerHomeActivity.mShoppingCart.get(
					info.position).getProductName());
			menu.add(Menu.NONE, 0, 0, "Edit");
			menu.add(Menu.NONE, 1, 1, "Delete");
		}
	}

	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();

		if (menuItemIndex == 0) {

			AlertDialog.Builder alert = new AlertDialog.Builder(
					this.getActivity());

			alert.setTitle("Ganti jumlah");
			alert.setMessage("Berapa?");

			// Set an EditText view to get user input
			
			final EditText input = new EditText(this.getActivity());
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							String value = input.getEditableText().toString();
							int amount;
							amount = Integer.parseInt(value);
							IONMartDBAdapter db = CustomerHomeActivity.db;
							Customer customer = CustomerHomeActivity.customer;
							LineItem l = CustomerHomeActivity.mShoppingCart
									.get(info.position);
							if(l.getQuantity()<amount){
								Toast.makeText(CartFragment.this.getActivity(), "Item is not available for that amount", Toast.LENGTH_LONG).show();
							} else {
								db.open();
								db.insertToShoppingCart(l.getIdProduct(), customer.getUsername(), amount);
								l.setQuantity(amount);
								adapterCart.notifyDataSetChanged();
								db.close();
							}
							
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});
			alert.show();
		} else if (menuItemIndex == 1) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this.getActivity());
			alertDialogBuilder.setTitle("Delete?");
			alertDialogBuilder.setMessage("Delete "
					+ CustomerHomeActivity.mShoppingCart.get(info.position)
							.getProductName() + "?");
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// if this button is clicked, close
							// current activity
							IONMartDBAdapter db = CustomerHomeActivity.db;
							db.open();
							Customer customer = CustomerHomeActivity.customer;
							LineItem l = CustomerHomeActivity.mShoppingCart
									.get(info.position);
							db.deleteProduct(l.getIdProduct(),customer.getUsername());
							Log.e("before",
									CustomerHomeActivity.mShoppingCart.size()
											+ "");
							LineItem x = CustomerHomeActivity.mShoppingCart
									.remove(info.position);
							CustomerHomeActivity.getShoppingcart();
							// adapterCart = new
							// CartProductAdapter(CartFragment.this.getActivity(),CustomerHomeActivity.mShoppingCart);
							Log.e("after test delete",
									CustomerHomeActivity.mShoppingCart.size()
											+ "");
							Log.e("delete", x.getIdProduct());
							adapterCart.notifyDataSetChanged();
							db.close();
							// Toast.makeText(getActivity().getApplicationContext(),
							// "product deleted", Toast.LENGTH_SHORT);
						}
					});
			alertDialogBuilder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// if this button is clicked, just close
							// the dialog box and do nothing
							dialog.cancel();
						}
					});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}
		return true;
	}
}
