package app.thirtyninth.githubviewer.data.network

import okhttp3.HttpUrl
import java.io.IOException

open class NetworkException : RuntimeException {
    constructor() : super()
    constructor(cause: Throwable) : super(cause)
}

class NoInternetException(cause: Throwable) : IOException(cause)

class EmptyDataException : NetworkException()

class UnauthorizedException(val code: Int, val request: String, val url: HttpUrl) : NetworkException()

class NotFoundException(val code: Int, val request: String, val url: HttpUrl) : NetworkException()

class HttpCallException(cause: Throwable) : NetworkException(cause)

class UnexpectedException(cause: Throwable) : NetworkException(cause)