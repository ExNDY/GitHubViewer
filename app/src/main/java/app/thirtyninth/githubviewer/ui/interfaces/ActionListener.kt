package app.thirtyninth.githubviewer.ui.interfaces

import app.thirtyninth.githubviewer.data.models.GitHubRepository

interface ActionListener {
    //FIXME Переделать на получение позиции
    fun onClick(item: GitHubRepository)
}