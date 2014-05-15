package net.changami.app.lunchdecider;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import net.changami.app.lunchdecider.data.LunchPointDao;
import net.changami.app.lunchdecider.data.LunchPointEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by chan_gami on 2014/04/19.
 */
public class PointListActivity extends ListActivity {

    static SQLiteDatabase mydb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_point_list);

        MySQLiteOpenHelper mHelper = new MySQLiteOpenHelper(getApplicationContext());
        mydb = mHelper.getWritableDatabase();

        LunchPointDao dao = new LunchPointDao(mydb);
        List<LunchPointEntity> entities = dao.findAll();
        List<ListItem> items = new ArrayList<ListItem>();
        for(LunchPointEntity entity : entities) {
            items.add(new ListItem(
                    entity.getPointName(),
                    (new SimpleDateFormat("yyyy-MM-dd", Locale.JAPANESE)).format(entity.getLastTime())
            ));
        }
        ListAdapter adapter = new ListItemAdapter(this, items);
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListItem item = (ListItem) parent.getItemAtPosition(position);
                Toast.makeText(PointListActivity.this, item.pointName, Toast.LENGTH_SHORT).show();

                //くりっくいべんとを取得した後にどうするの
            }
        });
    }

    class ListItem {
        String pointName;
        String lastTime;

        ListItem(String pointName, String lastTime) {
            this.pointName = pointName;
            this.lastTime = lastTime;
        }
    }

    static class ViewHolder {
        TextView pointNameTextView;
        TextView lastTimeTextView;
    }

    class ListItemAdapter extends ArrayAdapter<ListItem> {

        LayoutInflater mInflater;

        ListItemAdapter(Context context, List<ListItem> items) {
            super(context, 0, items);
            mInflater = getLayoutInflater();
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


}