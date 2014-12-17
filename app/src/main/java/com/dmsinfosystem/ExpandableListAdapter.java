package com.dmsinfosystem;

/**
 * Created by root on 8/12/14.
 */
import java.util.HashMap;
        import java.util.List;

        import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
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
    private String _button_id;
    private Cartsql _helper;
    private SQLiteDatabase _db;
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData,String button_id,SQLiteDatabase db,Cartsql helper) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._button_id =button_id;
        this._db=db;
        this._helper=helper;
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
            convertView = infalInflater.inflate(R.layout.subtext, null);
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
            convertView = infalInflater.inflate(R.layout.grouprow, null);
        }
        //LinearLayout headerLayout = (LinearLayout) convertView.findViewById(R.id.groupHeadingLayout);

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.groupText);
        final Button buyButton = (Button) convertView.findViewById(R.id.buyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                _helper = new Cartsql(_context);
                _helper.onInsert(_db,_button_id.replace(" ","")+String.valueOf(groupPosition).replace(" ","") );

                Toast.makeText(_context,"Item Added to Cart", Toast.LENGTH_SHORT);
                buyButton.setText("Bought");

                Log.i("Buy","Buy Button working " + _button_id);

                Log.i("Buy","Buy Button working " + String.valueOf(groupPosition));

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
}
