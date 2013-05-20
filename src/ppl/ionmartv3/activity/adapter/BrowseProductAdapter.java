package ppl.ionmartv3.activity.adapter;

import java.util.List;

import ppl.ionmartv3.R;
import ppl.ionmartv3.R.id;
import ppl.ionmartv3.R.layout;
import ppl.ionmartv3.activity.session.Product;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BrowseProductAdapter extends BaseAdapter
{
	private Context mContext;
	private List<Product> mListProduct;
	
	
	public BrowseProductAdapter(Context c, List<Product> list){
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
		Product entry = mListProduct.get(arg0);
		if(arg1 == null){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			arg1 = inflater.inflate(R.layout.layout_productinfo,null);
		}
		ImageView ivIcon = (ImageView) arg1.findViewById(R.id.ivIcon);
		ivIcon.setImageBitmap(entry.getImage());
		
		TextView tvName = (TextView) arg1.findViewById(R.id.tvName);
		tvName.setText(entry.getName());
		return arg1;
	}
	}