package com.application

import android.app.Application
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SCApplication : Application() {

    companion object {
        const val VI_LANG_TAG = "vi-VN"
//        const val EN_LANG_TAG = "en-US"

        @MainThread
        fun changeLanguage(languageTag: String) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
        }
    }

}