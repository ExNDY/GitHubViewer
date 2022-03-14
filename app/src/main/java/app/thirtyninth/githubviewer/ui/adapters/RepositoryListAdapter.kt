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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class RepositoryListAdapter(
    colors: JsonObject,
    val onItemClicked: (GitHubRepositoryModel) -> Unit
) : ListAdapter<GitHubRepositoryModel, RepositoryListAdapter.RepositoryListViewHolder>(
    RepositoryListDiffCallback()
) {
    val languageColors: Map<String, Color> = colors.mapValues { (_, colorString) ->
        Color.parseColor(colorString.jsonPrimitive.content).let {
            Color.valueOf(it)
        }
    }

    //Inner class implementation for using initial external object with colors of languages
    inner class RepositoryListViewHolder(
        private val itemBinding: RepositoriesListItemBinding,
        onItemClicked: (Int) -> Unit
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        init {
            itemView.setOnClickListener {
                onItemClicked(bindingAdapterPosition)
            }
        }

        fun bind(repository: GitHubRepositoryModel) {
            bindName(repository.name)

            bindLanguage(repository.language)
            bindDescription(repository.description)
        }

        private fun bindName(name: String?) {
            itemBinding.repositoryName.text = name
        }

        private fun bindLanguage(language: String?) {
            if (language.isNullOrEmpty()) {
                itemBinding.language.visibility = View.GONE
            } else {
                val languageTextColor = languageColors[language]

                if (languageTextColor != null) {
                    itemBinding.language.setTextColor(
                        languageTextColor.toArgb()
                    )
                }

                itemBinding.language.text = language
            }

        }

        private fun bindDescription(description: String?) {
            if (description.isNullOrEmpty()) {
                itemBinding.repositoryDescription.visibility = View.GONE
            } else {
                itemBinding.repositoryDescription.text = description
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryListViewHolder {
        val itemBinding =
            RepositoriesListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RepositoryListViewHolder(itemBinding) {
            onItemClicked(getItem(it))
        }
    }

    override fun onBindViewHolder(holder: RepositoryListViewHolder, position: Int) {
        holder.bind(currentList[position])
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