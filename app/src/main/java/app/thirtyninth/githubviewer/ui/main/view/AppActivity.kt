package app.thirtyninth.githubviewer.ui.main.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.ui.NetworkConnectionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NetworkConnectionManager(this).startNetworkCallback()

        installSplashScreen()

        setContentView(R.layout.activity_app)
    }

    override fun onPause() {
        super.onPause()
        NetworkConnectionManager(this).stopNetworkCallback()
    }
}