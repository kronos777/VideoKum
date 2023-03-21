package com.example.videokum;


import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;


import java.io.File;
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
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        //keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        playLocal();
        if (filename.size() > 0) {
            setVideoCard(filename.get(0));
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
      //  Toast.makeText(MainActivity.this, "onResume", Toast.LENGTH_SHORT).show();
        playLocal();
        if (filename.size() > 0) {
            setVideoCard(filename.get(0));
        }
    }

    private synchronized void playLocal() {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "Movies");
        for (File f : Objects.requireNonNull(yourDir.listFiles())) {
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
           for (File f : Objects.requireNonNull(yourDir.listFiles())) {
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





}
