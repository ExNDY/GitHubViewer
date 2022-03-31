package app.thirtyninth.githubviewer.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.databinding.RepositoriesListItemBinding

class RepositoryListAdapter(
    private val colors: Map<String, Color>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<RepositoryListAdapter.RepositoryListViewHolder>() {

    private var items: List<GitHubRepository> = emptyList()

    class RepositoryListViewHolder(
        val itemBinding: RepositoriesListItemBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: GitHubRepository) {
            itemBinding.repositoryName.text = item.name
            itemBinding.language.text = item.language
            itemBinding.repositoryDescription.text = item.description
        }
    }

    fun submitList(list: List<GitHubRepository>) {
        val diffCallback = RepositoriesDiffCallback(items, list)
        val difResult = DiffUtil.calculateDiff(diffCallback)

        items = emptyList()
        items = list
        difResult.dispatchUpdatesTo(this)
    }

    fun getItem(position: Int): GitHubRepository = items[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryListViewHolder {
        val itemBinding =
            RepositoriesListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )

        return RepositoryListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RepositoryListViewHolder, position: Int) {
        val item = items[position]
        val languageColor = colors[item.language]

        holder.bind(item)

        if (languageColor != null) {
            holder.itemBinding.language.setTextColor(
                languageColor.toArgb()
            )
        }

        holder.itemBinding.itemContainer.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = items.size
}

class RepositoriesDiffCallback(
    private val oldRepositoriesList: List<GitHubRepository>,
    private val newRepositoriesList: List<GitHubRepository>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldRepositoriesList.size

    override fun getNewListSize(): Int = newRepositoriesList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRepositoriesList[oldItemPosition].id == newRepositoriesList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRepositoriesList[oldItemPosition] == newRepositoriesList[newItemPosition]
    }

}