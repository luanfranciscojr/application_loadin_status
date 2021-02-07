package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

fun NotificationManager.sendNotification(fileName: String, status: Int, applicationContext: Context) {

    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    detailIntent.putExtra(applicationContext.getString(R.string.file_name_key), fileName)
    detailIntent.putExtra(applicationContext.getString(R.string.status_key), status)
    val detailPendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
            applicationContext,
            "channelId"
    ).setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(
                    applicationContext
                            .getString(R.string.notification_title)
            )
            .setContentText(applicationContext
                    .getString(R.string.notification_description))
            .setContentIntent(detailPendingIntent)
            .setAutoCancel(true).addAction(
                    R.drawable.ic_assistant_black_24dp,
                    applicationContext
                            .getString(R.string.notification_button),
                    detailPendingIntent
            ).setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(0, builder.build())

}
