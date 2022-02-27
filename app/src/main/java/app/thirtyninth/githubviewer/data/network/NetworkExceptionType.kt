package app.thirtyninth.githubviewer.data.network

enum class NetworkExceptionType {
    OK,
    NOT_MODIFIED,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    UNPROCESSABLE_ENTITY,
    UNSUPPORTED_MEDIA_TYPE,
    SERVER_ERROR,
    NOT_DETERMINED,
    NONE,
    EMPTY_DATA
}