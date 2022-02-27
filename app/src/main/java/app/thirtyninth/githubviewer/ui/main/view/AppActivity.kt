package app.thirtyninth.githubviewer.ui.main.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.ui.NetworkConnectionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NetworkConnectionManager(this).startNetworkCallback()

        installSplashScreen()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        setContentView(R.layout.activity_app)

        setupActionBarWithNavController(
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)as NavHostFragment).navController,
            AppBarConfiguration(setOf(R.id.repositoriesFragment, R.id.loginFragment))
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onPause() {
        super.onPause()
        NetworkConnectionManager(this).stopNetworkCallback()
    }

}