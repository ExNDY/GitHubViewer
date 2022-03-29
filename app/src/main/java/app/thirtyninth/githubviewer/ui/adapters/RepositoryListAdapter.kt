package app.thirtyninth.githubviewer.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.databinding.RepositoriesListItemBinding
import app.thirtyninth.githubviewer.ui.interfaces.ActionListener

class RepositoryListAdapter(
    private val colors: Map<String, Color>,
    private val listener: ActionListener
) : ListAdapter<GitHubRepository, RepositoryListAdapter.RepositoryListViewHolder>(
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

        holder.bind(item)

        if (languageColor != null) {
            holder.itemBinding.language.setTextColor(
                languageColor.toArgb()
            )
        }

        holder.itemBinding.itemContainer.setOnClickListener {
            listener.onClick(position)
        }
    }

    class RepositoryListViewHolder(
        val itemBinding: RepositoriesListItemBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: GitHubRepository) {
            itemBinding.repositoryName.text = item.name
            itemBinding.language.text = item.language
            itemBinding.repositoryDescription.text = item.description
        }
    }
}

private class RepositoryListDiffCallback : DiffUtil.ItemCallback<GitHubRepository>() {
    override fun areItemsTheSame(
        oldItem: GitHubRepository,
        newItem: GitHubRepository
    ): Boolean =
        oldItem.id == newItem.id


    override fun areContentsTheSame(
        oldItem: GitHubRepository,
        newItem: GitHubRepository
    ): Boolean =
        oldItem == newItem
}