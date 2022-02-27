package app.thirtyninth.githubviewer.ui

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.annotation.NonNull
import androidx.annotation.RequiresPermission
import app.thirtyninth.githubviewer.utils.Variables

class NetworkConnectionManager(applicationContext:Context) {
    val context = applicationContext

    private var isRegistered = false

    fun stopNetworkCallback() {
        if (isRegistered){
            val connectivityManager = context.getSystemService(Context. CONNECTIVITY_SERVICE ) as ConnectivityManager

            connectivityManager.unregisterNetworkCallback(networkCallback)

            isRegistered = false
        }
    }

    fun startNetworkCallback() {
        try {
            if (!isRegistered){
                val connectivityManager = context.getSystemService(Context. CONNECTIVITY_SERVICE ) as ConnectivityManager

                Variables.isNetworkConnected = checkConnection(connectivityManager)

                connectivityManager.registerDefaultNetworkCallback(networkCallback)

                isRegistered = true
            }
        } catch (e: Exception) {
            set(false)
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            set(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)

            set(false)
        }
    }

    private fun set(isOnline: Boolean) {
        Variables.isNetworkConnected = isOnline
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun checkConnection(@NonNull connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork

        return if (network == null) {
            false
        } else {
            val actualNetworkCapabilities = connectivityManager.getNetworkCapabilities(network)

            if (actualNetworkCapabilities != null) {
                ((actualNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                        || actualNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || actualNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
            } else false
        }
    }
}