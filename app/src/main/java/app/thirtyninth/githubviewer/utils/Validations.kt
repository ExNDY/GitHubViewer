package app.thirtyninth.githubviewer.utils

import app.thirtyninth.githubviewer.ui.interfaces.Validation
import java.util.regex.Pattern

object Validations {
    private const val LOGIN_PATTERN = "^[a-zA-Z0-9_-]{0,38}$"
    private const val AUTHORIZATION_TOKEN_PATTERN = "^gh[pousr]_[a-zA-Z0-9]{0,40}$"

    fun validateLogin(login: String): Validation {
        //Temporary muted while don't found solution for MutableStateFlow
        //if (login.isEmpty()) return FieldValidation.Empty

        val matcher = Pattern.compile(LOGIN_PATTERN).matcher(login)
        return if (matcher.matches()) Validation.Correct else Validation.Incorrect
    }

    fun validateAuthorisationToken(authToken: String): Validation {
        //Temporary muted while don't found solution for MutableStateFlow
        //if (authToken.isEmpty()) return FieldValidation.Empty

        val matcher = Pattern.compile(AUTHORIZATION_TOKEN_PATTERN).matcher(authToken)

        return if (matcher.matches()) Validation.Correct else Validation.Incorrect
    }
}