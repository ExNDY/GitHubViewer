package app.thirtyninth.githubviewer.di

import android.app.Application
import app.thirtyninth.githubviewer.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class HiltApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        //TODO разобраться с вариантами билда
//        if (BuildConfig.DEBUG){
//
//        }
        Timber.plant(Timber.DebugTree())
    }
}