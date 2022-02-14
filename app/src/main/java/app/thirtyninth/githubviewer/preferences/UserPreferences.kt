package app.thirtyninth.githubviewer.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import app.thirtyninth.githubviewer.ProtoSettings
import app.thirtyninth.githubviewer.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class UserPreferences @Inject constructor(private val dataStore: DataStore<ProtoSettings>) : Preferences {
    override suspend fun getLoggedInState(): Flow<Boolean> {
        return dataStore.data
            .map { it.isLoggedIn }
    }

    override suspend fun getAuthenticationToken(): Flow<String?> {
        return dataStore.data
            .map { it.userToken }
    }

    override suspend fun getUserName(): Flow<String?> {
        return dataStore.data
            .map { it.userName }
    }

    override suspend fun saveUser(user: User) {
        dataStore.updateData {
            it.toBuilder()
                .setIsLoggedIn(true)
                .setUserName(user.username)
                .setUserToken(user.token)
                .build()
        }
    }

    override suspend fun clear() {
        dataStore.updateData {
            it.toBuilder()
                .clear()
                .build()
        }
    }

    val saved get() = dataStore.data.take(1)


        private val Context.dataStore: DataStore<ProtoSettings> by dataStore(
    fileName = "preferences.pb",
    serializer = PreferencesSerializer
)
}