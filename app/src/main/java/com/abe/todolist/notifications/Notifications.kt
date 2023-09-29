package com.abe.todolist.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.abe.todolist.R

const val NOTIFICATIONID = 1
const val CHANNELID = "channel1"
const val TITLEEXTRA = "TitleExtra"
const val MESSEAGEEXTRA = "MessageExtra"
class Notifications(): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notifications = context?.let {
            NotificationCompat.Builder(it, CHANNELID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(intent?.getStringExtra(TITLEEXTRA))
                .setContentText(intent?.getStringExtra(MESSEAGEEXTRA))
                .build()
        }
        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATIONID,notifications)
    }
}