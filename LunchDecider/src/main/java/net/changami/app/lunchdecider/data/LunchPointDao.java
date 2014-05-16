package net.changami.app.lunchdecider.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by chan_gami on 2014/05/16.
 */
public class LunchPointDao {

    private static final String TABLE_NAME = "lunch_point";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_POINT_NAME = "name";
    private static final String COLUMN_LAST_TIME = "last_time";
    private static final String[] COLUMNS = {COLUMN_ID, COLUMN_POINT_NAME, COLUMN_LAST_TIME};

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPANESE);

    // SQLiteDatabase
    private SQLiteDatabase db;

    public LunchPointDao(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 全データの取得
     *
     * @return
     */
    public List<LunchPointEntity> findAll() {
        List<LunchPointEntity> entityList = new ArrayList<LunchPointEntity>();
        Cursor cursor = db.query(
                TABLE_NAME,
                COLUMNS,
                null,
                null,
                null,
                null,
                COLUMN_ID);

        while (cursor.moveToNext()) {
            LunchPointEntity entity = new LunchPointEntity();
            entity.setId(cursor.getInt(0));
            entity.setPointName(cursor.getString(1));
            //SQLiteはDate型ないもん
            try {
                entity.setLastTime(SDF.parse(cursor.getString(2)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            entityList.add(entity);
        }

        return entityList;
    }

    /**
     * データの登録
     *
     * @param pointName
     * @return
     */
    public long insert(String pointName) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_POINT_NAME, pointName);
        //SQLiteはDate型ないもん
        values.put(COLUMN_LAST_TIME, SDF.format(new Date()));
        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * データの更新
     *
     * @param entity
     * @return
     */
    public int update(LunchPointEntity entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_POINT_NAME, entity.getPointName());
        //SQLiteはDate型ないもん
        values.put(COLUMN_LAST_TIME, SDF.format(entity.getLastTime()));
        String where = COLUMN_ID + "=" + entity.getId();
        return db.update(TABLE_NAME, values, where, null);
    }

    /**
     * データの削除
     *
     * @param _id
     * @return
     */
    public int delete(int _id) {
        String where = COLUMN_ID + "=" + _id;
        return db.delete(TABLE_NAME, where, null);
    }

}
