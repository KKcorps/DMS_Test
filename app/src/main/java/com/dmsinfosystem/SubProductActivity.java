package com.dmsinfosystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jxl.format.*;
import jxl.read.biff.BiffException;
import jxl.read.biff.File;
import jxl.*;
import jxl.read.biff.WorkbookParser;
/**
 * Created by root on 8/12/14.
 */

public class SubProductActivity extends Activity implements GestureOverlayView.OnGesturePerformedListener{

    private String button_d;
    private Cartsql helper;
    private SQLiteDatabase db;

    private ExpandableListView mExpandableListView;
    private ExpandableListAdapter mExpandableListAdapter;

    private TextView mSubProduct;
    private String SubProductHeading;
    private LinearLayout subproductll;

    private List<String> HostingPackages = new ArrayList<String>();

    private List<String> BasicPackages = new ArrayList<String>();
    private List<String> StandardPackages = new ArrayList<String>();
    private List<String> PremiumPackages = new ArrayList<String>();
    private List<String> PremiumPlusPackages = new ArrayList<String>();
    private HashMap<String, List<String> > ChildPackages = new HashMap<String, List<String> >() ;
    private static int j, SheetNo;
    private Intent product,activityFromFooter;
    GestureLibrary mLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity);
        subproductll = (LinearLayout) findViewById(R.id.subActivityll);

        product = getIntent();
        db = openOrCreateDatabase("test_users.db", Context.MODE_PRIVATE, null);

        helper = new Cartsql(getApplicationContext());
        helper.onCreate(db);

        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        mSubProduct = (TextView) findViewById(R.id.subProductHeading);
        SubProductHeading = product.getStringExtra("subProduct");
        mSubProduct.setText( SubProductHeading );

        List<String> serviceProduct = Arrays.asList(getResources().getStringArray(R.array.productServices));
        InputStream workbookStream;

        if(serviceProduct.contains(SubProductHeading)){
            SheetNo = serviceProduct.indexOf(SubProductHeading);
            workbookStream = getResources().openRawResource(R.raw.servicepackages);
        }else {
            SheetNo = decideSheet(SubProductHeading);
            workbookStream = openWorkbookStream(SubProductHeading.split(" ")[1]);
        }

        initialiseList(SubProductHeading, SheetNo, workbookStream);

        mExpandableListAdapter = new com.dmsinfosystem.ExpandableListAdapter(this, HostingPackages, ChildPackages,SubProductHeading ,db,helper);
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

                //Toast.makeText(getApplicationContext(),"Click working", Toast.LENGTH_SHORT).show();
                Log.i("Expendables", "Group click listener working");
                return false;
            }
        });

        if(mExpandableListAdapter.getGroupCount()==1) {
            mExpandableListView.expandGroup(0);
            //LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            //View groupView = inflater.inflate(R.layout.newgrouprow,null);

            //Button BuyButton = (Button) groupView.findViewById(R.id.buyButton);
            //BuyButton.setVisibility(View.INVISIBLE);
        }

        initialiseFooter();

        //GesturePerformer
        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!mLibrary.load()) {
            Log.i("GestureListener","No Library Found");
        }

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(this);


        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected int decideSheet(String subProductHeading){
        int sheetNoDecide = 0;
        subProductsEnum sn = subProductsEnum.valueOf(subProductHeading.replaceAll("\\s","").toUpperCase());

        switch (sn){
            case WEBSITEREDESIGNING :
                sheetNoDecide = 0;
                break;

            case LOGODESIGNING:
                sheetNoDecide = 1;
                break;

            case BROCHUREDESIGNING:
                sheetNoDecide = 2;
                break;

            case STATIONARYDESIGNING:
                sheetNoDecide = 3;
                break;

            case CUSTOMDESIGNING:
                sheetNoDecide = 4;
                break;

            case EMAILERDESIGNING:
                sheetNoDecide = 5;
                break;

            case WEBHOSTING:
                sheetNoDecide = 0;
                break;

            case RESELLERHOSTING:
                sheetNoDecide = 1;
                break;

            case EMAILHOSTING:
                sheetNoDecide = 2;
                break;

            case WEBSITEDEVELOPMENT:
                sheetNoDecide = 0;
                break;

            case ECOMMERCEDEVELOPMENT:
                sheetNoDecide = 1;
                break;

            case SEARCHENGINEOPTIMISATION:
                sheetNoDecide = 0;
                break;

            case SOCIALMEDIAMARKETING:
                sheetNoDecide = 1;
                break;

            default:
                sheetNoDecide = 0;

        }
        return  sheetNoDecide;
    }

    protected InputStream openWorkbookStream(String ProductHeading){
        InputStream Is;
        Resources rs = getResources();
        productsEnum pn = productsEnum.valueOf(ProductHeading.toUpperCase());
        switch (pn){
            case DESIGNING:
                Is = rs.openRawResource(R.raw.designingpackages);
                Log.i("File Reader", "Designing");
                break;

            case REDESIGNING:
                Is = rs.openRawResource(R.raw.designingpackages);
                Log.i("File Reader", "Designing");
                break;

            case HOSTING:
                Is = rs.openRawResource(R.raw.hostingpackages);
                Log.i("File Reader", "Hosting");
                break;

            case DIGITAL:
                Is = rs.openRawResource(R.raw.digitalpackages);
                Log.i("File Reader", "Digital");
                break;

            case DEVELOPMENT:
                Is = rs.openRawResource(R.raw.developmentpackages);
                Log.i("File Reader", "Development");
                break;

            case ENGINE:
                Is = rs.openRawResource(R.raw.digitalpackages);
                Log.i("File Reader", "SEO");
                break;

            case MEDIA:
                Is = rs.openRawResource(R.raw.digitalpackages);
                Log.i("File Reader", "SMM");
                break;

            default:
                Is = rs.openRawResource(R.raw.hostingpackages);
                Log.i("File Reader", "Default Hosting");
        }
        return Is;
    }

    public enum subProductsEnum {
        WEBSITEREDESIGNING, LOGODESIGNING, BROCHUREDESIGNING, STATIONARYDESIGNING, CUSTOMDESIGNING, EMAILERDESIGNING,
        WEBHOSTING, RESELLERHOSTING, EMAILHOSTING,
        SEARCHENGINEOPTIMISATION,SOCIALMEDIAMARKETING,
        WEBSITEDEVELOPMENT,ECOMMERCEDEVELOPMENT
    }

    public enum productsEnum{
        DESIGNING,REDESIGNING,HOSTING,DIGITAL,DEVELOPMENT,ENGINE,MEDIA
    }

    protected void initialiseList(String subProductHeading, int SheetNo, InputStream workbook){
        List<String> resultSet = new ArrayList<String>();
        List<String> features = new ArrayList<String>();

        String packageName;
        int initalColumn = 0;
        boolean includeFeaturesColumn = false;

        File inputWorkbook = new File(new byte[]{});

        //Important: InputStream inputStream = getResources().openRawResource(R.raw.hostingpackages);

        if(null != inputWorkbook){
            Workbook w;
            try {
                w = Workbook.getWorkbook(workbook);

                // Get the first sheet
                Log.i("File Reader", "Number of Sheets: " + String.valueOf(w.getNumberOfSheets()) );

                Sheet sheet = w.getSheet(SheetNo);
                Log.i("File Reader", "Sheet no" + String.valueOf(SheetNo));

                String spn = sheet.getCell(0,0).getContents();

                if(spn.equals("Email-Hosting") || spn.equals("ECommerce-Hosting") ||spn.equals("Website Development") ||spn.equals("Activities")
                    || spn.equals("Brochure Designing")){
                    initalColumn = 1;
                    includeFeaturesColumn = true;
                    for(int j=0;j< sheet.getRows();j++){
                        Cell featureCell = sheet.getCell(0,j);
                        features.add(featureCell.getContents());
                    }
                }

                Log.i("File Reader", "Columns: " + String.valueOf(sheet.getColumns()) );

                // Loop over column and lines
                for (int j = initalColumn; j < sheet.getColumns(); j++) {
                    resultSet = new ArrayList<String>();
                    Cell cell = sheet.getCell(j, 0);
                    packageName = cell.getContents();

                    if(packageName!="") {
                        HostingPackages.add(packageName);
                    }else{
                        Log.i("File Reader", "Columns Ended");
                        break;
                    }

                    if(!cell.getContents().isEmpty()){
                        for (int i = 1; i < sheet.getRows(); i++) {
                            Cell cel = sheet.getCell(j, i);
                            //jxl.format.CellFormat format = cel.getCellFormat();

                            if(includeFeaturesColumn) resultSet.add(cel.getContents() + " " + features.get(i));
                            else resultSet.add(cel.getContents());

                            Log.i("File Reader","Cell Contents: "+cel.getContents());
                            if(cel.getContents()=="") break;
                      }
                    }

                    if(resultSet.size()!=0) {
                        ChildPackages.put(packageName, resultSet);
                    }else{
                        Log.i("File Reader", "File Ended");
                        break;
                    }
                    //continue;
                }
            } catch (BiffException e) {
                e.printStackTrace();
                Log.i("File Reader", "BIFF Exception");
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("File Reader", "Other Exceptions");
            }
        }
        else
        {
            resultSet.add("File not found..!");
            Log.i("File Reader", "File not Found");
        }
        if(resultSet.size()==0){
            //resultSet.add("Data not found..!");
            Log.i("File Reader", "Data not Found");
        }

    }

    public static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[is.available()];
        int i = Integer.MAX_VALUE;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray(); // be sure to close InputStream in calling function
    }

    private void initialiseFooter(){
        View footerView =  ((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);

        final ImageButton BSearch = (ImageButton) footerView.findViewById(R.id.footerSearch);

        BSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityFromFooter = new Intent(SubProductActivity.this, SearchListActivity.class);
                startActivity(activityFromFooter);

                //BSearch.setBackgroundColor(Color.parseColor("#FDC619"));
            }
        });


        ImageButton BCart = (ImageButton) footerView.findViewById(R.id.footerCart);
        BCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityFromFooter = new Intent(SubProductActivity.this, NewCartActivity.class);
                startActivity(activityFromFooter);
            }
        });

        ImageButton BHome = (ImageButton) footerView.findViewById(R.id.footerHome);

        BHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //activityFromFooter = new Intent(SubProductActivity.this, SearchListActivity.class);
                //startActivity(activityFromFooter);
                //view.setBackgroundColor(Color.parseColor("#FDC619"));
                finish();
            }
        });

        ImageButton BFaceBook = (ImageButton) footerView.findViewById(R.id.footerFaceBook);
        BFaceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityFromFooter = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/DMSInfoSystem"));
                startActivity(activityFromFooter);
            }
        });

        ImageButton BLinkedIn = (ImageButton) footerView.findViewById(R.id.footerLinkedIn);
        BLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityFromFooter = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.linkedin.com/company/dms-infosystem-pvt-ltd-"));
                startActivity(activityFromFooter);
            }
        });

        subproductll.addView(footerView);

    }

    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = mLibrary.recognize(gesture);

        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
            String result = predictions.get(0).name;
            if ("close".equalsIgnoreCase(result)) {
                //Toast.makeText(this, "Opening the document", Toast.LENGTH_LONG).show();
                Log.i("GestureListener","CLose Detected");
                finish();
            }else if("okay".equalsIgnoreCase(result)){
                Log.i("Gesture Listener", "Okay detected");
                Intent cartActivity = new Intent(SubProductActivity.this, NewCartActivity.class);
                startActivity(cartActivity);
            }
        }

        Log.i("GestureListener","Inside Gesture Listener");
    }

    @Override
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

