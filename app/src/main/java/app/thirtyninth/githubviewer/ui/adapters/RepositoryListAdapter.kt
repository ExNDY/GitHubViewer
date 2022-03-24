package app.thirtyninth.githubviewer.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.databinding.RepositoriesListItemBinding
import app.thirtyninth.githubviewer.ui.interfaces.ActionListener

class RepositoryListAdapter(
    private val colors: Map<String, Color>,
    private val listener: ActionListener
) : ListAdapter<GitHubRepositoryModel, RepositoryListAdapter.RepositoryListViewHolder>(
    RepositoryListDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryListViewHolder {
        val itemBinding =
            RepositoriesListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )

        return RepositoryListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RepositoryListViewHolder, position: Int) {
        val item = currentList[position]
        val languageColor = colors[item.language]

        holder.bind(item, languageColor)
        holder.container.setOnClickListener {
            listener.onClick(position, item.owner?.login.toString(), item.name.toString())
        }
    }

    class RepositoryListViewHolder(
        private val itemBinding: RepositoriesListItemBinding
        ) : RecyclerView.ViewHolder(itemBinding.root) {

        val container = itemBinding.itemContainer

        fun bind(item: GitHubRepositoryModel, languageTextColor: Color?) {
            bindName(item.name)
            bindLanguage(item.language, languageTextColor)
            bindDescription(item.description ?: "")
        }

        private fun bindName(name: String?) {
            itemBinding.repositoryName.text = name
        }

        private fun bindLanguage(language: String?, languageTextColor: Color?) {
            if (languageTextColor != null) {
                itemBinding.language.setTextColor(
                    languageTextColor.toArgb()
                )
            }

            itemBinding.language.text = language
        }

        private fun bindDescription(description: String?) {
            if (description.isNullOrEmpty()) {
                itemBinding.repositoryDescription.visibility = View.GONE
            } else {
                itemBinding.repositoryDescription.text = description
            }
        }
    }
}

private class RepositoryListDiffCallback : DiffUtil.ItemCallback<GitHubRepositoryModel>() {
    override fun areItemsTheSame(
        oldItem: GitHubRepositoryModel,
        newItem: GitHubRepositoryModel
    ): Boolean =
        oldItem.id == newItem.id


    override fun areContentsTheSame(
        oldItem: GitHubRepositoryModel,
        newItem: GitHubRepositoryModel
    ): Boolean =
        oldItem == newItem
}