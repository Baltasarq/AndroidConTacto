package com.devbaltasarq.tacto;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/** Eases opening the database */
public class SqlIO extends SQLiteOpenHelper {
    public static final String DB_NAME = "contactDB";
    public static final int DB_VERSION = 2;

    public static final String TABLE_CONTACTS = "contacts";
    public static final String CONTACTS_COL_NAME = "_id";
    public static final String CONTACTS_COL_TLF = "tlf";

    public SqlIO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i( "SqlIO", DB_NAME + " creating: " + TABLE_CONTACTS);

        try {
            db.beginTransaction();
            db.execSQL( "CREATE TABLE IF NOT EXISTS "
                    + TABLE_CONTACTS + "("
                    + CONTACTS_COL_NAME + " string(255) PRIMARY KEY NOT NULL,"
                    + CONTACTS_COL_TLF + " string(20) NOT NULL)"
            );
            db.setTransactionSuccessful();
        } catch(SQLException exc) {
            Log.e( "SqlIO.onCreate", exc.getMessage() );
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int v1, int v2) {
        Log.i( "SqlIO", DB_NAME + " " + v1 + " -> " + v2 );

        try {
            db.beginTransaction();
            db.execSQL( "DROP TABLE IF EXISTS " + TABLE_CONTACTS);
            db.setTransactionSuccessful();
        } catch(SQLException exc) {
            Log.e( "SqlIO.onUpgrade", exc.getMessage() );
        } finally {
            db.endTransaction();
        }

        this.onCreate( db );
    }
}
