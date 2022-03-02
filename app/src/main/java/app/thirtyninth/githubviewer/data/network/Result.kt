package app.thirtyninth.githubviewer.data.network

sealed class Result<out R> {
    data class Success<out T>(val data: T, val code: Int?) : Result<T>()

    data class Error(val exception: Exception, val code: Int?) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success [data = $data]"
            is Error -> "Error [exception = $exception"
        }
    }
}
