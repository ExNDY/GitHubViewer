package app.thirtyninth.githubviewer.ui.interfaces

sealed interface ValidationResult {
    object Correct : ValidationResult
    object Incorrect : ValidationResult
    object Empty : ValidationResult
}
