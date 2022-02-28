package app.thirtyninth.githubviewer.ui.adapters

import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Bundle
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

                if (languageTextColor != null){
                    itemBinding.language.setTextColor(
                        languageTextColor.toArgb()
                    )
                }

                itemBinding.language.text = language
            }

        }

        fun bindDescription(description: String?) {
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

    override fun onBindViewHolder(
        holder: RepositoryListViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val bundle = payloads[0] as Bundle

            for (key: String in bundle.keySet()) {
                if (key == PAYLOAD_DESCRIPTION) {
                    bundle.getString(PAYLOAD_DESCRIPTION)?.let { holder.bindDescription(it) }
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int = currentList.size
}

private const val PAYLOAD_DESCRIPTION = "PAYLOAD_DESCRIPTION"

private class RepositoryListDiffCallback : DiffUtil.ItemCallback<GitHubRepositoryModel>() {
    override fun getChangePayload(
        oldItem: GitHubRepositoryModel,
        newItem: GitHubRepositoryModel
    ): Any? {
        if (newItem.name == oldItem.name) {
            return if (newItem.description == oldItem.description) {
                super.getChangePayload(oldItem, newItem)
            } else {
                val bundle = Bundle()

                bundle.putString(PAYLOAD_DESCRIPTION, newItem.description)
                bundle
            }
        }

        return super.getChangePayload(oldItem, newItem)
    }

    override fun areItemsTheSame(
        oldItem: GitHubRepositoryModel,
        newItem: GitHubRepositoryModel
    ): Boolean =
        oldItem.name == newItem.name


    override fun areContentsTheSame(
        oldItem: GitHubRepositoryModel,
        newItem: GitHubRepositoryModel
    ): Boolean =
        oldItem == newItem
}