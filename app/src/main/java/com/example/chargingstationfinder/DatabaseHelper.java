package com.example.chargingstationfinder;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyDatabaseFile";
    public static final int VERSION_NUM = 2;
    public static final String TABLE_NAME = "locations";
    public static final String COL_long = "longitude";
    public static final String COL_name = "locationName";
    public static final String COL_lat = "lattitude";
    public static final String COL_phone = "phone";



    public DatabaseHelper(Activity ctx){
        //The factory parameter should be null, unless you know a lot about Database Memory management
        super(ctx, DATABASE_NAME, null, VERSION_NUM );
    }

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db)
    {
        //Make sure you put spaces between SQL statements and Java strings:
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_long +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_name + " TEXT, " + COL_lat + " TEXT, "+ COL_phone + " INTEGER)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database upgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database downgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }

    // insert message into database
    public long insert(output message) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_name, message.getLocationName());
        contentValues.put(COL_long, message.getLongitude());
        contentValues.put(COL_lat, message.getLattitude());
        contentValues.put(COL_phone, message.getPhoneN());

        return db.insert(TABLE_NAME, null, contentValues);
    }

    public ArrayList<output> getAllMessage() {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<output> messages = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        Log.i("Version Number", db.getVersion() + "");
        Log.i("Column Count", cursor.getColumnCount() + "");
        Log.i("Row Count", cursor.getCount() + "");
        for (String name : cursor.getColumnNames()) {
            Log.i("Column Name", name);
        }

        while (cursor.moveToNext()) {
            int  longitude = cursor.getInt(0);
            String name = cursor.getString(1);
            int lattitude = cursor.getInt(2);
            Long Phone = cursor.getLong(3);
            output message = new output(name,longitude,lattitude,Phone);

            Log.i("Message", message.toString());
            messages.add(message);
        }

        return messages;
    }
}
