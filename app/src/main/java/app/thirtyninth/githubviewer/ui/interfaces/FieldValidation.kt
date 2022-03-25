package app.thirtyninth.githubviewer.ui.interfaces

sealed interface FieldValidation{
    object Correct: FieldValidation
    object Incorrect: FieldValidation
    object Empty: FieldValidation
}
