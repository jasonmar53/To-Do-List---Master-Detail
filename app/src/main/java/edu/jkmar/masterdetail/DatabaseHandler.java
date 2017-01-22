package edu.jkmar.masterdetail;

/**
 * Created by Jason on 11/12/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "todolist";

    // Contacts table name
    private static final String TABLE_ITEMS = "items";

    // Contacts Table Columns names
    private static final String KEY_POS = "pos";
    private static final String KEY_NAME = "name";
    private static final String KEY_CHECKED = "checked";
    private static final String KEY_CODE = "code";
    private static final String KEY_DETAIL = "detail";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_POS + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_CHECKED + " TEXT," +  KEY_CODE + " Text," + KEY_DETAIL + " TEXT" + ")";
        Log.i("Tag", CREATE_ITEMS_TABLE);
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);

        // Create tables again
        onCreate(db);
    }

    public void addListItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, null, null);
        ArrayList<ListItem> mList = ToDoListManager.getmList();
        for (int i = 0; i < mList.size(); i++) {
            ContentValues values = new ContentValues();
            // values.put(KEY_POS, i);
            values.put(KEY_NAME, mList.get(i).getName());
            values.put(KEY_CHECKED, mList.get(i).getChecked());
            values.put(KEY_CODE, mList.get(i).getCode());
            values.put(KEY_DETAIL, mList.get(i).getDetail());
            db.insert(TABLE_ITEMS, null, values);
        }
    }

    public ArrayList<ListItem> getList() {
        ArrayList<ListItem> itemslist = ToDoListManager.getnewList();
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                ListItem item = new ListItem(null, false, null, null);
                item.setName(cursor.getString(1));
                item.setChecked(cursor.getString(2));
                item.setCode(cursor.getString(3));

                item.setDetail(cursor.getString(4));   //
                Log.i("tag", "bissadkosfs");
                itemslist.add(item);
            } while (cursor.moveToNext());
        }
        return itemslist;
    }

}
