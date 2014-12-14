package com.students.lmi.lmi_app;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    SimpleAdapter sAdapter;
    Elements title;
    int k=0;
    int t=15; //t - индекс последнего прогруженного элемента
    boolean isFirstTime=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        newsCount = 0;
        t = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        String filename = "TitlesList";
        File file = new File(getFilesDir(), filename);
        new siteParser().execute();//Парсим из сайта/файла.
       if ((file.exists()||file.length()!=0)) //Если файл с кэшем создан и не пуст, то подгружаем новости (Первые 15)
       {
           String[] from = {ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE, ATTRIBUTE_NAME_DATETEXT};
           // массив ID View-компонентов, в которые будут вставлять данные
           int[] to = {R.id.tvText, R.id.ivImg, R.id.tvDate};
           sAdapter = new SimpleAdapter(this, data, R.layout.item, from, to);

           ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(sAdapter);
           animationAdapter.setAbsListView(getListView());
           setListAdapter(animationAdapter);//Ставим адаптер с анимацией
           sAdapter.notifyDataSetChanged();// Ставим уведомлялку изменения контента на адаптер
           isFirstTime = false;
       }
       new siteParser().execute();//Парсим из сайта/файла.
       getListView().setOnScrollListener(scrollListener); //Cвязываем наш список с ScrollListener(он создан ниже)
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    public class siteParser extends AsyncTask<String, Void, String> //непосредственно сам парсер
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
                    output.close();
                    Log.i("Parsing", "finished, t="+Integer.toString(t));
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
                    Log.i("Parsing", "finished, t="+Integer.toString(t));
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(News.this,CurrentNews.class);
        startActivity(intent);
        //TODO В этой активности нужно сделать вывод полной статьи/новости с сайта
        Log.i("poz", Integer.toString(position));
        //intent.putExtra("new",newslist.get(position));
        super.onListItemClick(l, v, position, id);


    }
    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() { //Создаем обработчик прокрутки
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

        }

        @Override
        public void onScroll(AbsListView absListView, int first, int i2, int total) { //AbsListView absListView - список,
            //first - первый видимый элемент,  i2 - количество видимых элементов, total - всего элементов в списке
            Log.i("Первый элемент в списке:", Integer.toString(first));
            if (first==total-i2&&total<k) {
                for (int i=t; i<t+15; i++) if (i<299) { //пихаем новые 15 новостей в массив
                    Map<String,Object> m;
                    m = new HashMap<String, Object>();
                    m.put(ATTRIBUTE_NAME_TEXT, titlelist.get(i));
                    m.put(ATTRIBUTE_NAME_IMAGE,R.drawable.ic_launcher);
                    m.put(ATTRIBUTE_NAME_DATETEXT, datelist.get(i));
                    data.add(m);
                }
                t+=15; //изменяем ту самую t на 15
                sAdapter.notifyDataSetChanged(); //Ставим уведомитель x2
            }
        }
    };
}