package app.thirtyninth.githubviewer.ui.main.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.preferences.KeyValueStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {
    @Inject
    lateinit var storage: KeyValueStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_app)

        if (savedInstanceState == null) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
            val navController = navHostFragment.navController
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

            val destinationId = if (storage.isLoggedIn()) {
                R.id.repositoriesFragment
            } else {
                R.id.authFragment
            }

            navGraph.setStartDestination(destinationId)
            navController.graph = navGraph
        }
    }
}