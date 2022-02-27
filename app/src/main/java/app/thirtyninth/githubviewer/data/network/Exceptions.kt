package app.thirtyninth.githubviewer.data.network

class Exceptions {
    companion object {
        const val ERROR_SERVER_EXCEPTIONS: String = "server_error"
        const val ERROR_AUTHENTICATOR: String = "Status: 401 Unauthorized"
        const val ERROR_NOT_MODIFIED: String = "Status: 304 Not Modified"
        const val ERROR_UNAUTHORIZED: String = ""
        const val ERROR_FORBIDDEN: String = ""
        const val ERROR_NOT_FOUND: String = ""
        const val ERROR_UNPROCESSABLE_ENTITY: String = ""
        const val ERROR_UNSUPPORTED_MEDIA_TYPE: String = ""
    }
}