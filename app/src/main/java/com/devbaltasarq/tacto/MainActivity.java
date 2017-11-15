package com.devbaltasarq.tacto;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView lvContacts = this.findViewById( R.id.lvContacts );
        final ImageButton btAdd = this.findViewById( R.id.btAdd );

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.addContact();
            }
        });

        this.dbManager = new SqlIO( this.getApplicationContext() );
        this.registerForContextMenu( lvContacts );
        this.showContacts();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        this.dbManager.close();
    }

    private void showContacts()
    {
        final ListView lvContacts = this.findViewById( R.id.lvContacts );
        SQLiteDatabase db = this.dbManager.getReadableDatabase();

        Cursor allContacts = db.rawQuery( "SELECT * FROM contacts", null );//?", new String[]{ SqlIO.DB_TABLE_CONTACTS } );

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter( this,
                R.layout.lvcontacts,
                allContacts,
                new String[]{ "_id", "tlf" },
                new int[] { R.id.lvContacts_Name, R.id.lvContacts_Tlf } );

        lvContacts.setAdapter( cursorAdapter );
    }

    private void addContact()
    {
        final EditText edName = this.findViewById( R.id.edName );
        final String name = edName.getText().toString();

        if ( !name.isEmpty() ) {
            final EditText edTlf = new EditText( this );
            AlertDialog.Builder dlg = new AlertDialog.Builder( this );
            dlg.setTitle( "Tlf?" );
            dlg.setView( edTlf );
            dlg.setNegativeButton( "Cancel", null );
            dlg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.this.dbAdd( name, edTlf.getText().toString() );
                    MainActivity.this.showContacts();
                }
            });
            dlg.create().show();
        } else {
            Toast.makeText( this, "Name??", Toast.LENGTH_LONG ).show();
        }

        return;
    }

    private void dbAdd(String name, String tlf)
    {
        SQLiteDatabase db = this.dbManager.getWritableDatabase();
        Cursor cursor = null;

        try {
            db.beginTransaction();
            cursor = db.query( SqlIO.DB_TABLE_CONTACTS,
                    new String[]{ "_id" },
                    "_id=?", new String[]{ name },
                    null,
                    null,
                    null,
                    "1" );

            if ( cursor.getCount() > 0 ) {
                ContentValues values = new ContentValues();
                values.put( "_id", name );
                values.put( "tlf", tlf );

                db.update( SqlIO.DB_TABLE_CONTACTS, values, "_id = ?", new String[]{ name }  );
            } else {
                ContentValues values = new ContentValues();
                values.put( "_id", name );
                values.put( "tlf", tlf );

                db.insert( SqlIO.DB_TABLE_CONTACTS, null, values );
            }

            db.setTransactionSuccessful();
        } catch(SQLException exc) {
            Log.e( "dbAdd", exc.getMessage() );
            Toast.makeText( this, "Database ERROR: could not add: " + name, Toast.LENGTH_LONG ).show();
        }
        finally {
            if ( cursor != null ) {
                cursor.close();
            }

            db.endTransaction();
        }
    }

    private void dbRemove(String name)
    {
        SQLiteDatabase db = this.dbManager.getWritableDatabase();

        try {
            db.beginTransaction();
            db.delete( SqlIO.DB_TABLE_CONTACTS, "_id = ?", new String[]{ name } );
            db.setTransactionSuccessful();
        } catch(SQLException exc) {
            Log.e( "dbRemove", exc.getMessage() );
            Toast.makeText( this, "Database ERROR: could not remove: " + name, Toast.LENGTH_LONG ).show();
        }
        finally {
            db.endTransaction();
        }
    }

    private SqlIO dbManager;
}
