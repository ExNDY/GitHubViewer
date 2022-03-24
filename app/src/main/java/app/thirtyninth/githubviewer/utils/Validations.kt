package app.thirtyninth.githubviewer.utils

import java.util.regex.Pattern

object Validations {
    private const val LOGIN_PATTERN = "^[a-zA-Z0-9_-]{0,38}$"
    private const val AUTHORIZATION_TOKEN_PATTERN = "^gh[pousr]_[a-zA-Z0-9]{0,40}$"

    fun validateLogin(login: String): LoginState {
        if (login.isEmpty()) return LoginState.EMPTY

        val matcher = Pattern.compile(LOGIN_PATTERN).matcher(login)
        return if (matcher.matches()) LoginState.CORRECT else LoginState.INVALID
    }

    fun validateAuthorisationToken(authToken: String): TokenState {
        if (authToken.isEmpty()) return TokenState.EMPTY

        val matcher = Pattern.compile(AUTHORIZATION_TOKEN_PATTERN).matcher(authToken)

        return if (matcher.matches()) TokenState.CORRECT else TokenState.INVALID
    }
}