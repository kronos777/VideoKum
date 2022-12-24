package com.example.videokum;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {


    ArrayList<String> allLocalFiles = new ArrayList<String>();
    public boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_splash);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);



       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 5*1000);
*/

        //проверяем есть ли соединение
        isConnected = hasConnection(SplashActivity.this);
        //смотрим все файлы на локальном носителе
        getAllFilesMovies();




        if(allLocalFiles.size() > 0 ) {

            if (isConnected) {
                getWebsite(15000);
                Toast.makeText(SplashActivity.this, "запускаем видео после синхронизации" + isConnected, Toast.LENGTH_SHORT).show();
            }

        } else {
            if (!isConnected) {
                Toast.makeText(SplashActivity.this, "нет подключения к интернету", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SplashActivity.this, "файлов в директории нет" + String.valueOf(allLocalFiles.size()), Toast.LENGTH_SHORT).show();
                //Log.i("no file loc dir:", String.valueOf(allLocalFiles.size()));
                getWebsite(25000);
            }
        }


    }




    private boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    private synchronized ArrayList<String> getAllFilesMovies() {

        File sdCardRoot = Environment.getExternalStorageDirectory();
        File videoDir = new File(sdCardRoot, "Movies");
        if(videoDir.exists()) {
            for (File f : Objects.requireNonNull(videoDir.listFiles())) {
                if (f.isFile())
                {
                    String name = f.getName();
                    Log.i("file names", name);
                    allLocalFiles.add(f.getName());
                }

            }
        } else {
            videoDir.mkdirs();
        }

        return allLocalFiles;
    }

    private synchronized void getWebsite(int sleepTime) {
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("http://iziboro0.beget.tech/kummedia/orehovo").get();
                    //String title = doc.title();
                    //Elements title = doc.select("h1");
                    Elements links = doc.select("li");
                    //Elements links = doc.select("a[href]");

                    //builder.append(title).append("\n");
                    ArrayList<String> mExampleList = new ArrayList<String>();
                    //getAllFilesMovies();
                    for (Element link : links) {
                        String[] namef = link.text().split("/");
                        String lnamef = namef[namef.length-1];
                        mExampleList.add(lnamef);
                        //Log.i("name filename:", lnamef);
                        //Log.i("Raw filename:", link.text());
                        //сравниваем в цикле каждое имя имя файла с каждым
                        if (allLocalFiles.contains(lnamef)) {
                            Log.i("exists file in loc st:", link.text());
                        } else {
                            downLoadFile(link.text());
                            Log.i("no file in loc st:", link.text());
                        }


                    }

                    if(allLocalFiles.size() > 0) {
                        for (int i = 0; i < allLocalFiles.size(); i++) {
                            String locFilesName = allLocalFiles.get(i);
                            if (mExampleList.contains(locFilesName)) {
                                Log.d("this file exists ser", locFilesName);
                                //Toast.makeText(MainActivity.this, "файл присутствует на сервере", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("this file no exists ser", locFilesName);
                                // Toast.makeText(MainActivity.this, "файл отсутствует на сервере", Toast.LENGTH_SHORT).show();
                                deleteFileInDevice(locFilesName);
                            }

                        }
                    } else {
                        getAllFilesMovies();
                    }
                    /**/

                    //down(mExampleList.get(0));


                    //   System.out.println(names);
                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                    // Log.i("Raw filename:", "error query");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        result.setText(builder.toString());
                        //    setNames(mExampleList);
                        // loadingFinished();
                        launchMainActivity(sleepTime);
                        // Thread.sleep(sleepTime);
                        // Thread mainThread = Thread.currentThread();
                        //Toast.makeText(SplashActivity.this, "Все видео загружены. " + allLocalFiles.toString(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(MainActivity.this, "Все видео загружены. " + mainThread.getId(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(MainActivity.this, "Все видео загружены. " + mainThread.getName(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).start();
    }

    private void launchMainActivity(int time){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, time);
    }

    private void deleteFileInDevice(String nameFile) {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "Movies");
        for (File f : yourDir.listFiles()) {
            if (f.isFile()) {
                String name = f.getName();
                if(name.equals(nameFile)) {
                    f.delete();
                    Log.i("has been delete", name);
                }
            }
        }
    }

    private synchronized void downLoadFile(String url) {
        String getUrl = url;
        //VideoListKum
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(getUrl));
        String title = URLUtil.guessFileName(getUrl, null, null);
        request.setTitle(title);
        request.setDescription("downloading file pleade wait");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, title);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Log.i("file names download", title);
        // Toast.makeText(MainActivity.this, "dowload started", Toast.LENGTH_SHORT).show();
    }


}