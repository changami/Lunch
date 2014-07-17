package net.changami.app.lunchdecider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    static final String DB = "sqlite_lunch.db";
    static final int DB_VERSION = 1;
    static final String CREATE_TABLE = "create table lunch_point ( _id integer primary key autoincrement, name text not null, last_time text);";
    static final String DROP_TABLE = "drop table lunch_point;";

    public MySQLiteOpenHelper(Context context) {
        super(context, DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    // DBのバージョンが異なるときに走る
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
