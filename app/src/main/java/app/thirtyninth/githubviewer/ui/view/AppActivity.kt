package app.thirtyninth.githubviewer.ui.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import app.thirtyninth.githubviewer.ProtoSettings
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.common.Constants
import app.thirtyninth.githubviewer.preferences.UserPreferences
import dagger.hilt.android.AndroidEntryPoint


private val Context.dataStore : DataStore<ProtoSettings> by dataStore(
    fileName = Constants.PREFERENCES_NAME,
    serializer = UserPreferences.PreferencesSerializer
)

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {
    private var isLoggedIn: Boolean? = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        setContentView(R.layout.activity_app)

/*
        val upref = UserPreferences(this.dataStore)
/*
        if (isLoggedIn == null) {
            lifecycleScope.launchWhenStarted {

            }
        }*/

        lifecycleScope.launchWhenStarted {
            upref.getLoggedInState().onEach {
                if (it) {
                    Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
                } else {
                    upref.saveUser(User("exndy", "token"))
                }
            }.collect()
        }*/

    }
}