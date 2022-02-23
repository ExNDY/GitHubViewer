package app.thirtyninth.githubviewer.preferences

import app.thirtyninth.githubviewer.data.models.LoginData
import kotlinx.coroutines.flow.Flow

interface Preferences {
    suspend fun getLoggedInState(): Flow<Boolean>
    suspend fun getAuthenticationToken(): Flow<String?>
    suspend fun getUserName(): Flow<String?>
    suspend fun getLoginData(): Flow<LoginData?>
    suspend fun saveUser(user: LoginData)
    suspend fun logout()
}