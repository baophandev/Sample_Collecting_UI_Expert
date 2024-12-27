package com.application

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.application.SCApplication.Companion.VI_LANG_TAG
import com.application.SCApplication.Companion.changeLanguage
import com.application.ui.navigation.AppNavigationGraph
import com.application.ui.theme.SampleCollectingApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.HttpClient
import okhttp3.internal.closeQuietly
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var httpClient: HttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeLanguage(VI_LANG_TAG)
        if (!hasRequiredPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 0)
        }

        createNotificationChannels()

        setContent {
            SampleCollectingApplicationTheme(dynamicColor = false) {
                Surface {
                    AppEntryPoint()
                }
            }
        }
    }

    override fun onDestroy() {
        if (::httpClient.isInitialized) {
            httpClient.closeQuietly()
            Log.d(TAG, "HttpClient closed")
        }
        super.onDestroy()
    }

    private fun createNotificationChannels() {
        // Create the NotificationChannel.
        val chatChannel = createChatNotificationChannel()

        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(chatChannel)
    }

    private fun createChatNotificationChannel(): NotificationChannel {
        val name = getString(R.string.notify_chat_channel)
        val descriptionText = getString(R.string.notify_chat_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(NOTIFY_CHAT_CHANNEL_ID, name, importance)
        mChannel.description = descriptionText

        return mChannel
    }

    private fun hasRequiredPermission(): Boolean {
        return PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        val PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        const val TAG = "MainActivity"

        const val NOTIFY_CHAT_CHANNEL_ID = "CHAT_CHANNEL"
    }
}

@Composable
fun AppEntryPoint() {
    AppNavigationGraph()
}

