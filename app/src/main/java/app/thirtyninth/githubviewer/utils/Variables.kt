package app.thirtyninth.githubviewer.utils

import kotlin.properties.Delegates

object Variables {
    var isNetworkConnected: Boolean by Delegates.observable(false) { _, _, _ ->
    }
}