package app.thirtyninth.githubviewer.preferences

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import app.thirtyninth.githubviewer.ProtoSettings
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object PreferencesSerializer : Serializer<ProtoSettings> {
    override val defaultValue: ProtoSettings
        get() = ProtoSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ProtoSettings {
        return try {
            ProtoSettings.parseFrom(input)
        } catch (ex: InvalidProtocolBufferException){
            throw CorruptionException("Cannot read proto.", ex)
        }
    }

    override suspend fun writeTo(t: ProtoSettings, output: OutputStream) = t.writeTo(output)

}