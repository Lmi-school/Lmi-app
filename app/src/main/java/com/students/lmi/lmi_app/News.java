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
import android.util.Log;
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

    private MyScrollView scrollView;
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
        {scrollView = (MyScrollView) findViewById(R.id.scrollView);
        scrollView.setOnScrollViewChange(new MyScrollView.OnScrollViewListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                Log.d("androidcasts", "onScrollChanged");
            }

            @Override
            public void onScrollBottomDetect() {
                Log.d("androidcasts", "onScrollBottomDetect");

            }

            @Override
            public void onScrollTopDetect() {
                Log.d("androidcasts", "onScrollTopDetect");
            }
        });}
        // Setting onClickListeners on buttons and doing some usual initializing whatnot
        mainLinear = (LinearLayout) findViewById(R.id.mainLinear);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(this);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(this);
        String filename = "TitlesList"; // Checking if file is exists,ya
        File file = new File(getFilesDir(), filename);
        if ((!(file.exists())||file.length()==0)) btnCreate.setEnabled(false); // if not then disabling button; it's more right, ya?
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
            Log.i("Parsing", "started");
            Document Page=null;
            newslist.clear();
            titlelist.clear();
            datelist.clear();
            String filename = "TitlesList";
            File file = new File(getFilesDir(), filename);
            if ((!(file.exists())||file.length()==0)) {
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
                startActivity(intent); // Just creating new activity for single piece of news
                // TODO open articles here
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
                    // Creating different layout parameters for future uses
                    LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(fillParent, fillParent, 1f); // Fill parent fully with 1f margins
                    LinearLayout.LayoutParams withoutMarginsParams = new LinearLayout.LayoutParams(fillParent, fillParent);  // The same but without margins
                    LinearLayout.LayoutParams text1Params = new LinearLayout.LayoutParams(wrapContent, wrapContent); // Layout for title
                    LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(wrapContent, wrapContent); // Layout for image in article preview
                                                                                                                   // with margins in next line
                    imgParams.setMargins(0, 0, 10, 0);
                    LinearLayout.LayoutParams imgDotsParams = new LinearLayout.LayoutParams(wrapContent, wrapContent); // Layout with gravity bottom for dots image
                    imgDotsParams.gravity = Gravity.BOTTOM;

                    // Transforming and adding title and date
                    TextView textNew = new TextView(this);
                    String tempString;
                    tempString = titlelist.get(newsCount).toString(); // Taking first title,check
                    SpannableString spanString = new SpannableString(tempString);
                    spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0); // Making bold and
                    textNew.setTextColor(Color.rgb(0, 36, 76)); // Coloring it
                    textNew.setTextSize(14); // Resizing
                    textNew.setText(spanString);
                    textNew.setMaxLines(2); // Setting two lines limitation
                                            // TODO rewrite limitation for all resolutions

                    TextView textDate = new TextView(this); // The same stuff for date text,test
                    tempString = datelist.get(newsCount).toString();
                    SpannableString spanString1 = new SpannableString(tempString);
                    textDate.setTextColor(Color.rgb(103, 103, 103));
                    spanString1.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString1.length(), 0);
                    textDate.setText(spanString1);
                    textDate.setGravity(80); // Bottom right corner gravity
                    textDate.setTextSize(12); // Resizing text
                    textDate.setMaxLines(1); // This would not ever be used xD

                    ImageView imgDots = new ImageView(this);
                    imgDots.setImageResource(R.drawable.ic_more_horiz_black_24dp); // Adding fancy dots in bottom right corner

                    ImageView imgNew = new ImageView(this);
                    imgNew.setImageResource(R.drawable.news1); // Placing the image
                                                               // TODO take images from the site

                    LinearLayout layoutNew = new LinearLayout(this);
                    layoutNew.setBackgroundColor(Color.rgb(165, 219, 222));  // Creating main layout to most outer one
                    layoutNew.setOrientation(horizontal);                    // to keep image and InnerFirst layout

                    LinearLayout layoutInnerFirst = new LinearLayout(this); // Layout that keeps title and InnerSecond layout
                    layoutInnerFirst.setOrientation(vertical);

                    LinearLayout layoutInnerSecond = new LinearLayout(this); // Layout for date and dots image
                    layoutInnerSecond.setOrientation(horizontal);

                    mainLinear.addView(layoutNew, lParams); // Adding main layout
                    layoutNew.addView(imgNew, imgParams); // Placing image there
                    layoutInnerFirst.addView(textNew, text1Params); // Modifying InnerFirst with title text
                    layoutNew.addView(layoutInnerFirst, withoutMarginsParams); // and placing it onto main one

                    layoutInnerFirst.addView(layoutInnerSecond, withoutMarginsParams); // Adding second inner layout to first
                    layoutInnerSecond.addView(textDate, dateParams); // Placing date text
                    layoutInnerSecond.addView(imgDots, imgDotsParams); // and fancy dots image there

                    newsCount++; // Taking next piece of news for current
                                 // TODO replace this with dynamic loading for 15-20 news at the time
                }
                break;
            case R.id.btnClear:
                mainLinear.removeAllViews(); // Clearing all the screen
                newsCount = 0; // Resetting counter to show first news first again
                break;
            case R.id.btnRefresh:
                if (k!=0) {
                    btnCreate.setEnabled(true); // TODO rewrite to auto-check of news loading state
                    Log.i("Refresh", "news");
                }
                break;
        }
    }
}