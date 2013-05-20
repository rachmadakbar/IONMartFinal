package ppl.ionmartv3.activity.adapter;

import java.util.List;

import ppl.ionmartv3.R;
import ppl.ionmartv3.R.id;
import ppl.ionmartv3.R.layout;
import ppl.ionmartv3.activity.session.LineItem;
import ppl.ionmartv3.activity.session.Product;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CartProductAdapter extends BaseAdapter
{
	private Context mContext;
	private List<LineItem> mListProduct;
	
	
	public CartProductAdapter(Context c, List<LineItem> list){
		mContext = c;
		mListProduct =list;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListProduct.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mListProduct.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		LineItem entry = mListProduct.get(arg0);
		if(arg1 == null){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			arg1 = inflater.inflate(R.layout.layout_cart,null);
		}
		ImageView prodIcon = (ImageView) arg1.findViewById(R.id.productIcon);
		prodIcon.setImageBitmap(entry.getProduct().getImage());
		
		TextView prodName = (TextView) arg1.findViewById(R.id.productName);
		prodName.setText(entry.getProductName());
		
		TextView prodPrice = (TextView) arg1.findViewById(R.id.productPrice);
		prodPrice.setText("Rp "+entry.getPricePerItem());
		
		TextView prodCount = (TextView) arg1.findViewById(R.id.productCount);
		prodCount.setText(entry.getQuantity()+" pcs");
		
		
		return arg1;
	}
	}