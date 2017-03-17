package com.example.neha.trackle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.neha.trackle.database.HistoryCursorWrapper;
import com.example.neha.trackle.database.HistoryDBHelper;
import com.example.neha.trackle.database.HistoryDBSchema;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neha on 4/24/2016.
 */
public class QueryLab {

    private Context mContext;
    private SQLiteDatabase mDatabase;

    /**
     * The function initializes the database reference variable.
     *
     * @param context
     * @return -
     *
     */
    public QueryLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new HistoryDBHelper(mContext).getWritableDatabase();
    }


    /**
     * The function inserts the data into the database.
     *
     * @param record it contains the data entered by the user.
     * @return -
     *
     */
    public void insertRecord(HistoryRecord record){
        ContentValues values = getContentValues(record);
        //Log.e("insertRecord",""+values);
        mDatabase.insert(HistoryDBSchema.HistoryTable.NAME, null, values);
    }

    /**
     * The function returns the data in the database required format for insertion.
     *
     * @param record it contains the data entered by the user.
     * @return -
     *
     */
    private ContentValues getContentValues(HistoryRecord record) {
        ContentValues values = new ContentValues();
        values.put(HistoryDBSchema.HistoryTable.Cols.ID, record.getID().toString());
        values.put(HistoryDBSchema.HistoryTable.Cols.DATE, record.getDate());
        values.put(HistoryDBSchema.HistoryTable.Cols.LATITUDE, record.getLatitude());
        values.put(HistoryDBSchema.HistoryTable.Cols.LONGITUDE, record.getLongitude());
        values.put(HistoryDBSchema.HistoryTable.Cols.ADDRESS, record.getAddress());
        values.put(HistoryDBSchema.HistoryTable.Cols.DURATION, record.getDuration());
        values.put(HistoryDBSchema.HistoryTable.Cols.COST, record.getCost());
        values.put(HistoryDBSchema.HistoryTable.Cols.NOTE, record.getNote());
        values.put(HistoryDBSchema.HistoryTable.Cols.PAIDPARKING, record.getPaidParking());
        values.put(HistoryDBSchema.HistoryTable.Cols.IMAGECOUNT, record.getImageCount());
        values.put(HistoryDBSchema.HistoryTable.Cols.HISTORYENABLED, record.getHistoryEnabled());
        return values;
    }

    /**
     * The function updates the reocrd data in the database.
     *
     * @param record it contains the updated data entered by the user.
     * @return -
     *
     */
    public void updateRecord(HistoryRecord record){
        ContentValues values = getContentValues(record);
        mDatabase.update(HistoryDBSchema.HistoryTable.NAME, values,
                HistoryDBSchema.HistoryTable.Cols.ID + " = ?",
                new String[]{record.getID().toString()});
    }

    /**
     * The function fetches the record data associated with the UUID.
     *
     * @param uuid it contains UUID for a record.
     * @return record contains the fetched data for the record.
     *
     */
    public HistoryRecord getRecordWithUUID(String uuid){
        HistoryCursorWrapper cursor = getRecord(uuid);
        cursor.moveToFirst();
        HistoryRecord record = cursor.getHistoryRecord();
        Log.e("getRecordWithUUID", "querlab" + record);
        cursor.close();
        return record;
    }

    /**
     * The function queries the database to fetch the record for a particular UUID.
     *
     * @param uuid it contains UUID for a record.
     * @return cursor returns the data in the cursor of the database.
     *
     */
    public HistoryCursorWrapper getRecord(String uuid){
        Cursor cursor = mDatabase.rawQuery("select * from " + HistoryDBSchema.HistoryTable.NAME + " where " + HistoryDBSchema.HistoryTable.Cols.ID + " = '" + uuid + "'", null);
        return new HistoryCursorWrapper(cursor);
    }

    /**
     * The function returns a list of all records from the database.
     *
     * @param startDate it contains startdate for a week.
     * @param endDate it contains the enddate for the week.
     * @return historyRecords contains all the list of records from the database.
     *
     */
    public List<HistoryRecord> getHistoryRecords(String startDate, String endDate){
        List<HistoryRecord> historyRecords = new ArrayList<>();
        HistoryCursorWrapper cursor = getHistoryData(startDate,endDate);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            historyRecords.add(cursor.getHistoryRecord());
            cursor.moveToNext();
        }
        cursor.close();
        return historyRecords;
    }

    /**
     * The function queries the database to fetch all the history records.
     *
     * @param startDate it contains startdate for a week.
     * @param endDate it contains the enddate for the week.
     * @return cursor contains all the list of records from the database.
     *
     */
    public HistoryCursorWrapper getHistoryData(String startDate, String endDate){
        Cursor cursor = mDatabase.rawQuery("select * from " + HistoryDBSchema.HistoryTable.NAME + " where " + HistoryDBSchema.HistoryTable.Cols.DATE + " BETWEEN '" + startDate + "' AND '" + endDate + "'", null);
        return new HistoryCursorWrapper(cursor);
    }

    /**
     * The function is used to delete a particular record from the database.
     *
     * @param recordID it contains UUID for a record.
     * @return true the database delete action is successfull.
     *
     */
    public boolean deleteHistoryRecord(String recordID){
        mDatabase.execSQL("DELETE FROM " + HistoryDBSchema.HistoryTable.NAME + " WHERE " + HistoryDBSchema.HistoryTable.Cols.ID +" = '" + recordID + "'");
        return true;
    }

    /**
     * The function is used to create a new image file for a record or fetch the image file if it already exists.
     *
     * @param record it contains record data.
     * @return file returns reference to the image file for a record.
     *
     */
    public File getPhotoFile(HistoryRecord record, int imageCount) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, record.getPhotoFilename(imageCount));
    }
}