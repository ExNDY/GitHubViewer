package app.thirtyninth.githubviewer.preferences

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import app.thirtyninth.githubviewer.ProtoSettings
import app.thirtyninth.githubviewer.ProtoSettings.parseFrom
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

@Singleton
object PreferencesSerializer : Serializer<ProtoSettings> {
    override val defaultValue: ProtoSettings = ProtoSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ProtoSettings = withContext(Dispatchers.IO) {
        try {
            return@withContext parseFrom(input)
        } catch (ex: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", ex)
        }
    }

    override suspend fun writeTo(t: ProtoSettings, output: OutputStream) =
        withContext(Dispatchers.IO) {
            return@withContext t.writeTo(output)
        }

}