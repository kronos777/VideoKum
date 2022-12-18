package com.example.videokum;


import static android.widget.Toast.makeText;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    VideoView vw;
    public ArrayList<String> names;
    ArrayList<String> filename = new ArrayList<>();
    ArrayList<Integer> videolist = new ArrayList<>();
    int currvideo = 0;
    TextView result;

    ArrayList<String> allLocalFiles = new ArrayList<String>();
    public boolean isConnected;
    public boolean isInstall;

    /*w f screen */
   /*
    private List<String> myList;
    Context context;
   w f screen */
    //private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vw = (VideoView)findViewById(R.id.vidvw);
        //vw = (VideoView)findViewById(R.id.vidvw);
        //vw.setMediaController(new MediaController(this));
        vw.setOnCompletionListener(this);


        //full screen option
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //filename.add("https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
        //filename.add("https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");

        /*
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/kumafil2");
        dir.mkdirs();
        Toast.makeText(MainActivity.this, "dowload started" + dir, Toast.LENGTH_SHORT).show();
        Log.v("LOG_TAG", "Dir: " + dir);*/


        //Log.v("LOG_TAG", "Dir: " + dir);

        //  down("http://iziboro0.beget.tech/kummedia/k1.mp4");
        //  setVideoCard(filename.get(0));


      //  getWebsite();
       // getAllFilesMovies();
       // deleteFileInDevice("k3.mp4");

        //проверяем наличие сети
        // смотрим если есть сеть, если что то чего нет на телефоне и качаем
        // смотрим какие есть на сервере и каких нет на сервере но есть на телефоне и удаляем их


     /**/
//        isInstall = isPackageInstalled("com.example.videokum", getPackageManager());
/*
        if (isAppExist()) {
            Toast.makeText(MainActivity.this, "isInstall yes", Toast.LENGTH_SHORT).show();}
        else {
            Toast.makeText(MainActivity.this, "isInstall no", Toast.LENGTH_SHORT).show();
        }*/


        isConnected = isOnline();
  //      Toast.makeText(MainActivity.this, "isInstall" + isInstall, Toast.LENGTH_SHORT).show();


        if (!isConnected) {
            //запускаем видео что уже есть
            playLocal();
        } else {
            //Toast.makeText(MainActivity.this, "connnection internet" + isConnected, Toast.LENGTH_SHORT).show();
            getAllFilesMovies();
         //   Toast.makeText(MainActivity.this, "name f file" + allLocalFiles.get(0), Toast.LENGTH_SHORT).show();
            getWebsite();
            //filename = getAllFilesMovies();
            playLocal();
            sendMail(allLocalFiles);
        }

        if (filename.size() > 0) {
            setVideoCard(filename.get(0));
        }


    //    sendMail(allLocalFiles);

        //String locFilesName = "k2.mp4";
        //deleteFileInDevice(locFilesName);

       // setVideoCard(filename.get(0));

      //  File sdCardRoot = Environment.DIRECTORY_MOVIES;
        //PeriodicWorkRequest myWorkRequest = new PeriodicWorkRequest.Builder(KumWorker.class, 20, TimeUnit.MINUTES).build();
        OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(KumWorker.class).build();
        WorkManager.getInstance().enqueue(myWorkRequest);

    }

    private synchronized boolean isAppExist() {

        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo("com.example.videokum", PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }


    private synchronized void playLocal() {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "Movies");
        for (File f : yourDir.listFiles()) {
            if (f.isFile())
            {
                String absolutePath = f.getAbsolutePath();
                //String name = f.getName();
                Log.i("local names", absolutePath);
                filename.add(absolutePath);
                //  f.delete();
            }

        }
    }

    protected synchronized boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }

   private synchronized void downLoadFile(String url) {
       String getUrl = url;
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

   private synchronized void sendMail(ArrayList<String> mess) {

        String mail = "creatorweb77@gmail.com";
        String message = "hi ivan, i am application kumavideo." + mess;
        String subject = "application kumavideo";

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);

        javaMailAPI.execute();

    }

   private synchronized ArrayList<String> getAllFilesMovies() {

       File sdCardRoot = Environment.getExternalStorageDirectory();
       File yourDir = new File(sdCardRoot, "Movies");
       for (File f : yourDir.listFiles()) {
           if (f.isFile())
           {
               String name = f.getName();
               Log.i("file names", name);
               allLocalFiles.add(f.getName());
               //  f.delete();
           }

       }
       return allLocalFiles;
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

    public void getWebsite() {
    //
           new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("http://iziboro0.beget.tech/kummedia/").get();
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


                    for (int i = 0; i < allLocalFiles.size(); i++) {
                        String locFilesName = allLocalFiles.get(i);
                        if (mExampleList.contains(locFilesName)) {
                            Log.i("this file exists ser", locFilesName);
                        } else {
                            Log.i("this file no exists ser", locFilesName);
                            deleteFileInDevice(locFilesName);
                        }

                    }/**/

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
                    }
                });
            }
            }).start();
    }


    public String getExt(String filePath){
        int strLength = filePath.lastIndexOf(".");
        if(strLength > 0)
            return filePath.substring(strLength + 1).toLowerCase();
        return null;
    }


    public ArrayList<String> listRaw() {
        ArrayList<String> nameFile = new ArrayList<String>();

        Field[] fields = R.raw.class.getFields();
      /*  Toast toast = Toast.makeText(getApplicationContext(),
                "count fields" + fields.length, Toast.LENGTH_SHORT);
        toast.show();*/
        for(int count=0; count < fields.length; count++){
            /*Toast.makeText(getApplicationContext(),
                    "count fields" + fields[count].getName()+"mp4", Toast.LENGTH_SHORT).show();*/
            nameFile.add(fields[count].getName());
            //videolist.add(R.raw.nameFile);
            Log.i("Raw Asset:", fields[count].getName());
        }
        return nameFile;
    }

    public void setVideo(int id)
    {
        String uriPath
                = "android.resource://"
                + getPackageName() + "/" + id;
        Uri uri = Uri.parse(uriPath);
        vw.setVideoURI(uri);
        vw.start();
    }

    public synchronized void setVideoCard(String id)
    {
        String uriPath = id;
        Uri uri = Uri.parse(id);
       //Uri uri = Uri.fromFile(new File(uriPath));
        // vw.setVideoURI(uri);
        vw.setVideoPath(id);
        vw.start();
    }

    public void onCompletion(MediaPlayer mediapalyer)
    {
      /*  AlertDialog.Builder obj = new AlertDialog.Builder(this);
        obj.setTitle("Воспроизведение завершено!");
        obj.setIcon(R.mipmap.ic_launcher);
        MyListener m = new MyListener();
        obj.setPositiveButton("Повторить", m);
        obj.setNegativeButton("Следующее", m);
        obj.setMessage("Воспроизвести слудующее видео?");
        obj.show();*/

        /*
        ++currvideo;
        if (currvideo == videolist.size())
            currvideo = 0;
        setVideo(videolist.get(currvideo));*/
        /* */
        ++currvideo;
        if (currvideo == filename.size())
            currvideo = 0;
        setVideoCard(filename.get(currvideo));

    }



    class MyListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which)
        {
            if (which == -1) {
                vw.seekTo(0);
                vw.start();
            }
            else {
                /*
                ++currvideo;
                if (currvideo == videolist.size())
                    currvideo = 0;
                setVideo(videolist.get(currvideo));*/
                ++currvideo;
                if (currvideo == filename.size())
                    currvideo = 0;
                setVideoCard(filename.get(currvideo));
            }
        }
    }



}