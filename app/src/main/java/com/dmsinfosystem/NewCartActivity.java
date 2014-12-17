package com.dmsinfosystem;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 17/12/14.
 */
public class NewCartActivity extends Activity {

    String TAG = "dmsinfosystem";
    public static TextView info;
    public static SQLiteDatabase database;
    public static LinearLayout ll;
    public static Cursor cursor;
    public static View itemView;
    public static LayoutInflater itemInflator;
    public static ImageButton B;
    public TextView success;
    public String Status;
    private Cartsql helper;
    public static ListView lv;
    public static String TABLE_NAME = "Users";
    public static String DB_PATH;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newcartlayout);
        itemInflator = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //database = openOrCreateDatabase("test_users.db", Context.MODE_PRIVATE, null);

        //helper = new Cartsql(getApplicationContext());

        //helper.onCreate(database);
        //ll = (LinearLayout) findViewById(R.id.itemLinearList);
        lv = (ListView) findViewById(R.id.itemListView);

        File dbFile = this.getDatabasePath("test_users.db");
        DB_PATH = dbFile.getAbsolutePath();
        if (dbFile.exists()) {
            Log.i(TAG, DB_PATH);
            try {
                database = SQLiteDatabase.openDatabase(DB_PATH, null, Context.MODE_PRIVATE);
                cursor = database.query("Users", new String[]{"_id", "name"}, null, null, null, null, null);
                lv.setAdapter(new SimpleCursorAdapter(getApplicationContext(),R.layout.cartitem,cursor,new String[]{"name"},new int[]{R.id.itemInfo},Context.MODE_PRIVATE) );
            }catch (SQLiteCantOpenDatabaseException e) {
                Log.i(TAG, "Database not Found!");
            }
        }
        registerForContextMenu(lv);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

       /*     ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            View obj = (View) lv.getItemAtPosition(acmi.position);

            menu.add("Remove Item");
            Log.i(TAG, "In on create context menu");*/
        if (v.getId()==R.id.itemListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Item Menu");
            menu.add("Remove Item");
        }
            //super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case 0:
                //SQLiteDatabase db = tasks.getWritableDatabase();
                SQLiteDatabase del_database = SQLiteDatabase.openDatabase(DB_PATH, null, Context.MODE_PRIVATE);
                ListView lv = (ListView) findViewById(R.id.itemListView);
                SimpleCursorAdapter adapter = (SimpleCursorAdapter) lv.getAdapter();
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                View adapterView = info.targetView;
                TextView adapterTextView = (TextView) adapterView.findViewById(R.id.itemInfo);

                Cursor newCursor = database.query("Users", new String[]{"_id", "name"}, null, null, null, null, null);
                del_database.delete(TABLE_NAME, "name=?", new String[]{adapterTextView.getText().toString()});
                ((SimpleCursorAdapter) lv.getAdapter()).swapCursor(newCursor);
                ((SimpleCursorAdapter) lv.getAdapter()).notifyDataSetChanged();// Not working, clears screen and doesn't reload
                Log.i(TAG, "In context item selected.Item with name: "+ adapterTextView.getText() );
                return true;
        }
         Log.i(TAG, "In context item selected outside switch");
         return super.onContextItemSelected(item);

    }
}
