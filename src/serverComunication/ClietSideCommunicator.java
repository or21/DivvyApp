package serverComunication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.main.divvyapp.R;
import com.main.divvyapp.StorePage;

public class ClietSideCommunicator {
	
	
	
	public ClietSideCommunicator(){
		
	}
	
	public void connectToServer(ServerAsyncParent activity){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", Integer.toString(-1)));
		new DataTransfer(activity, params, DataTransfer.METHOD_GET).execute("http://nir.milab.idc.ac.il/php/milab_get_deals.php");
	}
	
	public List<String> setDealsFromServer(JSONArray deals){
		try {

			// gets all store names to represent each name once on the list
			// not effective solution
			List<String> storeidArr = new ArrayList<String>();
			for (int i = 0; i < deals.length(); i++) {
				JSONObject row = deals.getJSONObject(i);
				String currentStoreid = row.getString("storeid");
				if (!(storeidArr.contains(currentStoreid))) {
					storeidArr.add(row.getString("storeid"));
				}
			}

//			// looping through All deals prepare the list of all stores
//			for(int i = 0; i <  deals.length(); i++){
//				JSONObject row = deals.getJSONObject(i);
//				String currentStoreid = row.getString("storeid");
//
//				if(storeidArr.contains(currentStoreid)){
//					HashMap<String, String> map = new HashMap<String, String>();
//					map.put("id", row.getString("id"));
//					map.put("claimedBy", row.getString("claimedBy"));
//					map.put("picture", row.getString("picture"));
//					map.put("storeid", row.getString("storeid"));
//					map.put("deadLine", row.getString("deadLine"));
//					storeList.add(map);
//					storeidArr.remove(storeidArr.indexOf(currentStoreid));
//				}
//				
//			}
			
			return storeidArr;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
