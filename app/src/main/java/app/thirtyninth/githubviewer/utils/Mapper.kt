package app.thirtyninth.githubviewer.utils

import android.content.Context
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.models.ExceptionBundle
import app.thirtyninth.githubviewer.data.network.EmptyDataException
import app.thirtyninth.githubviewer.data.network.HttpCallException
import app.thirtyninth.githubviewer.data.network.NetworkException
import app.thirtyninth.githubviewer.data.network.NoInternetException
import app.thirtyninth.githubviewer.data.network.NotFoundException
import app.thirtyninth.githubviewer.data.network.UnauthorizedException
import app.thirtyninth.githubviewer.data.network.UnexpectedException
import app.thirtyninth.githubviewer.ui.interfaces.LocalizeString
import app.thirtyninth.githubviewer.ui.interfaces.ValidationResult

fun mapAlertMessage(exceptionBundle: ExceptionBundle, context: Context): String {
    return if (exceptionBundle.errorCode != null
        && exceptionBundle.request != null
        && exceptionBundle.url != null
    ) {
        "code: ${exceptionBundle.errorCode}" + "\n" +
                "request: ${exceptionBundle.request}" + "\n" +
                "url: ${exceptionBundle.url}" + "\n" +
                "message: ${exceptionBundle.message.getString(context)}"
    } else {
        exceptionBundle.message.getString(context)
    }
}

fun mapExceptionToBundle(throwable: Throwable): ExceptionBundle {
    return when (throwable) {
        is EmptyDataException -> ExceptionBundle(
            LocalizeString.Resource(R.string.exception_title_empty_data),
            LocalizeString.Resource(R.string.exception_message_empty_data),
            R.drawable.img_empty,
            R.color.exceptionEmptyColor
        )
        is HttpCallException -> ExceptionBundle(
            LocalizeString.Resource(R.string.exception_title_http_call),
            LocalizeString.Resource(R.string.exception_message_http_call),
            R.drawable.img_error,
            R.color.exceptionColor
        )
        is NetworkException -> ExceptionBundle(
            LocalizeString.Resource(R.string.exception_title_network),
            LocalizeString.Resource(R.string.exception_message_network),
            R.drawable.img_no_internet,
            R.color.exceptionColor
        )
        is NoInternetException -> ExceptionBundle(
            LocalizeString.Resource(R.string.exception_title_no_internet),
            LocalizeString.Resource(R.string.exception_message_no_internet),
            R.drawable.img_no_internet,
            R.color.exceptionColor
        )
        is NotFoundException -> ExceptionBundle(
            LocalizeString.Resource(R.string.exception_title_not_found),
            LocalizeString.Resource(R.string.exception_message_not_found),
            R.drawable.img_error,
            R.color.exceptionColor,
            throwable.code,
            throwable.request,
            throwable.url
        )
        is UnauthorizedException -> {
            ExceptionBundle(
                LocalizeString.Resource(R.string.exception_title_unauthorized),
                LocalizeString.Resource(R.string.exception_message_unauthorized),
                R.drawable.img_error,
                R.color.exceptionColor,
                throwable.code,
                throwable.request,
                throwable.url
            )
        }
        is UnexpectedException -> ExceptionBundle(
            LocalizeString.Resource(R.string.exception_title_unexpected),
            LocalizeString.Resource(R.string.exception_message_unexpected),
            R.drawable.img_error,
            R.color.exceptionColor,
        )
        else -> {
            ExceptionBundle(
                LocalizeString.Resource(R.string.exception_title_unknown),
                LocalizeString.Raw(throwable.message.toString()),
                R.drawable.img_error,
                R.color.exceptionColor
            )
        }
    }
}

fun mapTokenValidation(status: ValidationResult): LocalizeString {
    return when (status) {
        is ValidationResult.Correct -> LocalizeString.Raw("")
        is ValidationResult.Incorrect -> LocalizeString.Resource(R.string.field_error_message_incorrect)
        is ValidationResult.Empty -> LocalizeString.Resource(R.string.field_error_message_is_empty)
    }
}