package com.example.servicestest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class TimerService : Service() {

    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var startTime: Long = 0

    override fun onCreate() {
        super.onCreate()
        handler = Handler()
        startTime = System.currentTimeMillis()
        startTimer()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {

            startTime = System.currentTimeMillis() + intent.getLongExtra("startTime", 0)
        }

        return START_STICKY
    }

    override fun onDestroy() {

        stopTimer()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startTimer() {

        runnable = object : Runnable {

            override fun run() {

                val elapsedTime = startTime - System.currentTimeMillis()
                updateNotification(elapsedTime)
                handler!!.postDelayed(this, 1000)

                if (elapsedTime <= 0) {

                    val manager = getSystemService(
                        NotificationManager::class.java
                    )

                    manager.notify(NOTIFICATION_ID, notifyTimeEnd())
                    stopTimer()
                }
            }
        }

        handler!!.post(runnable as Runnable)
    }

    private fun notifyTimeEnd() : Notification {

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Timer Service")
            .setContentText("Timer is running..")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun stopTimer() {

        handler!!.removeCallbacks(runnable!!)
        stopForeground(true)
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification() : Notification {

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Timer Service")
            .setContentText("Timer is running..")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun updateNotification(millis: Long) {

        val manager = getSystemService(
            NotificationManager::class.java
        )

        manager.notify(NOTIFICATION_ID, buildNotification())
        Log.d("TimerService", "Elapsed time: " + formatTime(millis))
    }

    private fun formatTime(millis: Long) : String {

        val seconds = (millis/1000).toInt() % 60
        val minutes = (millis / (1000 * 60) % 60).toInt()
        val hours = (millis / (1000 * 60 * 60) % 24).toInt()

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    companion object{

        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "TimerChannel"
    }

}