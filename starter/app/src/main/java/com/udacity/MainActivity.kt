package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var urlSelected: String = ""
    private var fileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if (urlSelected.isEmpty()) {
                Toast.makeText(this, getString(R.string.select_download), Toast.LENGTH_SHORT).show()
            } else {
                custom_button.buttonState = ButtonState.Loading
                download()
            }

        }
        createChannel(getString(R.string.channelId), getString(R.string.chanel_tile))

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                custom_button.buttonState = ButtonState.Completed
                val status = downloadManager.downloadStatus(downloadID);
                val notificationManager = ContextCompat.getSystemService(
                        context,
                        NotificationManager::class.java
                ) as NotificationManager
                notificationManager.sendNotification(fileName, status, context)
            }
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.radio_glide ->
                    if (checked) {
                        fileName = getString(R.string.glide_name)
                        urlSelected = URL_GLIDE
                    }
                R.id.radio_load_app ->
                    if (checked) {
                        fileName = getString(R.string.load_app)
                        urlSelected = URL_APP
                    }
                R.id.radio_retrofit ->
                    if (checked) {
                        fileName = getString(R.string.retrofit)
                        urlSelected = URL_RETROFIT
                    }
            }
        }
    }

    private fun download() {
        val request =
                DownloadManager.Request(Uri.parse(urlSelected))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download Status"
            val notificationManager = this.getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


    fun DownloadManager.downloadStatus(downloadId: Long): Int {
        val query = DownloadManager.Query()
                .setFilterById(downloadId)
        var status = DownloadManager.STATUS_FAILED
        val cursor = query(query);
        if (cursor.moveToFirst()) {
            status = cursor.getInt(
                    cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            );
        }
        return status
    }

    companion object {
        private const val URL_APP =
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE = "https://github.com/bumptech/glide"
        private const val URL_RETROFIT = "https://github.com/square/retrofit"
    }

}
