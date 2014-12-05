package com.students.lmi.lmi_app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;


public class News extends Activity implements View.OnClickListener{

    LinearLayout mainLinear;
    Button btnCreate;
    Button btnClear;
    Button btnRefresh;

    int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
    int fillParent =  LinearLayout.LayoutParams.MATCH_PARENT;
    int horizontal = LinearLayout.HORIZONTAL;
    int vertical = LinearLayout.VERTICAL;
    int newsCount = 0;

    public static ArrayList<String> newslist = new ArrayList<String>();
    public static ArrayList<String> titlelist = new ArrayList<String>();
    public static ArrayList<String> datelist = new ArrayList<String>();

    Elements title;
    int k=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        newsCount = 0;

        new siteParser().execute();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        mainLinear = (LinearLayout) findViewById(R.id.mainLinear);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(this);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(this);
        btnCreate.setEnabled(false);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
    }
    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    public class siteParser extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... arg)
        {
            k=0;
            Document Page=null;
            newslist.clear();
            titlelist.clear();
            datelist.clear();
            String filename = "TitlesList";
            File file = new File(getFilesDir(), filename);
            if (!(file.exists())) {
                try {
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
                    output.close();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                Intent intent = new Intent(this, CurrentNews.class);
                startActivity(intent);
                String filename = "myfile";

                File file = new File(getFilesDir(), filename);
                try{
                    file.createNewFile();
                    BufferedWriter output = new BufferedWriter(new FileWriter(file));
                    output.write(file.getAbsolutePath());
                    output.close();
                    BufferedReader input = new BufferedReader(new FileReader(file));
                    String string1;
                    string1 = input.readLine();
                    input.close();

                    TextView articleText = (TextView) findViewById(R.id.articleText);
                    /*if (file.exists())*/
                    //articleText.setText(string1);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreate:
                //for (int i=0;i<k;i++){
                if (k!=0){
                    LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(fillParent, fillParent, 1f);
                    lParams.setMargins(5, 5, 5, 5);
                    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(fillParent, fillParent);
                    //textParams.setMargins(0, 0, 10, 5);
                    LinearLayout.LayoutParams withoutMarginsParams = new LinearLayout.LayoutParams(fillParent, fillParent);
                    LinearLayout.LayoutParams text1Params = new LinearLayout.LayoutParams(wrapContent, wrapContent);
                    LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(fillParent, fillParent, 1f);
                    LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(wrapContent, wrapContent);
                    imgParams.setMargins(0, 0, 10, 0);
                    LinearLayout.LayoutParams imgDotsParams = new LinearLayout.LayoutParams(wrapContent, wrapContent);
                    imgDotsParams.gravity = Gravity.BOTTOM;

                    TextView textNew = new TextView(this);
                    String tempString;
                    TextView textDate = new TextView(this);
                    tempString = titlelist.get(newsCount).toString();
                    SpannableString spanString = new SpannableString(tempString);
                    spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
//              textNew.setGravity(16);
                    textNew.setTextColor(Color.rgb(0, 36, 76));
                    textNew.setTextSize(14);
                    textNew.setText(spanString);
                    textNew.setMaxLines(2);
                    //textNew.setLineSpacing(3.2f, 0.8f);

                    tempString = datelist.get(newsCount).toString();
                    SpannableString spanString1 = new SpannableString(tempString);
                    textDate.setTextColor(Color.rgb(103, 103, 103));
                    spanString1.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString1.length(), 0);
                    textDate.setText(spanString1);
                    textDate.setGravity(80);
                    textDate.setTextSize(12);
                    textDate.setMaxLines(1);

                    ImageView imgDots = new ImageView(this);
                    imgDots.setImageResource(R.drawable.ic_more_horiz_black_24dp);

                    ImageView imgNew = new ImageView(this);
                    imgNew.setImageResource(R.drawable.news1);

                    LinearLayout layoutNew = new LinearLayout(this);
                    layoutNew.setBackgroundColor(Color.rgb(165, 219, 222));
                    layoutNew.setOrientation(horizontal);

                    LinearLayout layoutInnerFirst = new LinearLayout(this);
                    layoutInnerFirst.setOrientation(vertical);

                    LinearLayout layoutInnerSecond = new LinearLayout(this);
                    layoutInnerSecond.setOrientation(horizontal);

                    mainLinear.addView(layoutNew, lParams);
                    layoutNew.addView(imgNew, imgParams);
                    layoutInnerFirst.addView(textNew, text1Params);
                    layoutNew.addView(layoutInnerFirst, withoutMarginsParams);

                    layoutInnerFirst.addView(layoutInnerSecond, withoutMarginsParams);
                    layoutInnerSecond.addView(textDate, dateParams);
                    layoutInnerSecond.addView(imgDots, imgDotsParams);
                    newsCount++;
                }
                break;
            case R.id.btnClear:
                mainLinear.removeAllViews();
                newsCount = 0;
                break;
            case R.id.btnRefresh:
                if (k!=0) {
                    btnCreate.setEnabled(true);
                }
                break;
        }
    }
}