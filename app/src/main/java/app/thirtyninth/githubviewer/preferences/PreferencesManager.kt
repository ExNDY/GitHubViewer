package app.thirtyninth.githubviewer.preferences

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    sharedPreferences: SharedPreferences
) {
    val isLoggedIn: Boolean by BooleanPreference(sharedPreferences, IS_LOGGED_IN_BOOLEAN)
    val login: String? by StringPreference(sharedPreferences, LOGIN)
    val authToken: String? by StringPreference(sharedPreferences, AUTH_TOKEN)


    companion object {
        private const val IS_LOGGED_IN_BOOLEAN = "isLoggedIn"
        private const val LOGIN = "login"
        private const val AUTH_TOKEN = "authToken"
    }
}