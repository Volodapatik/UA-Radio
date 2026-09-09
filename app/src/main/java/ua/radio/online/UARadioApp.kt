package ua.radio.online

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class UARadioApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "UA Radio",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Керування відтворенням радіо"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "ua_radio_playback"
    }
}
