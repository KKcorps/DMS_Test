package com.dmsinfosystem;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CartActivity extends Activity {

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
    public static List<String> cartItemsList = new ArrayList<String>();
    public static ListAdapter cartListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cartlayout);
        itemInflator = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        database = openOrCreateDatabase("test_users.db", Context.MODE_PRIVATE, null);

        helper = new Cartsql(getApplicationContext());

        helper.onCreate(database);
        ll = (LinearLayout) findViewById(R.id.itemLinearList);

        File dbFile = this.getDatabasePath("test_users.db");
        final String DB_PATH = dbFile.getAbsolutePath();
        if (dbFile.exists()) {
            Log.i(TAG, DB_PATH);
            try {
                database = SQLiteDatabase.openDatabase(DB_PATH, null, Context.MODE_PRIVATE);
                cursor = database.query("Users", new String[]{"_id", "name"}, null, null, null, null, null);
                cursor.moveToFirst();

                boolean ffff = cursor.moveToFirst();
                Log.i("qqq", String.valueOf(ffff));
                Cursor cur = database.rawQuery("SELECT COUNT(*) Users", null);
                cur.moveToFirst();
                cur.moveToNext();
                int save = cur.getCount();
                if (cur.moveToFirst() && cursor.moveToFirst()) {
                    do {

                        final int nameno = cursor.getColumnIndex("name");
                        itemView = itemInflator.inflate(R.layout.cartitem, null);

                        info = (TextView) itemView.findViewById(R.id.itemInfo);
                        //B = (ImageButton) itemView.findViewById(R.id.itemClose);
                        final String where = "name = 'cursor.getString(nameno)'";
                        final String del = cursor.getString(nameno);
                        B.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ///database.delete("Users","name"+"="+del, null);
                                // "name"+"="+del1
                                database.execSQL("delete from " + "Users" + " where name='" + del + "'");
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        });

                        String infoString = cursor.getString(nameno);
                        info.setText(infoString);

                        cartItemsList.add(infoString);

                        ll.addView(itemView);
                        Log.i(TAG, infoString);
                    }
                    while (cursor.moveToNext() && cursor != null);
                }
            } catch (SQLiteCantOpenDatabaseException e) {
                Log.i(TAG, "Database not Found!");
            }

            //cartListAdapter = new CBAdapter(getApplicationContext(),cartItemsList);

            final Button confirmOrder = (Button) findViewById(R.id.confirmButton);

            if (!cursor.moveToFirst()) {

                confirmOrder.setText("Close Cart");
                confirmOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            } else {
                //top.addView(a,params);
                confirmOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent Infor = new Intent(CartActivity.this, SmsActivity.class);
                        startActivity(Infor);
                        Log.i("Status", "" + Status);
                        //Uri.parse("http://59.162.167.52/api/MessageCompose?admin=contact@dmsinfosystem.com&user=amit@dmsinfosystem.com:W124X4&senderID=TEST SMS&receipientno=9717304620&msgtxt=hello&state=4");
                        Log.i("Done", "Done");
                    }
                });
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

