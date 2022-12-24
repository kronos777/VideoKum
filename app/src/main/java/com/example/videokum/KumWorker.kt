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

    override fun doWork(): Result {
        //checkWebsite()
        return Result.success()
    }

    //fun checkWebsite() {  }

}