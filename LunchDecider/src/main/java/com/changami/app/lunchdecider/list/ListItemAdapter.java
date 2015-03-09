package com.changami.app.lunchdecider.list;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.changami.app.lunchdecider.R;

import java.util.List;

/**
 * Created by chan_gami on 2014/05/18.
 */
public class ListItemAdapter extends ArrayAdapter<ListItem> {

    LayoutInflater mInflater;

    public ListItemAdapter(Context context, Activity activity, List<ListItem> items) {
        super(context, 0, items);
        mInflater = activity.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItem item = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_row, null);
            holder = new ViewHolder();
            holder.pointNameTextView =
                    (TextView) convertView.findViewById(R.id.item_point_name);
            holder.lastTimeTextView =
                    (TextView) convertView.findViewById(R.id.item_last_selected);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.pointNameTextView.setText(item.pointName);
        holder.lastTimeTextView.setText(item.lastTime);
        return convertView;
    }

}
