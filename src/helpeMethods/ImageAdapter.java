package helpeMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.main.divvyapp.DealsPage;
import com.main.divvyapp.R;

import serverComunication.ClietSideCommunicator;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	
	//Store Map
	  private static final Map<String, Integer> constantStores;
	    static
	    {
	    	constantStores = new HashMap<String, Integer>();
	    	constantStores.put("new_store", R.drawable.newlogo);
	    	constantStores.put("castro", R.drawable.castro);
	    	constantStores.put("nike", R.drawable.nike);
	    	constantStores.put("hm", R.drawable.hm);

	    }
	
	 private Context mContext;
	 
	 //stores from database
	 private List<String> recievedStoreList;
	 
	  // references to our images
	  private List<Integer> imageList;


	    
	 public ImageAdapter(Context c, List<String> recievedStoreList) {
		 this.recievedStoreList = recievedStoreList;
		 
	     mContext = c;
	     
	     String currentStore = "";
	     imageList = new ArrayList<Integer>();
	     
	     for (int i = 0; i < recievedStoreList.size(); i++) {
	    	 currentStore = recievedStoreList.get(i).toLowerCase();
			if (!(imageList.contains(currentStore))) {
				if (constantStores.containsKey(currentStore)) {
					imageList.add(constantStores.get(currentStore));

				}else if(!(constantStores.containsKey("newlogo"))){
					imageList.add(constantStores.get("newlogo"));
				}
			}
		}
	     
	     
	    }
	 
	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(255, 255));
	            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
	            imageView.setPadding(8, 8, 8, 8);
	        } else {
	            imageView = (ImageView) convertView;
	        }
	        imageView.setImageResource(imageList.get(position));
	        return imageView;
	    }


	    
	    public int getCount() {
	        return imageList.size();
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }


}
