package app.thirtyninth.githubviewer.ui.interfaces

interface ActionListener {
    fun onClick(clickedPosition: Int, owner: String, repositoryName: String)
}