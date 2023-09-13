package com.nxg.im.chat.component.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nxg.im.chat.R
import com.nxg.mvvm.logger.SimpleLogger

object NotificationService : SimpleLogger {

    private fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String,
        channelDesc: String
    ) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDesc
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 通知聊天消息
     */
    fun notifyChatMessage(
        context: Context,
        notificationId: Int,
        title: String, text: String
    ) {
        logger.debug { "notifyChatMessage: title $title, text $text" }
        val channelId = "1"
        val channelName = "chat_message"
        val channelDesc = "chat message notify"
        createNotificationChannel(context, channelId, channelName, channelDesc)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_jetchat)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                logger.debug { "notifyChatMessage failed! no permission" }
                return
            }
            notify(notificationId, builder.build())
        }
    }

    /**
     * 通知视频通话
     */
    fun notifyChatVideo(
        context: Context,
        notificationId: Int,
        title: String, text: String
    ) {
        val channelId = "2"
        val channelName = "chat_video_call"
        val channelDesc = "chat video call notify"
        createNotificationChannel(context, channelId, channelName, channelDesc)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_jetchat)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Much longer text that cannot fit one line...")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                logger.debug { "no POST_NOTIFICATIONS permission" }
                return
            }
            notify(notificationId, builder.build())
        }
    }

}