package app.thirtyninth.githubviewer.utils

import android.content.res.Resources
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.network.EmptyDataException
import app.thirtyninth.githubviewer.data.network.HttpCallException
import app.thirtyninth.githubviewer.data.network.NetworkException
import app.thirtyninth.githubviewer.data.network.NoInternetException
import app.thirtyninth.githubviewer.data.network.NotFoundException
import app.thirtyninth.githubviewer.data.network.UnauthorizedException
import app.thirtyninth.githubviewer.data.network.UnexpectedException
import app.thirtyninth.githubviewer.ui.interfaces.Validation

fun mapExceptionToStringMessage(throwable: Throwable, resources: Resources): String {
    return when (throwable) {
        is EmptyDataException -> resources.getString(R.string.exception_message_empty_data)
        is HttpCallException -> resources.getString(R.string.exception_message_http_call)
        is NetworkException -> resources.getString(R.string.exception_message_network)
        is NoInternetException -> resources.getString(R.string.exception_message_no_internet)
        is NotFoundException -> resources.getString(R.string.exception_message_not_found)
        is UnauthorizedException -> resources.getString(R.string.exception_message_unauthorized)
        is UnexpectedException -> resources.getString(R.string.exception_message_unexpected)
        else -> {
            throwable.message.toString()
        }
    }
}

fun mapValidation(status: Validation, resources: Resources): String? {
    return when (status) {
        is Validation.Correct -> null
        is Validation.Incorrect -> resources.getString(R.string.field_error_message_incorrect)
        is Validation.Empty -> resources.getString(R.string.field_error_message_is_empty)
    }
}