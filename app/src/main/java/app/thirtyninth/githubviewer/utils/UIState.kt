package app.thirtyninth.githubviewer.utils

enum class UIState {
    NORMAL,
    LOADING,
    SUCCESS,
    ERROR
}
enum class TokenState {
    CORRECT,
    INVALID,
    UNAUTORIZED,
    FORBIDDEN,
    EMPTY
}

enum class UsernameState {
    CORRECT,
    INVALID,
    NOT_MODIFIED,
    UNAUTORIZED,
    FORBIDDEN,
    EMPTY
}