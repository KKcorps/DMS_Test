package com.dmsinfosystem;

/**
 * Created by root on 8/12/14.
 */
import java.lang.reflect.Array;
import java.util.HashMap;
        import java.util.List;

        import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private HashMap<String,int[]> _listDataPrices = new HashMap<String, int[]>();
    private String _button_id;
    private Cartsql _helper;
    private SQLiteDatabase _db;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData,String button_id,SQLiteDatabase db,Cartsql helper) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._button_id =button_id;
        this._db=db;
        this._helper=helper;
        initialisePrices();
    }


    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.groupfeatures, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.text);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.newgrouprow, null);
        }
        //LinearLayout headerLayout = (LinearLayout) convertView.findViewById(R.id.groupHeadingLayout);

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.groupText);
        TextView groupPriceInfo = (TextView) convertView.findViewById(R.id.groupPrice);

        final Button buyButton = (Button) convertView.findViewById(R.id.buyButton);
        final int buttonPrice = _listDataPrices.get(_button_id.replace(" ",""))[groupPosition];

        //final String buttonName = _listDataHeader.get(groupPosition)+ " " +_button_id.replace(" ","");
        final String buttonName = '"'+_listDataHeader.get(groupPosition) +" "+_button_id+'"';

        if(buttonPrice==0) {
            groupPriceInfo.setText("Custom Price");
            buyButton.setVisibility(View.INVISIBLE);
            LinearLayout grouprowInfoText = (LinearLayout) convertView.findViewById(R.id.groupRowInfoText);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
            grouprowInfoText.setLayoutParams(layoutParams);

            grouprowInfoText.setGravity(Gravity.CENTER_VERTICAL);

        }
        else groupPriceInfo.setText("Rs. "+ String.valueOf(buttonPrice)+"/Year");

        Cursor cur = _db.rawQuery("SELECT * FROM " + "Users" + " where name='" + buttonName + "'", null);

        if (cur.getCount()>0) {
            Log.i("Buy", "Cursor is Null");
            buyButton.setText("Added");
            buyButton.setBackgroundResource(R.drawable.button_noclick);
            //buyButton.setClickable(false);
            //buyButton.setEnabled(false);
        }

        //Shared preferences to get the total amount

        preferences = _context.getSharedPreferences("MyPref",Context.MODE_PRIVATE);

        editor = preferences.edit();
        //if(preferences.contains("Total Price")){

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                _helper = new Cartsql(_context);

                Cursor cur = _db.rawQuery("SELECT * FROM " + "Users" + " where name='" + buttonName + "'", null);

                if (cur.getCount()<=0) {
                    Log.i("Buy","Cursor is Null");
                    _helper.onInsert(_db, buttonName, buttonPrice);
                    buyButton.setText("Added");
                    buyButton.setBackgroundResource(R.drawable.button_noclick);
                    //buyButton.setClickable(false);
                    //buyButton.setEnabled(false);

                    int TotalPrice = preferences.getInt("Total Price", 0);
                    editor.putInt("Total Price",TotalPrice+buttonPrice).apply();
                    editor.commit();
                    Toast.makeText(_context, "Item added to Cart", Toast.LENGTH_SHORT).show();

                }else{
                    Log.i("Buy","Cursor is not Null");
                    Toast.makeText(_context, "Item already present in Cart", Toast.LENGTH_SHORT).show();
                }






                    Log.i("Buy", "Buy Button working " + _button_id);

                    //Log.i("Buy","Buy Button working " + String.valueOf(groupPosition));

                }

        });
        //buyButton.setId();
        //lblListHeader.setTypeface(null, Typeface.BOLD);
        //lblListHeader.setTextColor(Color.BLACK);
        //lblListHeader.setTextSize(16);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void initialisePrices(){
        int[] packagesPrice = new int[]{999,1499,2999,4999};
        _listDataPrices.put("WebHosting",packagesPrice);

        packagesPrice = new int[]{7999,14999,25999,32999};
        _listDataPrices.put("ResellerHosting",packagesPrice);

        packagesPrice = new int[]{1499,3999,9999,15999};
        _listDataPrices.put("EmailHosting",packagesPrice);

        packagesPrice = new int[]{6999,8999,14999,22999,39999};
        _listDataPrices.put("WebsiteDevelopment",packagesPrice);

         packagesPrice = new int[]{0};
        _listDataPrices.put("WebsiteRedesigning",packagesPrice);

        packagesPrice = new int[]{19990,22990,39990,59990};
        _listDataPrices.put("ECommerceDevelopment",packagesPrice);

        packagesPrice = new int[]{4999,8999,14999,19999};
        _listDataPrices.put("SearchEngineOptimisation",packagesPrice);

        packagesPrice = new int[]{5999,11999,16999,19999};
        _listDataPrices.put("SocialMediaMarketing",packagesPrice);

        packagesPrice = new int[]{3000,5000,15000};
        _listDataPrices.put("LogoDesigning",packagesPrice);

        packagesPrice = new int[]{0};
        _listDataPrices.put("EmailerDesigning",packagesPrice);

        packagesPrice = new int[]{2999,4999,9999,0};
        _listDataPrices.put("BrochureDesigning",packagesPrice);

        packagesPrice = new int[]{3999,7999,11999};
        _listDataPrices.put("StationaryDesigning",packagesPrice);

        packagesPrice = new int[]{2000,2000,4999,4999};
        _listDataPrices.put("CustomDesigning",packagesPrice);

        packagesPrice = new int[]{0};
        _listDataPrices.put("Websitemaintenance",packagesPrice);

        packagesPrice = new int[]{0};
        _listDataPrices.put("BulkSMS",packagesPrice);

        packagesPrice = new int[]{0};
        _listDataPrices.put("BulkEmailer",packagesPrice);

        packagesPrice = new int[]{0};
        _listDataPrices.put("ContentWriting",packagesPrice);

    }
}
