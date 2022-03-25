package app.thirtyninth.githubviewer.preferences

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class IntPreference(
    private val sharedPreference: SharedPreferences,
    private val key: String,
    private val defaultValue: Int = 0
) : ReadWriteProperty<Any, Int> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Int =
        sharedPreference.getInt(key, defaultValue)


    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        sharedPreference.edit()
            .putInt(key, value)
            .apply()
    }
}

class BooleanPreference(
    private val sharedPreference: SharedPreferences,
    private val key: String,
    private val defaultValue: Boolean = false
) : ReadWriteProperty<Any, Boolean> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean =
        sharedPreference.getBoolean(key, defaultValue)


    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        sharedPreference.edit()
            .putBoolean(key, value)
            .apply()
    }
}

class StringPreference(
    private val sharedPreference: SharedPreferences,
    private val key: String,
    private val defaultValue: String? = null
) : ReadWriteProperty<Any, String?> {
    override fun getValue(thisRef: Any, property: KProperty<*>): String? =
        sharedPreference.getString(key, defaultValue)


    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        sharedPreference.edit()
            .putString(key, value)
            .apply()
    }
}