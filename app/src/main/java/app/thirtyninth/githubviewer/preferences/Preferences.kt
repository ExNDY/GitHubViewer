package app.thirtyninth.githubviewer.preferences

import app.thirtyninth.githubviewer.data.models.User
import kotlinx.coroutines.flow.Flow

interface Preferences {
    suspend fun getLoggedInState(): Flow<Boolean>
    suspend fun getAuthenticationToken(): Flow<String?>
    suspend fun getUserName(): Flow<String?>
    suspend fun saveUser(user: User)
    suspend fun clear()
}