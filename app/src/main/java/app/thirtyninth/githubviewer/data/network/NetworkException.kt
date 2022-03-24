package app.thirtyninth.githubviewer.data.network

import okhttp3.HttpUrl
import java.io.IOException

open class NetworkException : RuntimeException {
    constructor() : super()
    constructor(cause: Throwable) : super(cause)
}

class NoInternetException : IOException {
    constructor() : super()
    constructor(cause: Throwable) : super(cause)
}

class EmptyDataException() : NetworkException()

class UnauthorizedException(code: Int, request: String, url: HttpUrl) : NetworkException()

class NotFoundException(code: Int, request: String, url: HttpUrl) : NetworkException()

class HttpCallException(cause: Throwable) : NetworkException(cause)

class UnexpectedException(cause: Throwable) : NetworkException(cause)