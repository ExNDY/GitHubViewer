package app.thirtyninth.githubviewer.ui.interfaces

import app.thirtyninth.githubviewer.data.models.GitHubRepository

interface ActionListener {
    fun onClick(item: GitHubRepository)
}