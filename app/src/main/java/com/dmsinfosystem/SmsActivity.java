package com.dmsinfosystem;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Milind on 16-12-2014.
 */


public class SmsActivity extends Activity{
    public String Status;
    public Button done;
    public EditText cemail,cname,cmobno;
    private ProgressBar progressBar;
    private TextView waitInfo;
    String NAME,MAIL,NUMBER, NetAmount;
    public static SQLiteDatabase database;
    public Cartsql helper;
    public static Cursor cursor;
    String TAG="DMSINFOSYSTEM";
    String msgexe="",msgStart;
    String msgcus="Thanks for Ordering\n";
    String msgcusUrlEncoded="";

    String NUMBEREXE="7583837748";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.smsactivity);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        waitInfo = (TextView) findViewById(R.id.textWaitInfo);
        progressBar.setVisibility(View.INVISIBLE);
        waitInfo.setVisibility(View.INVISIBLE);

        done = (Button)findViewById(R.id.done);
        cemail =(EditText)findViewById(R.id.contactemail1);
        MAIL = cemail.getText().toString();
        cmobno =(EditText)findViewById(R.id.contactNumber1);
        NUMBER= cmobno.getText().toString();
        cname =(EditText)findViewById(R.id.contactName1);
        NAME = cname.getText().toString();
        NetAmount = getIntent().getStringExtra("Net Amount");
        //final String UNAME = NAME.replace(" ","%20");

        database = openOrCreateDatabase("test_users.db", Context.MODE_PRIVATE, null);

        helper = new Cartsql(getApplicationContext());

        helper.onCreate(database);

        File dbFile = this.getDatabasePath("test_users.db");
        final String DB_PATH = dbFile.getAbsolutePath();
        if(dbFile.exists()){
            Log.i(TAG,DB_PATH);
            try{
                database = SQLiteDatabase.openDatabase(DB_PATH, null, Context.MODE_PRIVATE);
                cursor = database.query("Users", new String[]{"_id","name","price"}, null, null, null, null, null);
                cursor.moveToFirst();

                boolean ffff= cursor.moveToFirst();
                Log.i("qqq",String.valueOf(ffff));
                Cursor cur = database.rawQuery("SELECT COUNT(*) Users", null);
                cur.moveToFirst();
                cur.moveToNext();
                int save = cur.getCount();
                //Log.i("QQq","cursor count:" + cur.getString(cur.getColumnIndex("name")));
                if(  cur.moveToFirst() && cursor.moveToFirst() ) {
                    do {

                        final int nameno = cursor.getColumnIndex("name");

                        msgcusUrlEncoded = msgcusUrlEncoded+cursor.getString(nameno)+"\n";

                        final String del =cursor.getString(nameno);



                        String infoString = cursor.getString(nameno);

                    }
                    while(cursor.moveToNext() && cursor!=null);
                    msgcusUrlEncoded = msgcusUrlEncoded + "amounting to "+NetAmount+" You will soon receive a call from our executive";
                }
            }catch (SQLiteCantOpenDatabaseException e){
                Log.i(TAG,"Database not Found!");
            }

            done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NAME=cname.getText().toString();
                NUMBER= cmobno.getText().toString();
                msgexe = msgexe +"%20"+ NAME+"%20"+NUMBER+"%20";
                MAIL = cemail.getText().toString();
                msgStart = "Hi "+NAME+",\n"+msgcus;
                msgcusUrlEncoded = msgStart+msgcusUrlEncoded;


                progressBar.setVisibility(View.VISIBLE);
                waitInfo.setVisibility(View.VISIBLE);
                Log.i("SMS Activity", "Progress Bar Visible");
                EmailSendingTask emailSendingTask = new EmailSendingTask();
                emailSendingTask.execute("Order Details", msgcusUrlEncoded, MAIL);

                try {
                    msgcusUrlEncoded = URLEncoder.encode(msgcusUrlEncoded,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MessageSendingTask messageSendingTask = new MessageSendingTask();
                messageSendingTask.execute(NUMBER, msgcusUrlEncoded);

                if(messageSendingTask.getStatus() == AsyncTask.Status.FINISHED) {
                    progressBar.setVisibility(View.INVISIBLE);
                    waitInfo.setVisibility(View.INVISIBLE);
                }

                /*Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpClient httpclient1 = new DefaultHttpClient();
                        HttpPost httppost1 = new HttpPost("http://59.162.167.52/api/MessageCompose?admin=contact@dmsinfosystem.com&user=amit@dmsinfosystem.com:W124X4&senderID=TEST%20SMS&receipientno=" + NUMBEREXE + "&msgtxt=" + msgexe + "&state=4");

                        try {
                            // Add your data

                            Log.i("trying", "trying");

                            // Execute HTTP Post Request
                            HttpResponse response1 = httpclient1.execute(httppost1);
                            String responseBody1 = EntityUtils.toString(response1.getEntity());

                            Log.i("response", responseBody1);

                            Log.i("done", "done");

                            Status = "" + responseBody1.charAt(7);

                            Log.i("Status", "" + Status);
                            if (Status.equals("0")) {

                                Intent indent = new Intent(SmsActivity.this,HomeScreen.class);

                                startActivity(indent);

                            }
                            if (Status.equals("1")) {

                                Intent indent = getIntent();
                                finish();
                                startActivity(indent);
                            }


                        } catch (ClientProtocolException e) {
                            // TODO Auto-generated catch block
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                        }

                    }
                });
                thread.start();

                Thread thread1 = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        HttpClient httpclient = new DefaultHttpClient();

                        HttpPost httppost = new HttpPost("http://59.162.167.52/api/MessageCompose?admin=contact@dmsinfosystem.com&user=amit@dmsinfosystem.com:W124X4&senderID=TEST%20SMS&receipientno="+NUMBER+"&msgtxt="+msgcus+"&state=4");
                        Log.i("CUSTOMER",""+NUMBER);
                        try {
                            // Add your data

                            Log.i("trying", "trying");

                            // Execute HTTP Post Request

                            HttpResponse response = httpclient.execute(httppost);
                            String responseBody = EntityUtils.toString(response.getEntity());
                            Log.i("response", responseBody);
                            finish();




                        } catch (ClientProtocolException e) {
                            // TODO Auto-generated catch block
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                        }

                    }
                });
                thread1.start();*/


        }});
            }
        }

    private class EmailSendingTask extends AsyncTask<String,Integer,Long> {

        protected Long doInBackground(String... parameters) {


            long pr = 0;
            int cnt = parameters.length;
            try {
                GMailSender sender = new GMailSender("marketing@dmsinfosystem.com", "Sm5H=dNX");
                sender.sendMail(parameters[0],
                        parameters[1],
                        "marketing@dmsinfosystem.com",
                        parameters[2]);
                pr = 100;
            } catch (Exception e) {
                pr = -1;
                Log.e("SendMail", e.getMessage(), e);
            }

            return pr;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            if(result == 100) {
                Log.i("Email Sender", "Email Sent Succesfully");
            }else if (result == -1){
                Log.i("Email Sender", "Some error was encountered");
            }
        }
    }

    private class MessageSendingTask extends AsyncTask<String,Integer,Long> {

        protected Long doInBackground(String... parameters) {
            long pr = 0;
            int cnt = parameters.length;
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,5000);
            HttpConnectionParams.setSoTimeout(httpParams,5000);

            HttpClient httpclient = new DefaultHttpClient(httpParams);

            HttpPost httppost = new HttpPost("http://59.162.167.52/api/MessageCompose?admin=contact@dmsinfosystem.com&user=amit@dmsinfosystem.com:W124X4&senderID=TEST%20SMS&receipientno=" + parameters[0] + "&msgtxt=" + parameters[1] + "&state=4");


            Log.i("CUSTOMER", "" + parameters[0]);
            try {
                // Add your data

                Log.i("trying", "trying");

                // Execute HTTP Post Request

                HttpResponse response = httpclient.execute(httppost);
                String responseBody = EntityUtils.toString(response.getEntity());
                Log.i("response", responseBody);
                pr = 100;
                return pr;
            } catch (Exception e) {
                pr = -1;
                Log.e("SendMail", e.getMessage(), e);

            }
            pr = -2;
            return pr;

        }


        protected void onProgressUpdate(Integer... progress) {

        }


        protected void onPostExecute(Long result) {
            if(result == 100) {

                Log.i("SMS Sender", "SMS Sent Succesfully");
                Intent data = new Intent();
                setResult(Activity.RESULT_OK, data);
                finish();

            }else if (result == -1){

                Toast.makeText(getApplicationContext(), "Sorry!! some error was encountered",Toast.LENGTH_SHORT).show();
                Log.i("SMS Sender", "Sorry!! some error was encountered");

            }else if (result == -2){

                Log.i("SendMail", "ConnectionTimeOut");
                Intent data = new Intent();
                setResult(Activity.RESULT_CANCELED, data);
                finish();
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