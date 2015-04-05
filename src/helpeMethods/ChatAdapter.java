package helpeMethods;

import java.util.ArrayList;

import com.main.divvyapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChatAdapter extends ArrayAdapter<ChatItem> {
	
    private static class ViewHolder {
        private ListView itemView;
        TextView otherUser;
        TextView dealDescription;

		@SuppressWarnings("unused")
		public ListView getItemView() {
			return itemView;
		}
    }

    public ChatAdapter(Context context, int textViewResourceId, ArrayList<ChatItem> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder viewHolder;
    	ChatItem item = getItem(position);
    	
    	if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
            .inflate(R.layout.chats_list, parent, false);

            viewHolder = new ViewHolder();
            
            viewHolder.otherUser = (TextView) convertView.findViewById(R.id.other_user);
            viewHolder.dealDescription = (TextView) convertView.findViewById(R.id.dealdescription);
            
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.otherUser.setText(item.getOtherUser());
        viewHolder.dealDescription.setText(item.getDealdescription());

    	
        return convertView;
    }
}