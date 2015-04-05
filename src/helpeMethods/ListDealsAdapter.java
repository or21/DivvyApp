package helpeMethods;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.main.divvyapp.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ListDealsAdapter extends BaseAdapter {
	
	//Store Map
	  private static final Map<String, Integer> constantStores;
	    static
	    {
	    	constantStores = new HashMap<String, Integer>();
	    	constantStores.put("newLogo", R.drawable.newlogo);
	    	constantStores.put("castro", R.drawable.castro);
//	    	constantStores.put("nike", R.drawable.nike);
//	    	constantStores.put("hm", R.drawable.hm);
//	    	constantStores.put("laline", R.drawable.laline);
//	    	constantStores.put("americaneagle", R.drawable.americaneagle);
//	    	constantStores.put("rayban", R.drawable.rayban);
//	    	constantStores.put("apple", R.drawable.apple);
//	    	constantStores.put("crazyline", R.drawable.crazyline);
//	    	constantStores.put("lacoste", R.drawable.lacoste);
	    }
	
	
	DealObj deal;
	ViewHolder holder;
	Context context;
	protected List<DealObj> listDeals;
	LayoutInflater inflater;
	
	// Constructor
	 public ListDealsAdapter(Context context, List<DealObj> listDeals) {
		 this.listDeals = listDeals;
		 this.inflater = LayoutInflater.from(context);
		 this.context = context;
		 }
	
	@Override
	public int getCount() {
		return listDeals.size();
	}

	@Override
	public Object getItem(int position) {
		return listDeals.get(position);
	}

	@Override
	public long getItemId(int position) {
//		return listDeals.get(position).getPicture();
		return 100;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		 if (convertView == null) {
		 
		 holder = new ViewHolder();
		 convertView = this.inflater.inflate(R.layout.layout_list_item,
		 parent, false);
		 
		 holder.dealName = (TextView) convertView
		 .findViewById(R.id.txt_id);
		 holder.storId = (TextView) convertView
		 .findViewById(R.id.txt_storeid);
		 holder.category = (TextView) convertView
		 .findViewById(R.id.txt_category);
		 
		 holder.claimedBy = (TextView) convertView
		 .findViewById(R.id.txt_claimedBy);
		 holder.picture = (ImageView) convertView.findViewById(R.id.img);
		 
		 convertView.setTag(holder);
		 } else {
		 holder = (ViewHolder) convertView.getTag();
		 }
		 
		 deal = listDeals.get(position);
		 holder.dealName.setText(deal.getDealName());
		 holder.storId.setText(deal.getStoreId());
		 holder.category.setText(deal.getCategory());
		 
		 if (deal.getClaimedBy().length() > 15) {
			 holder.claimedBy.setText("pending");
		}
		 
		 // Image setup
		 if (constantStores.containsKey(deal.getStoreId())) {
			 holder.picture.setImageResource(constantStores.get(deal.getStoreId()));
		}else{
			holder.picture.setImageResource(constantStores.get("newLogo"));
		}

		 return convertView;
		 }
	
	
	 private class ViewHolder {
//		 TextView id;
		 TextView dealName;
		 TextView storId;
		 TextView category;
		 TextView claimedBy;
		 ImageView picture;
		 }

}
