package com.dmsinfosystem;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 17/12/14.
 */
public class NewCartActivity extends Activity {

    String TAG = "dmsinfosystem";
    public static TextView info;
    public static SQLiteDatabase database;
    public static LinearLayout ll,totalAmount;
    public static Cursor cursor;
    public static View itemView;
    public static LayoutInflater itemInflator;
    public static ImageButton B;
    public TextView totalPrice,totalPriceInfo,NetPrice,ServiceTax,EduCess,SHECess;
    public String Status;
    private Cartsql helper;

    private SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

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
        ll = (LinearLayout) findViewById(R.id.amountList);

        lv = (ListView) findViewById(R.id.itemListView);
        totalAmount = (LinearLayout) findViewById(R.id.totalAmount);

        totalPrice = (TextView) findViewById(R.id.itemTotalPrice);
        totalPriceInfo = (TextView) findViewById(R.id.itemTotal);
        NetPrice = (TextView) findViewById(R.id.netAmountPrice);
        ServiceTax = (TextView) findViewById(R.id.serviceTaxPrice);
        EduCess = (TextView) findViewById(R.id.educationCessPrice);
        SHECess = (TextView) findViewById(R.id.sheCessPrice);

        preferences = getApplicationContext().getSharedPreferences("MyPref",Context.MODE_PRIVATE);
        int netPrice;
        double serviceTax,eduCess,sheCess;
        netPrice = preferences.getInt("Total Price", 0);
        serviceTax = roundTwoDecimals(0.12 * netPrice);
        eduCess = roundTwoDecimals(0.02 * serviceTax);
        sheCess = roundTwoDecimals(0.01 * serviceTax);

        totalPrice.setText("Rs. "+ String.valueOf(netPrice) );
        ServiceTax.setText("Rs. " + String.valueOf(serviceTax));
        EduCess.setText("Rs. "+ String.valueOf(eduCess));
        SHECess.setText("Rs. " + String.valueOf(sheCess));
        NetPrice.setText("Rs. "+String.valueOf(netPrice + serviceTax + eduCess + sheCess) );

