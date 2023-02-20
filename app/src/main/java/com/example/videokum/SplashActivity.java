package com.example.videokum;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {


    ArrayList<String> allLocalFiles = new ArrayList<>();
    public boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_splash);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        //проверяем есть ли соединение
        isConnected = hasConnection(SplashActivity.this);
        //смотрим все файлы на локальном носителе
        getAllFilesMovies();




        if(allLocalFiles.size() > 0 ) {

            if (isConnected) {
                getWebsite(20000);
                Toast.makeText(SplashActivity.this, "запускаем видео после синхронизации" + isConnected, Toast.LENGTH_SHORT).show();
            }

        } else {
            if (!isConnected) {
                Toast.makeText(SplashActivity.this, "нет подключения к интернету", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(SplashActivity.this, "файлов в директории нет" + allLocalFiles.size(), Toast.LENGTH_SHORT).show();
                getWebsite(35000);
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
        return wifiInfo != null && wifiInfo.isConnected();
    }

    private synchronized void getAllFilesMovies() {

        File sdCardRoot = Environment.getExternalStorageDirectory();
        File videoDir = new File(sdCardRoot, "Movies");

            for (File f : Objects.requireNonNull(videoDir.listFiles())) {
                if (f.isFile())
                {
                    String name = f.getName();
                    Log.i("file names", name);
                    allLocalFiles.add(f.getName());
                }

            }
    }

    private synchronized void getWebsite(int sleepTime) {
        //
        new Thread(() -> {
            final StringBuilder builder = new StringBuilder();

            try {
                Document doc = Jsoup.connect("http://iziboro0.beget.tech/kummedia/pposadstoyca/").get();
                Elements links = doc.select("li");
                ArrayList<String> mExampleList = new ArrayList<>();

                for (Element link : links) {
                    String[] stringSite = link.text().split("/");
                    String fileName = stringSite[stringSite.length-1];
                    mExampleList.add(fileName);
                    if (!allLocalFiles.contains(fileName)) {
                       downLoadFile(link.text());
                    }


                }

                if(allLocalFiles.size() > 0) {
                    for (int i = 0; i < allLocalFiles.size(); i++) {
                        String locFilesName = allLocalFiles.get(i);
                        if (!mExampleList.contains(locFilesName)) {
                           deleteFileInDevice(locFilesName);
                        }

                    }
                } else {
                    getAllFilesMovies();
                }


            } catch (IOException e) {
                builder.append("Error : ").append(e.getMessage()).append("\n");
            }

            runOnUiThread(() -> launchMainActivity(sleepTime));
        }).start();
    }

    private void launchMainActivity(int time){
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }, time);
    }

    private void deleteFileInDevice(String nameFile) {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "Movies");
        for (File f : Objects.requireNonNull(yourDir.listFiles())) {
            if (f.isFile()) {
                String name = f.getName();
                if(name.equals(nameFile)) {
                    f.delete();
                }
            }
        }
    }

    private synchronized void downLoadFile(String url) {
        //VideoListKum
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String title = URLUtil.guessFileName(url, null, null);
        request.setTitle(title);
        request.setDescription("downloading file please wait");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, title);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Log.i("file names download", title);
    }


}