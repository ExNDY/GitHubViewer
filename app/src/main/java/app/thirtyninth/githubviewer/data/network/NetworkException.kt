package app.thirtyninth.githubviewer.data.network

import okhttp3.HttpUrl
import java.io.IOException

open class NetworkException(cause: Throwable?) : RuntimeException(cause)

class NoInternetException(cause: Exception) : IOException(cause)

class UnauthorizedException(msg: String, url: HttpUrl) : NetworkException(null)

class NotFoundException(msg: String, url: HttpUrl) : NetworkException(null)

class UnexpectedException(cause: Exception) : NetworkException(cause)