package app.thirtyninth.githubviewer.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import app.thirtyninth.githubviewer.ProtoSettings
import app.thirtyninth.githubviewer.common.Constants
import app.thirtyninth.githubviewer.preferences.UserPreferences

object DataStoreUtil {
    val Context.dataStore : DataStore<ProtoSettings> by dataStore(
        fileName = Constants.PREFERENCES_NAME,
        serializer = UserPreferences.PreferencesSerializer
    )
}