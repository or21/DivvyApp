package serverComunication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.main.divvyapp.R;
import com.main.divvyapp.StorePage;

public class ClietSideCommunicator {
	
	/*
	 * Connects to 'deals' table in database
	 */
	public void connectDealsTable(ServerAsyncParent activity){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", Integer.toString(-1)));
		new DataTransfer(activity, params, DataTransfer.METHOD_GET).execute("http://nir.milab.idc.ac.il/php/milab_get_deals.php");
	}
	
	
	/*
	 * Depends on JSON table, returns list of all values under this field
	 * connect method required
	 */
	public List<String> getFromTable(JSONArray table,String field){
		try {
			
			// puts all param values from table to array storeidArr
			List<String> paramArr = new ArrayList<String>();
			for (int i = 0; i < table.length(); i++) {
				JSONObject row = table.getJSONObject(i);
				
				// Exception
				if (!(row.has(field))) {
					Log.d("ClientSideCommunicator", "no such param in table");
				}
				
				String currentParam = row.getString(field);
				
				/*
				 * we have to make sure that null field will be writen as '0' in database
				 */
				if (currentParam == "0") {
					paramArr.add("null");
				}
				else if (!(paramArr.contains(currentParam))) {
					paramArr.add(currentParam);
				}
			}
			return paramArr;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * same as getFrom table just with filter - takes only params with filterField == filterParam
	 */
	public List<String> getFromTable(JSONArray table,String param, String filterField , String filterParam){
try {
			
			// puts all param values from table to array storeidArr
			List<String> paramArr = new ArrayList<String>();
			for (int i = 0; i < table.length(); i++) {
				JSONObject row = table.getJSONObject(i);
				
				// Exception
				if (!(row.has(param))) {
					Log.d("ClientSideCommunicator", "no such param in table");
				}
				String filter = row.getString(filterField);
				
				String currentParam = row.getString(param);
				/*
				 * we have to make sure that null field will be writen as '0' in database
				 */
				if (currentParam.equalsIgnoreCase("0") && (filter.equalsIgnoreCase(filterParam))) {
					paramArr.add("null");
				}else if (!(paramArr.contains(currentParam)) && (filter.equalsIgnoreCase(filterParam))) {
					paramArr.add(currentParam);
				}
			}
			return paramArr;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
