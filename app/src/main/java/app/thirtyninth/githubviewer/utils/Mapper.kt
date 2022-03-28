package app.thirtyninth.githubviewer.utils

import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.network.EmptyDataException
import app.thirtyninth.githubviewer.data.network.HttpCallException
import app.thirtyninth.githubviewer.data.network.NetworkException
import app.thirtyninth.githubviewer.data.network.NoInternetException
import app.thirtyninth.githubviewer.data.network.NotFoundException
import app.thirtyninth.githubviewer.data.network.UnauthorizedException
import app.thirtyninth.githubviewer.data.network.UnexpectedException
import app.thirtyninth.githubviewer.ui.interfaces.LocalizeString
import app.thirtyninth.githubviewer.ui.interfaces.ValidationResult

fun mapExceptionToStringMessage(throwable: Throwable): LocalizeString {
    return when (throwable) {
        is EmptyDataException -> LocalizeString.Resource(R.string.exception_message_empty_data)
        is HttpCallException -> LocalizeString.Resource(R.string.exception_message_http_call)
        is NetworkException -> LocalizeString.Resource(R.string.exception_message_network)
        is NoInternetException -> LocalizeString.Resource(R.string.exception_message_no_internet)
        is NotFoundException -> LocalizeString.Resource(R.string.exception_message_not_found)
        is UnauthorizedException -> LocalizeString.Resource(R.string.exception_message_unauthorized)
        is UnexpectedException -> LocalizeString.Resource(R.string.exception_message_unexpected)
        else -> {
            LocalizeString.Raw(throwable.message.toString())
        }
    }
}

fun mapTokenValidation(status: ValidationResult): LocalizeString {
    return when (status) {
        is ValidationResult.Correct -> LocalizeString.Raw("")
        is ValidationResult.Incorrect -> {
            val msg = LocalizeString.Resource(R.string.field_error_message_incorrect)
            msg
        }
        is ValidationResult.Empty -> LocalizeString.Resource(R.string.field_error_message_is_empty)
    }
}