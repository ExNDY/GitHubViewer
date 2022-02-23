package app.thirtyninth.githubviewer.ui

interface UIState {
    fun setNormalState()
    fun setLoadingState()
    fun setErrorState()
}