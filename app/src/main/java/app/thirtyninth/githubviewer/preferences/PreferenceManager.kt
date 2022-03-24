package app.thirtyninth.githubviewer.preferences

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    private val prefEditor = sharedPreferences.edit()

    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(IS_LOGGED_IN_BOOLEAN, false)
        set(value) {
            prefEditor.putBoolean(IS_LOGGED_IN_BOOLEAN, value)
            prefEditor.commit()
        }

    var username: String
        get() = sharedPreferences.getString(USERNAME, "") ?: ""
        set(value) {
            prefEditor.putString(USERNAME, value)
            prefEditor.commit()
        }

    var authToken: String
        get() = sharedPreferences.getString(AUTH_TOKEN, "") ?: ""
        set(value) {
            prefEditor.putString(AUTH_TOKEN, value)
            prefEditor.commit()
        }

    companion object {
        const val IS_LOGGED_IN_BOOLEAN = "isLoggedIn"
        const val USERNAME = "username"
        const val AUTH_TOKEN = "authToken"
    }
}