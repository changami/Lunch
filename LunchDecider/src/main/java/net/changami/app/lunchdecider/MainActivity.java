package net.changami.app.lunchdecider;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import net.changami.app.lunchdecider.data.LunchPointDao;
import net.changami.app.lunchdecider.data.LunchPointEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity {

    static SQLiteDatabase mydb;
    static LunchPointDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MySQLiteOpenHelper mHelper = new MySQLiteOpenHelper(getApplicationContext());
        mydb = mHelper.getWritableDatabase();
        dao = new LunchPointDao(mydb);

        Button decideButton = (Button) findViewById(R.id.decide_button);
        decideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final List<String> records = new ArrayList<String>();

                List<LunchPointEntity> entities = dao.findAll();
                for (LunchPointEntity entity : entities) {
                    records.add(entity.getPointName());
                }

                if (records.size() != 0) {
                    Toast.makeText(MainActivity.this, records.get(new Random().nextInt(records.size())) + "に行きましょう", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "おひるごはんたべるところがありません", Toast.LENGTH_SHORT).show();
                }
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
