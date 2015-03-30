package com.students.lmi.lmi_app;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;

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
    public static ArrayList<String> referencelist = new ArrayList<String>();
    public static ArrayList<String> titlelist = new ArrayList<String>();
    public static ArrayList<String> datelist = new ArrayList<String>();
    public static ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(100000);
    final String ATTRIBUTE_NAME_TEXT = "text";
    final String ATTRIBUTE_NAME_IMAGE = "image";
    final String ATTRIBUTE_NAME_DATETEXT = "date";
    SimpleAdapter sAdapter;
    Elements title;
    Dialog dialog, dialog2;
    Elements references;
    int k=0, num=0;
    int newsLoadCount = 15; // newsLoadCount - количество новостей для прогрузки в списке
    int lastLoaded = 0; //lastLoaded - индекс последнего прогруженного элемента
    int colors[] = {R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4,R.drawable.p5};
    boolean isFirstTime=true;
    boolean isCreated;
    SwipeRefreshLayout refreshLayout;
    ScaleInAnimationAdapter animationAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent1 = new Intent(News.this,IntroActivity.class);

        isCreated = false;
        newsCount = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        String filename = "TitlesList";
        File file = new File(getFilesDir(), filename);
        String file_ref_name = "NewsList";
        File file_ref = new File(getFilesDir(), file_ref_name);
        dialog = ProgressDialog.show(News.this,"Загрузка","Подождите, новости загружаются...");
        //if (!file.exists() || file.length() == 0)
        new siteParser().execute();//Парсим из сайта/файла.
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.reflv);
        //refreshLayout = (SwipeRefreshLayout) findViewById(R.id.reflv);
        if (refreshLayout== null)
            Log.i("NULL","IT'S STILL UNKNOWN");
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String filename = "TitlesList";
                File file = new File(getFilesDir(), filename);
                String file_ref_name = "NewsList";
                File file_ref = new File(getFilesDir(), file_ref_name);
                if (file.exists()&&file.length()!=0)
                    file.delete();
                if (file_ref.exists()&&file_ref.length()!=0)
                    file_ref.delete();
                new siteParser().execute();
            }
        });
    }
    public void showResult() {
        String filename = "TitlesList";
        File file = new File(getFilesDir(), filename);
        String file_ref_name = "NewsList";
        File file_ref = new File(getFilesDir(), file_ref_name);
        if ((file.exists()&&file.length()!=0)) //Если файл с кэшем создан и не пуст, то подгружаем новости (Первые 15)
        {
            for (int i=0; i<k && i<newsLoadCount; i++){  //пихаем первую партию новостей в массив
                Map<String,Object> m;
                m = new HashMap<String, Object>();
                m.put(ATTRIBUTE_NAME_TEXT, titlelist.get(i));
                m.put(ATTRIBUTE_NAME_IMAGE, colors[i%5]);
                m.put(ATTRIBUTE_NAME_DATETEXT, datelist.get(i));
                data.add(m);
                lastLoaded++;
            }
                Log.i("Loading news from", "0 to "+Integer.toString(lastLoaded-1));
            String[] from = {ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE, ATTRIBUTE_NAME_DATETEXT};
            // массив ID View-компонентов, в которые будут вставлять данные
            int[] to = {R.id.tvText, R.id.ivImg, R.id.tvDate};
            sAdapter = new SimpleAdapter(this, data, R.layout.item, from, to);


            setListAdapter(sAdapter);
            sAdapter.notifyDataSetChanged();// Ставим уведомлялку изменения контента на адаптер
            isFirstTime = false;
            getListView().setOnScrollListener(scrollListener); //Cвязываем наш список с ScrollListener(он создан ниже)
        }

    }
    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){

        if (dialog2!=null)
            dialog2.dismiss();
        super.onResume();
    }
    public class siteParser extends AsyncTask<String, Void, String> //непосредственно сам парсер
    {
        @Override
        protected String doInBackground(String... arg)
        {
            num = 0;
            k = 0;
            Log.i("Parsing", "started");
            Document Page;
            titlelist.clear();
            datelist.clear();
            referencelist.clear();
            String filename = "TitlesList";
            File file = new File(getFilesDir(), filename);
            String file_ref_name = "NewsList";
            File file_ref = new File(getFilesDir(), file_ref_name);
            //if (!(file.exists()&&file.length()!=0&&file_ref.exists()&&file_ref.length()!=0)) {
            if ((!file.exists()||file.length()==0)&&(!file_ref.exists()||file_ref.length()==0)){
                try {
                    Log.i("Parsing", "started");
                    file.createNewFile();
                    file_ref.createNewFile();
                    BufferedWriter output = new BufferedWriter(new FileWriter(file));
                    BufferedWriter outref = new BufferedWriter(new FileWriter(file_ref));
                    String site = "http://lmi-school.ru/?p=2";
                    Page = Jsoup.connect(site).get();
                    title = Page.select(".titlediv");
                    references = Page.select(".text [align=right] a"); //ключевая строка, где мы вынимаем ссылки
                    for (Element titles : title) {
                        titlelist.add(titles.text().substring(13));
                        datelist.add(titles.text().substring(0, 10));
                        output.write(titles.text() + "\n");
                        k++;
                    }
                    for (Element referens : references) {
                        referencelist.add(referens.attr("href").substring(0)); //пихаем ссылки в массив
                        num++;
                        Log.i("Num", "processing, num="+Integer.toString(num)+referens.attr("href"));
                        outref.write(referens.attr("href")+"\n");//и заодно пишем файл
                    }
                    output.close(); //Закрываем файлы
                    outref.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    BufferedReader input = new BufferedReader(new FileReader(file));
                    BufferedReader inref = new BufferedReader((new FileReader(file_ref)));
                    String line;
                    while ((line = input.readLine()) != null) {
                        titlelist.add(line.substring(13));
                        datelist.add(line.substring(0, 10));
                        k++;
                        Log.i("Parsing", "k="+Integer.toString(k));
                    }
                    String line2;
                    while ((line2 = inref.readLine()) != null) {
                        referencelist.add(line2);
                        num++;
                        Log.i("Num", "processing, num ="+Integer.toString(num)+line2);
                    }
                    Log.i("Parsing", "finished, t ="+Integer.toString(lastLoaded));
                    input.close();
                    inref.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            isCreated = true;
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            showResult();
            dialog.dismiss();
            refreshLayout.setRefreshing(false);
            super.onPostExecute(s);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.news, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) { //position - позиция тыкаемого велосипеда
        if (referencelist.size() != 0) {
            Intent intent = new Intent(News.this, CurrentNews.class);
            dialog2 = ProgressDialog.show(News.this, "Загрузка", "Подождите, новость загружается...");

            intent.putExtra("reference", referencelist.get(position)); //передаем в другую активность кусочек ссылки
            startActivity(intent);//запускаем активность
        }
        super.onListItemClick(l, v, position, id);

    }
    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() { //Создаем обработчик прокрутки
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

        }

        @Override
        public void onScroll(AbsListView absListView, int first, int i2, int total) { //AbsListView absListView - список,
            //first - первый видимый элемент,  i2 - количество видимых элементов, total - всего элементов в списке
            Log.i("Первый элемент в списке", Integer.toString(first));
            if ((first == total - i2 && total < k) || total==0) {
                for (int i = lastLoaded; i < k && i < lastLoaded+newsLoadCount; i++){ //пихаем новую партию новостей в массив
                    Map<String,Object> m;
                    m = new HashMap<String, Object>();
                    m.put(ATTRIBUTE_NAME_TEXT, titlelist.get(i));
                    m.put(ATTRIBUTE_NAME_IMAGE,colors[i%5]);
                    m.put(ATTRIBUTE_NAME_DATETEXT, datelist.get(i));
                    data.add(m);
                }
                Log.i("Loading news from", Integer.toString(lastLoaded)+" to "+Integer.toString(lastLoaded+newsLoadCount-1));
                lastLoaded += newsLoadCount; // обновляем номер последней загруженной новости
                sAdapter.notifyDataSetChanged(); //Ставим уведомитель x2
            }
        }
    };
}