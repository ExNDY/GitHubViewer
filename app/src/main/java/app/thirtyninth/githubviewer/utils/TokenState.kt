package app.thirtyninth.githubviewer.utils

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