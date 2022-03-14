package app.thirtyninth.githubviewer.data.network

class NetworkException(message: String) : Exception()

class NoInternetException : Exception()

class UnauthorizedException: Exception()

class NotFoundException: Exception()

class UnexpectedException(cause: Exception): Exception(cause)