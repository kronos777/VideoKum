package com.example.videokum;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ProgressBar;
import android.widget.Toast;


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

    public long downloadID;
    ArrayList<String> newLnkList = new ArrayList<>();
    int identifierDownload = 1;

    ProgressBar progressBar;

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                //Toast.makeText(SplashActivity.this, "Загрузка завершена " + itendifierDownload, Toast.LENGTH_SHORT).show();
                if(newLnkList.size() == 1) {
                    stopDownloadingProcess("Приятного просмотра.");
                } else if (newLnkList.size() > 1) {
                    if(newLnkList.size()-1 == identifierDownload) {
                        stopDownloadingProcess("Приятного просмотра.");
                    } else {
                        //Log.d("currentDownloadFile", newLnkList.get(identifierDownload).toString());
                        downLoadFile(newLnkList.get(identifierDownload));
                        ++identifierDownload;
                    }

                }
            }

        }
    };


    @SuppressLint({"SourceLockedOrientationActivity", "UnspecifiedRegisterReceiverFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_splash);

       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

       progressBar = (ProgressBar) findViewById(R.id.progressBar);

       //check internet connected
       isConnected = hasConnection(SplashActivity.this);
       //see all local files
       getAllFilesMovies();
       setModeExecutable(isConnected);
       registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }


    private void setModeExecutable(boolean isConnected) {
        if(allLocalFiles.size() > 0 ) {

            if (isConnected) {
                getWebsite();
                // Toast.makeText(SplashActivity.this, "запускаем видео после синхронизации" + isConnected, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SplashActivity.this, "интернета нет, но файлы в директории есть" + allLocalFiles.size(), Toast.LENGTH_SHORT).show();
                launchMainActivity(30000);
            }

        } else {
            if (!isConnected) {
                Toast.makeText(SplashActivity.this, "файлов в директории нет и интернета тоже нет." + allLocalFiles.size(), Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(SplashActivity.this, "файлов в директории нет, а подключение есть" + allLocalFiles.size(), Toast.LENGTH_SHORT).show();
                getWebsite();
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

    private void getAllFilesMovies() {

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

    private void getWebsite() {
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
                      // downLoadFile(link.text());
                        newLnkList.add(link.text());
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

                if(newLnkList.size() > 0){
                    runOnUiThread(() -> startDownloadingProcess("Начался процесс синхронизации с сервером, пожалуйста подождите."));
                } else {
                    //go see movie
                    runOnUiThread(() -> goNextActivity("Новых файлов на сервере нет. Приятного просмотра."));
                }


            } catch (IOException e) {
                builder.append("Error : ").append(e.getMessage()).append("\n");
            }

           // runOnUiThread(() -> launchMainActivity(sleepTime));
        }).start();
    }

    private void startDownloadingProcess(String message) {
        downLoadFile(newLnkList.get(0));
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Toast.makeText(SplashActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void stopDownloadingProcess(String message) {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        Toast.makeText(SplashActivity.this, message, Toast.LENGTH_LONG).show();
        launchMainActivity(0);
    }

    private void goNextActivity(String message) {
        Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
        launchMainActivity(0);
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

    private void downLoadFile(String url) {
        //VideoListKum
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String title = URLUtil.guessFileName(url, null, null);
        request.setTitle(title);
        request.setDescription("downloading file please wait");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, title);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);
        Log.i("file names download", title);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }


}