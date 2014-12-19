package com.dmsinfosystem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Cartsql extends SQLiteOpenHelper {

    public static String TABLE_USERS = "Users";
    public static String COLUMN_ID = "_id";
    public static String COLUMN_NAME = "name";
    public static String COLUMN_PRICE = "price";
    private static String DATABASE_NAME = "test_users.db";
    public static int DATABASE_VERSION = 1;
    public static String NAME;
    public static String PRICE;
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_USERS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_NAME
            + " text not null, "+ COLUMN_PRICE
            + " text not null);" ;


    public Cartsql(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+ TABLE_USERS +"'", null);

        if(cursor==null || cursor.getCount()<=0) {
            database.execSQL(DATABASE_CREATE);
            Log.i("SQL","Table created");
        }
        Log.i("SQL","Table not created");
    }



    public void onInsert(SQLiteDatabase database,String name, int Price){

        NAME = name;
        PRICE = String.valueOf(Price);

        final String DATABASE_INSERT = "insert into "
                +TABLE_USERS + "(" + COLUMN_NAME +"," + COLUMN_PRICE + ") values ('"+NAME+"','"+PRICE+"');" ;

        Log.i("SQL", DATABASE_INSERT);
        database.execSQL(DATABASE_INSERT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Cartsql.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        Log.i("SQL","Table upgraded");
        Log.i("SQL", DATABASE_CREATE);
        onCreate(db);
    }

}