package app.thirtyninth.githubviewer.preferences

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import app.thirtyninth.githubviewer.ProtoSettings
import app.thirtyninth.githubviewer.data.models.LoginData
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

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
            .catch { ex ->
                if (ex is IOException){
                    emit(ProtoSettings.getDefaultInstance())
                } else {
                    throw ex
                }
            }
            .map {
                it.userName
            }
    }

    override suspend fun saveUser(user: LoginData) {
        dataStore.updateData {
            it.toBuilder()
                .setIsLoggedIn(true)
                .setUserName(user.username)
                .setUserToken("token " + user.token)
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

    @Suppress("BlockingMethodInNonBlockingContext")
    object PreferencesSerializer : Serializer<ProtoSettings> {
        override val defaultValue: ProtoSettings
            get() = ProtoSettings.getDefaultInstance()

        override suspend fun readFrom(input: InputStream): ProtoSettings {
            return try {
                ProtoSettings.parseFrom(input)
            } catch (ex: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", ex)
            }
        }

        override suspend fun writeTo(t: ProtoSettings, output: OutputStream) = t.writeTo(output)

    }
}