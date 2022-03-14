package app.thirtyninth.githubviewer.data.repository

import androidx.datastore.core.DataStore
import app.thirtyninth.githubviewer.ProtoSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class KeyValueStorage @Inject constructor(
    dataStore: DataStore<ProtoSettings>
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val tokenStateFlow: StateFlow<String> = dataStore.data.map {
        it.userToken
    }.stateIn(scope, SharingStarted.Eagerly, "")
}