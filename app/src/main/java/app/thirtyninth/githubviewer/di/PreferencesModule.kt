package app.thirtyninth.githubviewer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import app.thirtyninth.githubviewer.ProtoSettings
import app.thirtyninth.githubviewer.preferences.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

// FIXME у нас же ниже object - в нем и нужно было приватную константу положить. зачем на toplevel?
//  в текущем виде после компиляции у нас получится 2 класса - PreferencesModule с статической функцией
//  и PreferencesModuleKt с статическим полем. а можно было 1 классом PreferencesModule обойтись :)
private const val PREFERENCES_NAME = "preferences"

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun provideSettings(@ApplicationContext context: Context): DataStore<ProtoSettings> {
        return DataStoreFactory.create(
            serializer = UserPreferences.PreferencesSerializer,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.dataStoreFile(PREFERENCES_NAME) },
            corruptionHandler = null
        )
    }

/*
    private val Context.dataStore : DataStore<ProtoSettings> by dataStore(
        fileName = "preferences.proto",
        serializer = PreferencesSerializer
    )*/
}

class KeyValueStorage @Inject constructor(dataStore: DataStore<ProtoSettings>) {
    val tokenStateFlow: StateFlow<String?> = dataStore.data.map { it.userToken }
        .stateIn(GlobalScope, SharingStarted.Eagerly, null)

}
