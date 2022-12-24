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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    VideoView vw;
    public ArrayList<String> names;
    ArrayList<String> filename = new ArrayList<>();
    ArrayList<Integer> videolist = new ArrayList<>();
    int currvideo = 0;

    ArrayList<String> allLocalFiles = new ArrayList<String>();
    public boolean isConnected;


   // public static String DIRECTORY_KUM = "VideoListKum";
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


        //isConnected = isOnline();
        isConnected = hasConnection(MainActivity.this);
  //      Toast.makeText(MainActivity.this, "isInstall" + isInstall, Toast.LENGTH_SHORT).show();
        getAllFilesMovies();

        if(allLocalFiles.size() > 0 ) {

            if (!isConnected) {
                //запускаем видео что уже есть
                playLocal();
                Toast.makeText(MainActivity.this, "запускаем видео что уже есть" + isConnected, Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(MainActivity.this, "connnection internet" + isConnected, Toast.LENGTH_SHORT).show();
                getAllFilesMovies();
                //   Toast.makeText(MainActivity.this, "name f file" + allLocalFiles.get(0), Toast.LENGTH_SHORT).show();
                getWebsite(15000);
                Toast.makeText(MainActivity.this, "запускаем видео после синхронизации" + isConnected, Toast.LENGTH_SHORT).show();
                //filename = getAllFilesMovies();
                //playLocal();
                //sendMail(allLocalFiles);
            }

            if (filename.size() > 0) {
                setVideoCard(filename.get(0));
            }


        } else {
            if (!isConnected) {
                Toast.makeText(MainActivity.this, "нет подключения к интернету", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "файлов в директории нет" + String.valueOf(allLocalFiles.size()), Toast.LENGTH_SHORT).show();
                Log.i("no file loc dir:", String.valueOf(allLocalFiles.size()));
                getWebsite(25000);
            }
        }



    //    sendMail(allLocalFiles);

        //String locFilesName = "k2.mp4";
        //deleteFileInDevice(locFilesName);

       // setVideoCard(filename.get(0));

      //  File sdCardRoot = Environment.DIRECTORY_MOVIES;
        /*PeriodicWorkRequest myWorkRequest = new PeriodicWorkRequest.Builder(KumWorker.class, 20, TimeUnit.MINUTES, 15, TimeUnit.MINUTES).build();
        // OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(KumWorker.class).build();
        //WorkManager.getInstance().enqueue(myWorkRequest);
        WorkManager.getInstance().enqueueUniquePeriodicWork(
                "paymentWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                myWorkRequest
        );*/
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
                if(absolutePath.split("-").length > 1) {
                    String[] arrnNames = absolutePath.split("/");
                    String nameFileDefice = arrnNames[arrnNames.length-1];
                    Toast.makeText(MainActivity.this, "файл содержит дефис" + String.valueOf(nameFileDefice), Toast.LENGTH_SHORT).show();
                    deleteFileInDevice(nameFileDefice);
                }
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

    public boolean hasConnection(final Context context)
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

    public synchronized void getWebsite(int sleepTime) {
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
                        try {
                            Thread.sleep(sleepTime);
                            playLocal();
                            if (filename.size() > 0) {
                                setVideoCard(filename.get(0));
                            }

                            Thread mainThread = Thread.currentThread();
                            Toast.makeText(MainActivity.this, "Все видео загружены. " + allLocalFiles.toString(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(MainActivity.this, "Все видео загружены. " + mainThread.getId(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(MainActivity.this, "Все видео загружены. " + mainThread.getName(), Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

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
