package app.thirtyninth.githubviewer.ui.interfaces

interface RecyclerViewActionListener {
    fun onClick(clickedPosition: Int, owner: String, repositoryName: String)
}