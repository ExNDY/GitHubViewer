package app.thirtyninth.githubviewer.data.network

import java.io.IOException

class NetworkException(message: String) : Exception()

class NoInternetException(cause: Exception) : IOException(cause)

class UnauthorizedException: Exception()

class NotFoundException: Exception()

class UnexpectedException(cause: Exception): Exception(cause)