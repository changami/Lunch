package net.changami.app.lunchdecider;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursor = mydb.query("lunch_point", new String[]{"name"}, null, null, null, null, "_id DESC");
        String[] data = new String[cursor.getCount()];
        int i = 0;

        for (boolean next = cursor.moveToFirst(); next; next = cursor.moveToNext()) {
            data[i] = cursor.getString(0);
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_row, data);
        setListAdapter(adapter);
    }

}
