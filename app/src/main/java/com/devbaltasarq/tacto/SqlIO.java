package com.devbaltasarq.tacto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/** Eases opening the database */
public class SqlIO extends SQLiteOpenHelper {
    public static final String DB_NAME = "contactDB";
    public static final String DB_TABLE_CONTACTS = "contacts";
    public static final int DB_VERSION = 2;

    public SqlIO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i( "SqlIO", DB_NAME + " creating: " + DB_TABLE_CONTACTS );

        try {
            db.beginTransaction();
            db.execSQL( "CREATE TABLE IF NOT EXISTS "
                    + DB_TABLE_CONTACTS
                    + "(_id string(255) PRIMARY KEY NOT NULL,"
                    + "tlf string(20) NOT NULL)"
            );
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int v1, int v2) {
        Log.i( "SqlIO", DB_NAME + " " + v1 + " -> " + v2 );

        try {
            db.beginTransaction();
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CONTACTS);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        this.onCreate( db );
    }
}
