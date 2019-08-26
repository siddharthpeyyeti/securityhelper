 package com.siddharth.securityhelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.provider.CallLog;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView viewImage ;
    TextView callDetails ;
    TextView showDbgStmt ;

    Spinner houseNumber;
    Spinner floorNumber;
    Spinner buildingNumber;

    StringBuilder dbgstmt = new StringBuilder("");
    StringBuilder sb = new StringBuilder();

    String phnumber;
    String picturePath;

    String todayDate;
    String callTime;
    File photoFolder = new File("/mnt/sdcard/.SecurityHelper/photos/");
    File photoFile = new File("/mnt/sdcard/.SecurityHelper/photos/photo.jpg");
    String photoFileName;

    File reportFile = new File("/mnt/sdcard/.SecurityHelper/report.csv");
    StringBuilder visitorFileName = new StringBuilder();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewImage = (ImageView) findViewById(R.id.viewImage);
        callDetails = (TextView) findViewById(R.id.viewCallDetails);
        showDbgStmt = (TextView) findViewById(R.id.debugStmt);

        showDbgStmt.setText(dbgstmt.toString());

        houseNumber = (Spinner) findViewById(R.id.houseNumber);
        floorNumber = (Spinner) findViewById(R.id.floorNumber);
        buildingNumber = (Spinner) findViewById(R.id.buildingNumber);

        String[] houseNumbers = new String[]{"H.No", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60"};
        ArrayAdapter<String> houseNumberAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, houseNumbers);
        houseNumber.setAdapter(houseNumberAdapter);

        String[] floorNumbers = new String[]{"Flr", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
        ArrayAdapter<String> floorNumberAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, floorNumbers);
        floorNumber.setAdapter(floorNumberAdapter);

        String[] buildingNumbers = new String[]{"Bldg", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        ArrayAdapter<String> buildingNumberAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, buildingNumbers);
        buildingNumber.setAdapter(buildingNumberAdapter);

        Button btn = (Button) findViewById(R.id.NoPhone);
        btn.setText("No Phone");

        //start camera app

        if (!photoFolder.exists()) {
            photoFolder.mkdirs();
        }

        startCamera();
    }

    public void startCamera()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        if (cameraIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            callTime = new SimpleDateFormat("kk-mm-ss").format(new Date());

            // move photo.jpg to photofilename
            photoFileName = "/mnt/sdcard/.SecurityHelper/photos/"+todayDate+"_"+callTime+".jpg";
            File to = new File(photoFileName);
            photoFile.renameTo(to);

            this.Refresh();
        }
        else
        {
            finish();
        }
    }

//    private static int RESULT_LOAD_IMAGE = 1;

    public void Refresh()
    {
        sb.delete(0,sb.capacity());
        sb.append("Phone Number: No Phone     ");
        Cursor mCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = mCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = mCursor.getColumnIndex(CallLog.Calls.TYPE);

        while (mCursor.moveToNext()) {
            String calltype = mCursor.getString(type);
            if (Integer.parseInt(calltype) == CallLog.Calls.MISSED_TYPE) {
                phnumber = mCursor.getString(number);
                sb.replace(14, 27, phnumber);
                sb.append(System.getProperty("line.separator"));

                break;
            }
        }

        callDetails.setText(sb.toString());

        houseNumber.setSelection(0);
        floorNumber.setSelection(0);
        buildingNumber.setSelection(0);

        /*
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Uri selectedImage = intent.getData();
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
        c.moveToLast();
        int columnIndex = c.getColumnIndex(filePath[0]);
        picturePath = c.getString(columnIndex);
        c.close();
        Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
        */

        Bitmap thumbnail = (BitmapFactory.decodeFile(photoFileName));
        viewImage.setImageBitmap(thumbnail);
        Log.w("path of image from gallery......******************.........", photoFileName);

        Button btn = (Button) findViewById(R.id.register_visitor);
        btn.setEnabled(true);
    }

    public void NoPhone(View view)
    {
        Button btn = (Button) findViewById(R.id.NoPhone);
        if (btn.getText() == "No Phone")
        {
            sb.replace(14, 27, "No Phone     ");
            if (phnumber.isEmpty())
            {
                btn.setText("No Phone");
            }
            else
            {
                btn.setText(phnumber);
            }
        }
        else
        {
            sb.replace(14, 27, phnumber);
            btn.setText("No Phone");
        }

        callDetails.setText(sb.toString());
    }

    public void RegisterVisitor(View view)
    {
        visitorFileName.delete(0,sb.capacity());
        visitorFileName.append(callTime);
        visitorFileName.append("_");

        if(houseNumber.getSelectedItem().toString().equals("H.No"))
        {
            dbgstmt.replace(0, dbgstmt.capacity(), "SELECT A H.NO");
            showDbgStmt.setText(dbgstmt.toString());
            return;
        }
        visitorFileName.append(houseNumber.getSelectedItem().toString());

          /*
          if(floorNumber.getSelectedItem().toString().equals("Flr"))
          {
              dbgstmt.replace(0,dbgstmt.capacity(),"SELECT A FLR");
              showDbgStmt.setText(dbgstmt.toString());
              return;
          }
          visitorFileName.append("-"+floorNumber.getSelectedItem().toString());

          if(buildingNumber.getSelectedItem().toString().equals("Bldg"))
          {
              dbgstmt.replace(0,dbgstmt.capacity(),"SELECT A BLDG");
              showDbgStmt.setText(dbgstmt.toString());
              return;
          }
          visitorFileName.append("-"+buildingNumber.getSelectedItem().toString());
          */
        visitorFileName.append(".txt");

        try
        {
            if (!(reportFile.exists()))
            {
                reportFile.createNewFile();
            }

            FileWriter fw1 = new FileWriter(reportFile,true);
            fw1.write(todayDate+" "+callTime+","
                      +houseNumber.getSelectedItem().toString()+","
                      +
                    phnumber+","+photoFileName
                      +System.getProperty("line.separator"));
            fw1.flush();
            fw1.close();
            reportFile.setWritable(false);

            String todayDirName = "/mnt/sdcard/.SecurityHelper" + "/" + todayDate;
            File folder = new File(todayDirName);
            if (!folder.exists())
            {
                folder.mkdirs();
            }

            File visitorsList = new File(todayDirName,visitorFileName.toString());
            if (!(visitorsList.exists()))
            {
                visitorsList.createNewFile();
            }

          sb.append("Picture Name: ");
          sb.append(photoFileName);
          sb.append(System.getProperty("line.separator"));

          FileWriter fw2 = new FileWriter(visitorsList,true);
          fw2.write(sb.toString());
          fw2.flush();
          fw2.close();
          visitorsList.setWritable(false);

          sb.delete(0,sb.capacity());

          dbgstmt.replace(0, dbgstmt.capacity(), "");
          showDbgStmt.setText(dbgstmt.toString());

          Button btn = (Button) findViewById(R.id.register_visitor);
          btn.setEnabled(false);

          //finish();
            startCamera();

        }
        catch (IOException e)
        {
            Log.e("","");
        }
    }
}


