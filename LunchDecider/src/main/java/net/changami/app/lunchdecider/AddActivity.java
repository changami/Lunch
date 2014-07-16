package net.changami.app.lunchdecider;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import net.changami.app.lunchdecider.data.LunchPointDao;

public class AddActivity extends Activity {

    static SQLiteDatabase mydb;
    static LunchPointDao dao;

    @InjectView(R.id.point_name)
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.inject(this);

        editText.setText("");

        MySQLiteOpenHelper mHelper = new MySQLiteOpenHelper(getApplicationContext());
        mydb = mHelper.getWritableDatabase();
        dao = new LunchPointDao(mydb);

    }

    @OnClick(R.id.add_button)
    public void onClick() {
        dao.insert(editText.getText().toString());
        Toast.makeText(AddActivity.this, "「" + editText.getText().toString() + "」をDBに追加しました", Toast.LENGTH_LONG).show();
        editText.setText("");
    }
}
