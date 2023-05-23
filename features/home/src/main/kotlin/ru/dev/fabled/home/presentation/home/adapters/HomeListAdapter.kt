package ru.dev.fabled.home.presentation.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.dev.fabled.core.ListItemClickListener
import ru.dev.fabled.domain.model.SearchData
import ru.dev.fabled.home.R
import ru.dev.fabled.home.databinding.ItemPersonBinding
import ru.dev.fabled.home.databinding.ItemRepositoryBinding

private const val TYPE_REPOSITORY = 0
private const val TYPE_PERSON = 1

class HomeListAdapter(
    private val listItemClickListener: ListItemClickListener<SearchData>
) : ListAdapter<SearchData, RecyclerView.ViewHolder>(SearchDataDiffUtil) {

    inner class RepositoryItemViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repository: SearchData.RepositoryModel) = with(binding) {
            repositoryName.text = repository.repositoryName
            forksCount.text =
                forksCount.context.getString(
                    R.string.forks_template,
                    repository.forksCount
                )
            repositoryDescription.text = repository.description

            root.setOnClickListener { listItemClickListener.onItemClick(repository) }
        }

    }

    inner class PersonItemViewHolder(private val binding: ItemPersonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: SearchData.PersonModel) = with(binding) {
            personIcon.load(person.avatarUrl) {
                crossfade(enable = true)
                placeholder(R.drawable.ic_person_loading)
                error(R.drawable.ic_person_error)
            }

            personLogin.text = person.userLogin
            personScore.text = person.score.toString()

            root.setOnClickListener { listItemClickListener.onItemClick(person) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_REPOSITORY -> {
                val binding = ItemRepositoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                RepositoryItemViewHolder(binding)
            }

            TYPE_PERSON -> {
                val binding = ItemPersonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PersonItemViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid data type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RepositoryItemViewHolder ->
                holder.bind(repository = currentList[position] as SearchData.RepositoryModel)

            is PersonItemViewHolder ->
                holder.bind(person = currentList[position] as SearchData.PersonModel)
        }
    }

    override fun getItemViewType(position: Int): Int = when (currentList[position]) {
        is SearchData.RepositoryModel -> TYPE_REPOSITORY
        is SearchData.PersonModel -> TYPE_PERSON
    }

    private object SearchDataDiffUtil : DiffUtil.ItemCallback<SearchData>() {

        override fun areItemsTheSame(oldItem: SearchData, newItem: SearchData): Boolean = when {
            oldItem is SearchData.PersonModel && newItem is SearchData.PersonModel ->
                oldItem.userId == newItem.userId

            oldItem is SearchData.RepositoryModel && newItem is SearchData.RepositoryModel ->
                oldItem.repositoryId == newItem.repositoryId

            else -> false
        }

        override fun areContentsTheSame(oldItem: SearchData, newItem: SearchData): Boolean =
            oldItem == newItem
    }

}