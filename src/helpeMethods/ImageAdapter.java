package helpeMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.main.divvyapp.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	
	//Store Map
//	  private static final Map<String, Integer> constantStores;
//	    static
//	    {
//	    	constantStores = new HashMap<String, Integer>();
//	    	constantStores.put("new_store", R.drawable.newlogo);
//	    	constantStores.put("castro", R.drawable.castro);
//	    	constantStores.put("nike", R.drawable.nike);
//	    	constantStores.put("hm", R.drawable.hm);
//	    	constantStores.put("laline", R.drawable.laline);
//	    	constantStores.put("americaneagle", R.drawable.americaneagle);
//	    	constantStores.put("rayban", R.drawable.rayban);
//	    	constantStores.put("apple", R.drawable.apple);
//	    	constantStores.put("crazyline", R.drawable.crazyline);
//	    	constantStores.put("lacoste", R.drawable.lacoste);
//	    	
//	    	///////////////deals//////////////////////
//	    	constantStores.put("11", R.drawable.oneplusone);
//	    	constantStores.put("12", R.drawable.oneplustwo);
//
//
//
//	    }
	
	 // The activity
	 private Context activity;
	 
	 //stores from database
	 private List<String> recievedStoreList;
	 
	  // references to our images
	  private List<Integer> imageList;


	    
	 public ImageAdapter(Context c, List<String> recievedStoreList) {
		 this.recievedStoreList = recievedStoreList;
		 
		 activity = c;
	     
	     String currentStore = "";
	     imageList = new ArrayList<Integer>();
	     
//	     for (int i = 0; i < recievedStoreList.size(); i++) {
//	    	 currentStore = recievedStoreList.get(i).toLowerCase();
//			if (!(imageList.contains(currentStore))) {
//				if (constantStores.containsKey(currentStore)) {
//					imageList.add(constantStores.get(currentStore));
//
//				}else if(!(constantStores.containsKey(currentStore))){
//					imageList.add(constantStores.get("11"));
//				}
//			}
//			
//		}
	     
//			imageList.add(R.drawable.addstore);

	    }
	 
	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(activity);
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
