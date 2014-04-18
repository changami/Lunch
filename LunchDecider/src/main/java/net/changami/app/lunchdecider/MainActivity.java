package net.changami.app.lunchdecider;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity {

    static SQLiteDatabase mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MySQLiteOpenHelper mHelper = new MySQLiteOpenHelper(getApplicationContext());
        mydb = mHelper.getWritableDatabase();

        Button decideButton = (Button) findViewById(R.id.decide_button);
        decideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DBの中からランダムのとこに行きましょう
                Cursor cursor = mydb.query("lunch_point", new String[]{"name"}, null, null, null, null, "_id DESC");
                cursor.moveToFirst();
                final List<String> records = new ArrayList<String>();
                // cursorの中身ぜんぶrecordsにつめこむ
                for (boolean next = cursor.moveToFirst(); next; next = cursor.moveToNext()) {
                    records.add(cursor.getString(0));
                }
                Toast.makeText(MainActivity.this, records.get(new Random().nextInt(records.size())) + "に行きましょう", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivity(new Intent(this, AddActivity.class));
            return true;
        }
        if (id == R.id.action_list) {
            startActivity(new Intent(this, PointListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}
