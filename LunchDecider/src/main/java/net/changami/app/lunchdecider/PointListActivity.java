package net.changami.app.lunchdecider;

import android.app.ListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import net.changami.app.lunchdecider.data.LunchPointDao;
import net.changami.app.lunchdecider.data.LunchPointEntity;
import net.changami.app.lunchdecider.list.ListItem;
import net.changami.app.lunchdecider.list.ListItemAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PointListActivity extends ListActivity {

    static SQLiteDatabase mydb;
    static LunchPointDao dao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_point_list);

        MySQLiteOpenHelper mHelper = new MySQLiteOpenHelper(getApplicationContext());
        mydb = mHelper.getWritableDatabase();
        dao = new LunchPointDao(mydb);

        List<LunchPointEntity> entities = dao.findAll();
        List<ListItem> items = new ArrayList<ListItem>();
        for (LunchPointEntity entity : entities) {
            items.add(new ListItem(
                    entity.getPointName(),
                    (new SimpleDateFormat("yyyy-MM-dd", Locale.JAPANESE)).format(entity.getLastTime())
            ));
        }
        ListAdapter adapter = new ListItemAdapter(this, this, items);
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListItem item = (ListItem) parent.getItemAtPosition(position);
                //アイテムをロングタップされた場合にリストダイアログを実装しよう（編集・削除）

            }
        });
    }

}