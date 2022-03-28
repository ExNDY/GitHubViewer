package app.thirtyninth.githubviewer.utils

import app.thirtyninth.githubviewer.ui.interfaces.ValidationResult
import java.util.regex.Pattern

object Validator {
    private const val AUTHORIZATION_TOKEN_PATTERN = "^gh[pousr]_[a-zA-Z0-9]{0,40}$"

    fun validateAuthorisationToken(authToken: String): ValidationResult {
        if (authToken.isEmpty()) return ValidationResult.Empty

        val matcher = Pattern.compile(AUTHORIZATION_TOKEN_PATTERN).matcher(authToken)

        return if (matcher.matches()) ValidationResult.Correct else ValidationResult.Incorrect
    }
}