        File dbFile = this.getDatabasePath("test_users.db");
        DB_PATH = dbFile.getAbsolutePath();
        if (dbFile.exists()) {
            Log.i(TAG, DB_PATH);
            try {
                database = SQLiteDatabase.openDatabase(DB_PATH, null, Context.MODE_PRIVATE);
                cursor = database.query("Users", new String[]{"_id", "name","price"}, null, null, null, null, null);
                lv.setAdapter(new SimpleCursorAdapter(getApplicationContext(),R.layout.cartitem,cursor,new String[]{"name","price"},new int[]{R.id.itemInfo,R.id.itemPrice},Context.MODE_PRIVATE) );

                if(cursor.getCount()<=0){
                    editor = preferences.edit();
                    editor.putInt("Total Price", 0).apply();
                    editor.commit();
                    totalPrice.setText("");
                    totalPriceInfo.setText("No Items in Cart");
                    ll.setVisibility(View.INVISIBLE);

                }else{
                    totalPriceInfo.setText("Gross Amount");
                    ll.setVisibility(View.VISIBLE);
                }
                //Setting  Listener on the Confirm Button
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

                            Intent Infor = new Intent(NewCartActivity.this, SmsActivity.class);
                            startActivity(Infor);
                            Log.i("Status", "" + Status);
                            //Uri.parse("http://59.162.167.52/api/MessageCompose?admin=contact@dmsinfosystem.com&user=amit@dmsinfosystem.com:W124X4&senderID=TEST SMS&receipientno=9717304620&msgtxt=hello&state=4");
                            Log.i("Done", "Done");
                        }
                    });
                }

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
                TextView adapterPriceView = (TextView) adapterView.findViewById(R.id.itemPrice);

                preferences = getApplicationContext().getSharedPreferences("MyPref",Context.MODE_PRIVATE);
                int oldAmount = preferences.getInt("Total Price", 0);
                int newAmount = oldAmount - Integer.valueOf(adapterPriceView.getText().toString());
                double serviceTax = roundTwoDecimals(0.12*newAmount);
                double eduCess = roundTwoDecimals(0.02*serviceTax);
                double sheCess = roundTwoDecimals(0.01*serviceTax);

                editor = preferences.edit();



                del_database.delete(TABLE_NAME, "name=?", new String[]{adapterTextView.getText().toString()});
                Cursor newCursor = database.query("Users", new String[]{"_id", "name","price"}, null, null, null, null, null);
                ((SimpleCursorAdapter) lv.getAdapter()).swapCursor(newCursor);
                ((SimpleCursorAdapter) lv.getAdapter()).notifyDataSetChanged();
                ll = (LinearLayout) findViewById(R.id.amountList);
                Button confirmOrder = (Button) findViewById(R.id.confirmButton);
                if(newCursor.getCount()<=0) {
                    editor.putInt("Total Price", 0).apply();
                    editor.commit();
                    totalPrice = (TextView) findViewById(R.id.itemTotalPrice);
                    totalPrice.setText("");
                    totalPriceInfo = (TextView) findViewById(R.id.itemTotal);
                    totalPriceInfo.setText("No Items in Cart");
                    ll.setVisibility(View.INVISIBLE);
                    confirmOrder.setText("Close Cart");
                    confirmOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });

                }else{
                    editor.putInt("Total Price", newAmount).apply();
                    editor.commit();

                    totalPriceInfo = (TextView) findViewById(R.id.itemTotal);
                    totalPriceInfo.setText("Gross Amount");

                    totalPrice = (TextView) findViewById(R.id.itemTotalPrice);
                    totalPrice.setText("Rs. " + String.valueOf(newAmount));
                    ServiceTax.setText("Rs. " + String.valueOf(serviceTax));
                    EduCess.setText("Rs. "+String.valueOf(eduCess));
                    SHECess.setText("Rs. "+String.valueOf(sheCess));
                    NetPrice.setText("Rs. "+String.valueOf( roundTwoDecimals(newAmount + serviceTax + eduCess + sheCess) ) );
                    ll.setVisibility(View.VISIBLE);
                    confirmOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent Infor = new Intent(NewCartActivity.this, SmsActivity.class);
                            startActivity(Infor);
                            Log.i("Status", "" + Status);
                            //Uri.parse("http://59.162.167.52/api/MessageCompose?admin=contact@dmsinfosystem.com&user=amit@dmsinfosystem.com:W124X4&senderID=TEST SMS&receipientno=9717304620&msgtxt=hello&state=4");
                            Log.i("Done", "Done");
                        }
                    });
                }


                Log.i(TAG, "In context item selected.Item with name: "+ adapterTextView.getText() );
                return true;
        }
         Log.i(TAG, "In context item selected outside switch");
         return super.onContextItemSelected(item);

    }

    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.clear_all:
                SQLiteDatabase del_database = SQLiteDatabase.openDatabase(DB_PATH, null, Context.MODE_PRIVATE);
                ListView lv = (ListView) findViewById(R.id.itemListView);
                ll = (LinearLayout) findViewById(R.id.amountList);
                Button confirmOrder = (Button) findViewById(R.id.confirmButton);
                preferences = getApplicationContext().getSharedPreferences("MyPref",Context.MODE_PRIVATE);
                editor = preferences.edit();
                del_database.execSQL("delete from "+ TABLE_NAME);

                Cursor newCursor = database.query("Users", new String[]{"_id", "name","price"}, null, null, null, null, null);
                ((SimpleCursorAdapter) lv.getAdapter()).swapCursor(newCursor);
                ((SimpleCursorAdapter) lv.getAdapter()).notifyDataSetChanged();

                editor.putInt("Total Price", 0).apply();
                editor.commit();
                totalPrice = (TextView) findViewById(R.id.itemTotalPrice);
                totalPrice.setText("");
                totalPriceInfo = (TextView) findViewById(R.id.itemTotal);
                totalPriceInfo.setText("No Items in Cart");
                ll.setVisibility(View.INVISIBLE);

                confirmOrder.setText("Close Cart");
                confirmOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
                Log.i(TAG, "Cart Cleared");


        }
        return super.onOptionsItemSelected(item);
    }
}
