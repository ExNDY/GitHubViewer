package app.thirtyninth.githubviewer.preferences

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import app.thirtyninth.githubviewer.ProtoSettings
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

//TODO FIX THIS
@Suppress("BlockingMethodInNonBlockingContext")
@Singleton
object PreferencesSerializer : Serializer<ProtoSettings> {
    override val defaultValue: ProtoSettings = ProtoSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ProtoSettings {
        try {
            return ProtoSettings.parseFrom(input)
        } catch (ex: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", ex)
        }
    }

    override suspend fun writeTo(t: ProtoSettings, output: OutputStream) = t.writeTo(output)

}