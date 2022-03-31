package app.thirtyninth.githubviewer.preferences

import androidx.datastore.core.DataStore
import app.thirtyninth.githubviewer.ProtoSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyValueStorage @Inject constructor(
    private val dataStore: DataStore<ProtoSettings>,
    preferencesManager: PreferencesManager
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    val tokenStateFlow: StateFlow<String?> = dataStore.data.map {
        it.authToken
    }.stateIn(scope, SharingStarted.Eagerly, null)

    fun isLoggedIn(): Boolean {
        return !tokenStateFlow.value.isNullOrBlank()
    }

    suspend fun saveToken(authToken:String){
        dataStore.updateData {
            it.toBuilder()
                .setAuthToken(authToken)
                .build()
        }
    }

    suspend fun logout(){
        dataStore.updateData {
            it.toBuilder()
                .clear()
                .build()
        }
    }

    //var authToken:String? = preferencesManager.authToken
}