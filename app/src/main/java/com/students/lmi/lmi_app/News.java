package com.students.lmi.lmi_app;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class News extends ListActivity{
    int newsCount = 0;
    public static ArrayList<String> newslist = new ArrayList<String>();
    public static ArrayList<String> titlelist = new ArrayList<String>();
    public static ArrayList<String> datelist = new ArrayList<String>();
    public static ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(100000);
    final String ATTRIBUTE_NAME_TEXT = "text";
    final String ATTRIBUTE_NAME_IMAGE = "image";
    final String ATTRIBUTE_NAME_DATETEXT = "date";
    Elements title;
    int k=0;
    int idOfNew = 309;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        newsCount = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        // Setting onClickListeners on buttons and doing some usual initializing whatnot
        String filename = "TitlesList"; // Checking if file is exists,ya
        File file = new File(getFilesDir(), filename);
        String file_gen_name = "NewsList";
        File file_gen = new File(getFilesDir(), file_gen_name);

       if ((file.exists()||file.length()!=0))
       {
           String[] from = { ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE, ATTRIBUTE_NAME_DATETEXT };
           // массив ID View-компонентов, в которые будут вставлять данные
           int[] to = { R.id.tvText, R.id.ivImg, R.id.tvDate };
           SimpleAdapter sAdapter = new SimpleAdapter(this, data,R.layout.item, from, to);
       }
       new siteParser().execute();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
   /* public class siteParseNews extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... arg)
        {
            int n=0;
            Document Page;
            String filename = "NewsList";
            File file_gen = new File(getFilesDir(), filename);
            for (int i=309-n;)
            return null;
        }
    }*/

    public class siteParser extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... arg)
        {
            k=0;
            Log.i("Parsing", "started");
            Document Page;
            titlelist.clear();
            datelist.clear();
            String filename = "TitlesList";
            File file = new File(getFilesDir(), filename);
            String file_gen_name = "NewsList";
            File file_gen = new File(getFilesDir(), file_gen_name);
            if ((!file.exists()||file.length()==0)) {
                try {
                    Log.i("Parsing", "started");
                    file.createNewFile();
                    BufferedWriter output = new BufferedWriter(new FileWriter(file));
                    String site = "http://lmi-school.ru/?p=2";
                    Page = Jsoup.connect(site).get();
                    title = Page.select(".titlediv");
                    for (Element titles : title) {
                        titlelist.add(titles.text().substring(13));
                        datelist.add(titles.text().substring(0, 10));
                        output.write(titles.text() + "\n");
                        k++;
                    }
                    for (int i=0; i<k; i++) {
                        Map<String,Object> m;
                        m = new HashMap<String, Object>();
                        m.put(ATTRIBUTE_NAME_TEXT, titlelist.get(i));
                        m.put(ATTRIBUTE_NAME_IMAGE,R.drawable.ic_launcher);
                        m.put(ATTRIBUTE_NAME_DATETEXT, datelist.get(i));
                        data.add(m);

                    }
                    output.close();
                    Log.i("Parsing", "finished");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    BufferedReader input = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = input.readLine()) != null) {
                        titlelist.add(line.substring(13));
                        datelist.add(line.substring(0, 10));
                        k++;
                        Log.i("Parsing", "k="+Integer.toString(k));
                    }
                    for (int i=0; i<k; i++) {
                        Map<String,Object> m;
                        m = new HashMap<String, Object>();
                        m.put(ATTRIBUTE_NAME_TEXT, titlelist.get(i));
                        m.put(ATTRIBUTE_NAME_IMAGE,R.drawable.ic_launcher);
                        m.put(ATTRIBUTE_NAME_DATETEXT, datelist.get(i));
                        data.add(m);
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            //btnCreate.setEnabled(true);
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.news, menu);
        return super.onCreateOptionsMenu(menu);
    }




    public void viewNews(View V)
    {
        String[] from = { ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE, ATTRIBUTE_NAME_DATETEXT };
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.tvText, R.id.ivImg, R.id.tvDate  };
        SimpleAdapter sAdapter = new SimpleAdapter(this, data,R.layout.item, from, to);
        setListAdapter(sAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(News.this,CurrentNews.class);
        startActivity(intent);
        Log.i("poz", Integer.toString(position));
        //intent.putExtra("new",newslist.get(position));
        super.onListItemClick(l, v, position, id);
    }
}