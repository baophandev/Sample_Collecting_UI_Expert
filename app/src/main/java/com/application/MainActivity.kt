package com.application

import android.content.pm.PackageManager
import android.os.Bundle
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

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeLanguage(VI_LANG_TAG)
        if (!hasRequiredPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 0)
        }
        setContent {
            SampleCollectingApplicationTheme(dynamicColor = false) {
                Surface {
                    AppEntryPoint()
                }
            }
        }
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
    }
}

@Composable
fun AppEntryPoint() {
    AppNavigationGraph()
}

