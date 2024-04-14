package com.tracker.expensetracker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SpendData.db";
    private static final String TABLE_NAME = "spend";   // Table Name
    private static final String Time_Table = "CREATE TABLE spends(Title VARCHAR(255),Amount DECIMAL(10,2),Date DATETIME);";
    private static final String PAST_WEEK = "SELECT * FROM spends WHERE Date >= now() - interval 1 week;";
    private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
//    SimpleDateFormat date = new SimpleDateFormat("dd-MM-YY hh:mm a");


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(Time_Table);
        } catch (Exception e) {
            Log.d("Error",e.toString());
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }catch (Exception e) {
            Log.d("Error",e.toString());
        }
    }

//    public void hel(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor c = db.rawQuery(PAST_WEEK,null);
//    }


}