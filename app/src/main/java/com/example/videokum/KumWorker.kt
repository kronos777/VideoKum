package com.example.videokum

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import android.text.Html
import android.util.Log
import android.webkit.URLUtil
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.lang.Thread.sleep
import java.util.*
import java.util.regex.Pattern

class KumWorker(private val mContext: Context, workerParameters: WorkerParameters) : Worker(mContext, workerParameters){

    var allLocalFiles = ArrayList<String>()

    override fun doWork(): Result {
        getAllFilesMovies()
        getWebsite()
        return Result.success()
    }

    fun getWebsite() {
        //
      //  Thread {
            val builder = StringBuilder()
            try {
                val doc = Jsoup.connect("http://iziboro0.beget.tech/kummedia/").get()
                //String title = doc.title();
                //Elements title = doc.select("h1");
                val links = doc.select("li")
                val emArrayList = links.toArray()
                //Elements links = doc.select("a[href]");
               // sleep(2000)
                //builder.append(title).append("\n");
                val mExampleList = ArrayList<String>()
                //getAllFilesMovies();
                val threadId = Thread.currentThread().id
                Log.d("arraylistsize", threadId.toString() + emArrayList.size.toString())
                for (link in emArrayList) {
                    Log.d("sitefilename", Html.fromHtml(link.toString()).toString())
                    val strValue = Html.fromHtml(link.toString()).toString()
                    val delim = "/"
                    val arr = Pattern.compile(delim).split(strValue)
                    val fileNameServer = arr.get(arr.size - 1)
                    Log.d("serverfilename", fileNameServer)
                    /*
                    val lnamef = namef[namef.size - 1]*/
                    mExampleList.add(fileNameServer)
                    //Log.i("name filename:", lnamef);
                    //Log.i("Raw filename:", link.text());
                    //сравниваем в цикле каждое имя имя файла с каждым
                    if (allLocalFiles.contains(fileNameServer)) {
                        Log.i("exists file in loc st:", link.toString())
                    } else {
                        downLoadFile(link.toString())
                        Log.i("no file in loc st:", link.toString())
                    }
                }
                for (item in allLocalFiles) {
                    val locFilesName: String = item
                    if (mExampleList.contains(locFilesName)) {
                        Log.i("this file exists ser", locFilesName)
                    } else {
                        Log.i("this file no exists ser", locFilesName)
                        deleteFileInDevice(locFilesName)
                    }
                } /**/

                //down(mExampleList.get(0));


                //   System.out.println(names);
            } catch (e: IOException) {
                builder.append("Error : ").append(e.message).append("\n")
                // Log.i("Raw filename:", "error query");
            }
         /*   runOnUiThread(Runnable {
                // result.setText(builder.toString());
                //    setNames(mExampleList);
            })*/
      //  }.start()
    }

    private fun getAllFilesMovies(): ArrayList<String> {
        val sdCardRoot = Environment.getExternalStorageDirectory()
        val yourDir = File(sdCardRoot, "Movies")
        for (f in yourDir.listFiles()) {
            if (f.isFile) {
                val name = f.name
                Log.i("file names", name)
                allLocalFiles.add(f.name)
                //  f.delete();
            }
        }
        return allLocalFiles
    }


    private fun deleteFileInDevice(nameFile: String) {
        val sdCardRoot = Environment.getExternalStorageDirectory()
        val yourDir = File(sdCardRoot, "Movies")
        for (f in yourDir.listFiles()) {
            if (f.isFile) {
                val name = f.name
                if (name == nameFile) {
                    f.delete()
                    Log.i("has been delete", name)
                }
            }
        }
    }

    private fun downLoadFile(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        val title = URLUtil.guessFileName(url, null, null)
        request.setTitle(title)
        request.setDescription("downloading file pleade wait")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, title)
        val downloadManager = mContext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        Log.i("file names download", title)
        // Toast.makeText(MainActivity.this, "dowload started", Toast.LENGTH_SHORT).show();
    }


}