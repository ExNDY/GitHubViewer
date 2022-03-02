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
            .map { it.userToken }
    }

    override suspend fun getUserName(): Flow<String?> {
        return dataStore.data
            .map { it.userName }
    }

    override suspend fun getLoginData(): Flow<LoginData?> {
        return dataStore.data
            .map { LoginData(it.userName, it.userToken) }
    }

    override suspend fun saveUser(user: LoginData) {
        dataStore.updateData {
            it.toBuilder()
                .setIsLoggedIn(true)
                .setUserName(user.username)
                .setUserToken(user.token)
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