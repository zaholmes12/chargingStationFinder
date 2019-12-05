package com.example.chargingstationfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;


public class MainActivity extends AppCompatActivity {

    EditText longitude;
    EditText latitude;
    Button scan;
    ListView olist;
    ArrayList<output> outputs = new ArrayList<>(100);
    MyListAdapter myAdapter;
    output op = new output(null,0,0,00);
    ProgressBar progress;
    Toolbar tb;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finalproject);
        longitude = findViewById(R.id.lon);
        latitude = findViewById(R.id.lat);
        scan = findViewById(R.id.scan);
        olist = findViewById(R.id.lv);
        progress = findViewById(R.id.ps);
        tb = findViewById(R.id.tbar);
        setActionBar(tb);
        ArrayList<output> outputs = new ArrayList<>(100);
        MyListAdapter myAdapter;



        DatabaseHelper dpopener = new DatabaseHelper(this);
        outputs = dpopener.getAllMessage();

        myAdapter = new MyListAdapter();
        olist.setAdapter(myAdapter);



        //Scans the values and displays them
        if(scan != null){
            scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new carchargingQuery().execute();
                    String val1 = longitude.getText().toString();
                    int nval1 = Integer.parseInt(val1);
                    String val2 = latitude.getText().toString();
                    int nval2 = Integer.parseInt(val2);
                    op.check(nval1,nval2);
                }
            });
        }

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.itemstoadd,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Item1:
                info();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public  void info(){
        View middle = getLayoutInflater().inflate(R.layout.info,null);

        AlertDialog.Builder abuilder = new AlertDialog.Builder(this);
        abuilder.setMessage("What is new Message")
                .setPositiveButton("Overflow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "You clicked on the overflow menu", Toast.LENGTH_SHORT).show();
                        prefs = getSharedPreferences("reserve", MODE_PRIVATE);
                        String previous= prefs.getString("reserve","");

                    }
                })
                .setNegativeButton("Negative", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // What to do on Cancel

                    }
                }).setView(middle);

        abuilder.create().show();
    }

    //stores everything in the list
        private class MyListAdapter extends BaseAdapter {

            public int getCount() {  return outputs.size();  } //This function tells how many objects to show

            public output getItem(int position) { return outputs.get(position);  }  //This returns the string at position p

            public long getItemId(int p) { return p; } //This returns the database id of the item at position p

            public View getView(int p, View recycled, ViewGroup parent)
            {
                View thisRow;

                thisRow = getLayoutInflater().inflate(R.layout.finalproject, null);
                olist = thisRow.findViewById(R.id.lv);

                return thisRow;
            }
        }
        //reads the site provided and sets it up for displaying
        private class carchargingQuery extends AsyncTask<String, Integer, String>{
            String ln = "";
            String latt = "";
            String Longi = "";
            String Phone = "";

            @Override                       //Type 1
            protected String doInBackground(String... strings) {
                String queryURL = "https://torunski.ca/FinalProjectCarCharging.xml";

                try {       // Connect to the server:
                    URL url = new URL(queryURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream inStream = urlConnection.getInputStream();

                    //Set up the XML parser:
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(inStream, "UTF-8");

                    //Iterate over the XML tags:
                    int EVENT_TYPE;         //While not the end of the document:
                    while ((EVENT_TYPE = xpp.getEventType()) != XmlPullParser.END_DOCUMENT) {
                        switch (EVENT_TYPE) {
                            case START_TAG:         //This is a start tag < ... >
                                String tagName = xpp.getName(); // What kind of tag?
                                if (tagName.equals(null)) {

                                } else if (tagName.equals("LocationTitle")) {
                                    ln = xpp.getAttributeValue(null,"LocationTitle");
                                    publishProgress(25);
                                } else if (tagName.equals("Longitude")) {
                                    Longi = xpp.getAttributeValue(null,"Longitude");
                                    publishProgress(45);
                                } else if(tagName.equals("Latitude")){
                                    latt = xpp.getAttributeValue(null,"Latitude");
                                    publishProgress(50);
                                } else{
                                    Phone = xpp.getAttributeValue(null,"phone");
                                    publishProgress(100);
                                }
                                break;
                            case END_TAG:           //This is an end tag: </ ... >
                                break;
                            case TEXT:              //This is text between tags < ... > Hello world </ ... >
                                break;
                        }
                        xpp.next(); // move the pointer to next XML element
                    }
                } catch (MalformedURLException mfe) {

                } catch (IOException ioe) {

                } catch (XmlPullParserException pe) {

                }
                //What is returned here will be passed as a parameter to onPostExecute:
                return null;
            }

            @Override                   //Type 3
            protected void onPostExecute(String sentFromDoInBackground) {
                super.onPostExecute(sentFromDoInBackground);
                //update GUI Stuff:
                double val1 = Double.parseDouble(Longi);
                double val2 = Double.parseDouble(latt);
                long val3 = Long.parseLong(Phone);
                output op = new output(ln,val1,val2,val3);
                Toast.makeText(MainActivity.this, "Everything is scanned", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                progress.setProgress(values[0]);
            }
        }
    }

