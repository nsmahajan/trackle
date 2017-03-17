package com.example.neha.trackle.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.neha.trackle.database.HistoryDBSchema.HistoryTable;
import com.example.neha.trackle.HistoryRecord;

import java.util.UUID;

/**
 * Created by neha on 4/4/2016.
 */
public class HistoryCursorWrapper extends CursorWrapper {
    public HistoryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public HistoryRecord getHistoryRecord() {
        String uuidString = getString(getColumnIndex(HistoryTable.Cols.ID));
        String date = getString(getColumnIndex(HistoryTable.Cols.DATE));
        double latitude = getDouble(getColumnIndex(HistoryTable.Cols.LATITUDE));
        double longitude = getDouble(getColumnIndex(HistoryTable.Cols.LONGITUDE));
        String address = getString(getColumnIndex(HistoryTable.Cols.ADDRESS));
        String duration = getString(getColumnIndex(HistoryTable.Cols.DURATION));
        String cost = getString(getColumnIndex(HistoryTable.Cols.COST));
        String note = getString(getColumnIndex(HistoryTable.Cols.NOTE));
        String paidparking = getString(getColumnIndex(HistoryTable.Cols.PAIDPARKING));
        int imageCount = getInt(getColumnIndex(HistoryTable.Cols.IMAGECOUNT));
        String historyEnabled = getString(getColumnIndex(HistoryTable.Cols.HISTORYENABLED));

        HistoryRecord historyRecord = new HistoryRecord();
        historyRecord.setId(UUID.fromString(uuidString));
        historyRecord.setDate(date);
        historyRecord.setLatitude(latitude);
        historyRecord.setLongitude(longitude);
        historyRecord.setAddress(address);
        historyRecord.setDuration(duration);
        historyRecord.setCost(cost);
        historyRecord.setNote(note);
        historyRecord.setPaidParking(paidparking);
        historyRecord.setImageCount(imageCount);
        historyRecord.setHistoryEnabled(historyEnabled);

        return historyRecord;
    }
}