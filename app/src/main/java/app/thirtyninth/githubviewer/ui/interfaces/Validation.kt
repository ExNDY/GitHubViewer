package app.thirtyninth.githubviewer.ui.interfaces

sealed interface Validation{
    object Correct: Validation
    object Incorrect: Validation
    object Empty: Validation
}
