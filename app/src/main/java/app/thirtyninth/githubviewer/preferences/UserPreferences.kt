package app.thirtyninth.githubviewer.preferences

import androidx.datastore.core.DataStore
import app.thirtyninth.githubviewer.ProtoSettings
import app.thirtyninth.githubviewer.data.models.LoginData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(private val dataStore: DataStore<ProtoSettings>) :
    Preferences {
    override suspend fun getLoggedInState(): Flow<Boolean> {
        return dataStore.data
            .map { it.isLoggedIn }
    }

    override suspend fun getAuthenticationToken(): Flow<String?> {
        return dataStore.data
            .map { it.authToken }
    }

    override suspend fun getLoginData(): Flow<LoginData?> {
        return dataStore.data
            .map { LoginData(it.authToken) }
    }

    override suspend fun saveUser(user: LoginData) {
        dataStore.updateData {
            it.toBuilder()
                .setIsLoggedIn(true)
                .setAuthToken(user.authToken)
                .build()
        }
    }

    override suspend fun logout() {
        dataStore.updateData {
            it.toBuilder()
                .clear()
                .build()
        }
    }

    val saved get() = dataStore.data.take(1)
}