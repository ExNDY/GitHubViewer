package app.thirtyninth.githubviewer.preferences

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    private val prefEditor = sharedPreferences.edit()

    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(IS_LOGGED_IN_BOOLEAN, false)
        set(value) {
            prefEditor.putBoolean(IS_LOGGED_IN_BOOLEAN, value)
            prefEditor.commit()
        }

    var login: String
        get() = sharedPreferences.getString(LOGIN, "") ?: ""
        set(value) {
            prefEditor.putString(LOGIN, value)
            prefEditor.commit()
        }

    var authToken: String
        get() = sharedPreferences.getString(AUTH_TOKEN, "") ?: ""
        set(value) {
            prefEditor.putString(AUTH_TOKEN, value)
            prefEditor.commit()
        }

    companion object {
        private const val IS_LOGGED_IN_BOOLEAN = "isLoggedIn"
        private const val LOGIN = "login"
        private const val AUTH_TOKEN = "authToken"
    }
}