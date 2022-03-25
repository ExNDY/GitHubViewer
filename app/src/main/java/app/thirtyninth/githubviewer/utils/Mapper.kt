package app.thirtyninth.githubviewer.utils

import android.content.res.Resources
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.network.NoInternetException
import app.thirtyninth.githubviewer.data.network.NotFoundException
import app.thirtyninth.githubviewer.data.network.UnauthorizedException

fun mapExceptionToMessage(throwable: Throwable, resources: Resources): String {
    return when (throwable) {
        //TODO Накинуть ресурсов для обозначения ошибок
        is NoInternetException -> resources.getString(R.string.request_error_connection_with_server)
        is UnauthorizedException -> resources.getString(R.string.request_error_401_authentication_error)
        is NotFoundException -> "TODO"
        else -> {
            ""
        }
    }
}