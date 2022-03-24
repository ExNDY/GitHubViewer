package app.thirtyninth.githubviewer.data.network

import okhttp3.HttpUrl
import java.io.IOException

open class NetworkException : RuntimeException {
    constructor() : super()
    constructor(cause: Throwable) : super(cause)
}

class NoInternetException(cause: Throwable) : IOException(cause)

class UnauthorizedException(msg: String, url: HttpUrl) : NetworkException()

class NotFoundException(msg: String, url: HttpUrl) : NetworkException()

class UnexpectedException(cause: Throwable) : NetworkException(cause)