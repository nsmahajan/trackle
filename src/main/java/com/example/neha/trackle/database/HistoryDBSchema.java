package com.example.neha.trackle.database;

/**
 * Created by neha on 4/3/2016.
 */
public class HistoryDBSchema {
    public static final class HistoryTable {
        public static final String NAME = "history";

        public static final class Cols {
            public static final String ID = "id";
            public static final String DATE = "date";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String ADDRESS = "address";
            public static final String DURATION = "duration";
            public static final String COST = "cost";
            public static final String NOTE = "note";
            public static final String PAIDPARKING = "paidparking";
            public static final String IMAGECOUNT = "imagecount";
            public static final String HISTORYENABLED = "historyenabled";
        }
    }
}
