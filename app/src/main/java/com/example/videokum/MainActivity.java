package com.example.videokum;


import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Toast;
import android.widget.VideoView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    VideoView vw;
    ArrayList<String> filename = new ArrayList<>();
    int currentVideo = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vw = (VideoView)findViewById(R.id.vidvw);
        vw.setOnCompletionListener(this);


        //full screen option
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        playLocal();
        if (filename.size() > 0) {
            setVideoCard(filename.get(0));
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(MainActivity.this, "onResume", Toast.LENGTH_SHORT).show();
    }

    private synchronized void playLocal() {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "Movies");
        for (File f : yourDir.listFiles()) {
            if (f.isFile())
            {
                String absolutePath = f.getAbsolutePath();
                if(absolutePath.split("-").length > 1) {
                    String[] arrnNames = absolutePath.split("/");
                    String nameFileDefice = arrnNames[arrnNames.length-1];
                   // Toast.makeText(MainActivity.this, "файл содержит дефис" + String.valueOf(nameFileDefice), Toast.LENGTH_SHORT).show();
                    deleteFileInDevice(nameFileDefice);
                }


                filename.add(absolutePath);
            }

        }
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

    public synchronized void setVideoCard(String id)
    {
        vw.setVideoPath(id);
        vw.start();
    }

    public void onCompletion(MediaPlayer mediapalyer)
    {
        ++currentVideo;
        if (currentVideo == filename.size())
            currentVideo = 0;
        setVideoCard(filename.get(currentVideo));

    }



    class MyListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which)
        {
            if (which == -1) {
                vw.seekTo(0);
                vw.start();
            }
            else {
                ++currentVideo;
                if (currentVideo == filename.size())
                    currentVideo = 0;
                setVideoCard(filename.get(currentVideo));
            }
        }
    }



}
