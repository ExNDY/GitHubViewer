package app.thirtyninth.githubviewer.utils

import java.util.regex.Pattern

object ValidationsUtil {
    private const val USERNAME_PATTERN = "^[a-zA-Z0-9_-]{0,38}$"
    private const val AUTHORIZATION_TOKEN_PATTERN = "^gh[pousr]_[a-zA-Z0-9]{0,40}$"

    fun validateUserName(userName: String): UsernameState {
        if (userName.isEmpty()) return UsernameState.EMPTY

        val matcher = Pattern.compile(USERNAME_PATTERN).matcher(userName)
        return if (matcher.matches()) UsernameState.CORRECT else UsernameState.INVALID
    }

    fun validateAuthorisationToken(token: String): TokenState {
        if (token.isEmpty()) return TokenState.EMPTY

        val matcher = Pattern.compile(AUTHORIZATION_TOKEN_PATTERN).matcher(token)

        return if (matcher.matches()) TokenState.CORRECT else TokenState.INVALID
    }
}