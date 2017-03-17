package com.example.neha.trackle.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.neha.trackle.database.HistoryDBSchema.HistoryTable;

import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by neha on 4/3/2016.
 */
public class HistoryDBHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "ParkingHistory.db";

    public HistoryDBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + HistoryTable.NAME + "(" +
                        HistoryTable.Cols.ID + ", " +
                        HistoryTable.Cols.DATE + ", " +
                        HistoryTable.Cols.LATITUDE + ", " +
                        HistoryTable.Cols.LONGITUDE + ", " +
                        HistoryTable.Cols.ADDRESS + ", " +
                        HistoryTable.Cols.DURATION + ", " +
                        HistoryTable.Cols.COST + ", " +
                        HistoryTable.Cols.NOTE + ", " +
                        HistoryTable.Cols.PAIDPARKING + ", " +
                        HistoryTable.Cols.HISTORYENABLED + ", " +
                        HistoryTable.Cols.IMAGECOUNT +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}