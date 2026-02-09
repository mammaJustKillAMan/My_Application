package com.example.myapplication.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.MainActivity

/**
 * Helper class for creating and showing notifications related to altitude risk.
 *
 * This class manages a notification channel for high and moderate altitude risk alerts
 * and provides a method to display notifications that open the main app activity when tapped.
 *
 * @property context Application context used to access system services and create notifications.
 */
class NotificationHelper(private val context: Context) {

    private val CHANNEL_ID = "altitude_risk_channel"
    private val NOTIFICATION_ID = 1001

    init {
        createNotificationChannel()
    }

    /**
     * Creates a notification channel for altitude risk alerts.
     *
     * This is required for Android O (API 26) and above. The channel is
     * configured with high importance so that notifications appear prominently.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Altitude Risk Alerts"
            val descriptionText = "Notifications for High and Moderate Altitude Risk"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Displays a notification with the given title and message.
     *
     * The notification will open [MainActivity] when tapped and will automatically
     * disappear once the user interacts with it.
     *
     * @param title The title text to show in the notification.
     * @param message The body text to show in the notification.
     */
    fun showRiskNotification(title: String, message: String) {
        // Intent to open the app when notification is clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning) // or R.drawable.ic_warning
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } catch (e: SecurityException) {
            // Permission not granted, handle gracefully
            e.printStackTrace()
        }
    }
}