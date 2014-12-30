/*Что-то более менее работает.*/
package com.students.lmi.lmi_app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class CurrentNews extends Activity{
    String ref;
    TextView article;
    String article_text;
    Elements title;
    boolean done = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_news);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ref = getIntent().getExtras().getString("reference");
        article = (TextView)findViewById(R.id.articleText);
        article.setText("Загрузка...");
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        new siteParser().execute();
        //while (!done) cute=1;
        while (!done) article.setText(article_text); //done - переменная, проверяющая, не сделан ли случайно парсинг.
        article.setText(article_text);
        super.onResume();
    }

    public class siteParser extends AsyncTask<String, Void, String> //непосредственно сам парсер
    {
        @Override
        protected String doInBackground(String... arg)
        {
            Log.i("PARSING NEW", "started");
            Document page;
                try {
                    page = Jsoup.connect("http://www.lmi-school.ru/"+ref).get();
                    title = page.select(".text [align=justify]");
                    Log.i("PARSING NEW", "started http://www.lmi-school.ru/"+ref);
                    for (Element titles : title)
                        article_text = titles.text();
                    done = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //TODO Здесь нужно еще вытащить какую-нибудь картинку. Чтобы покрасивее было.

            return null;
        }
    }
}
