package app.thirtyninth.githubviewer.preferences

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    var isLoggedIn: Boolean by BooleanPreference(sharedPreferences, "isLoggedIn")
    var authToken: String? by StringPreference(sharedPreferences, "authToken")

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}