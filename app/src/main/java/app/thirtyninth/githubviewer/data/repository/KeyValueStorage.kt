package app.thirtyninth.githubviewer.data.repository

import androidx.datastore.core.DataStore
import app.thirtyninth.githubviewer.ProtoSettings
import app.thirtyninth.githubviewer.preferences.UserPreferences
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class KeyValueStorage @Inject constructor(
    userPreferences: UserPreferences
) {
    val tokenStateFlow: StateFlow<String?> = userPreferences.saved.map{
        it.userToken
    }.stateIn(MainScope(), SharingStarted.Eagerly, null)
    /*
    val tokenStateFlow: StateFlow<String?> = dataStore.data.map {
        it.userToken
    }.stateIn(MainScope(), SharingStarted.Eagerly, null)*/
}