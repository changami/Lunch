package net.changami.app.lunchdecider;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by chan_gami on 2014/04/17.
 */
public class AddActivity extends Activity {

    static SQLiteDatabase mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        MySQLiteOpenHelper mHelper = new MySQLiteOpenHelper(getApplicationContext());
        mydb = mHelper.getWritableDatabase();

        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.point_name);
                assert editText.getText() != null;
                ContentValues values = new ContentValues();
                values.put("name", editText.getText().toString());
                mydb.insert("lunch_point", null, values);
                Toast.makeText(AddActivity.this, "「" + editText.getText().toString() + "」をDBに追加しましたよ", Toast.LENGTH_LONG).show();
            }
        });
    }
}
