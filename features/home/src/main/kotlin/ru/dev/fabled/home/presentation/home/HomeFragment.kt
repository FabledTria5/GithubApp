package ru.dev.fabled.home.presentation.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.dev.fabled.core.BaseFragment
import ru.dev.fabled.core.hideSoftKeyBoard
import ru.dev.fabled.core.repeatOnLifecycleWithScope
import ru.dev.fabled.core.use
import ru.dev.fabled.domain.model.Resource
import ru.dev.fabled.domain.model.SearchData
import ru.dev.fabled.home.databinding.FragmentHomeBinding
import ru.dev.fabled.home.presentation.home.adapters.HomeListAdapter

/**
 * Fragment to search users and repositories
 */
class HomeFragment :
    BaseFragment<HomeScreenContract.State, HomeScreenContract.Effect>() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()

    /**
     * Adapter for home list content
     */
    private val homeListAdapter: HomeListAdapter by lazy {
        HomeListAdapter { searchData ->
            when (searchData) {
                is SearchData.RepositoryModel -> {
                    Uri.parse(
                        "GitHubApp://RepositoryFragment/${searchData.repositoryName}/${searchData.repositoryOwner}"
                    ).let { uri ->
                        findNavController().navigate(uri)
                    }
                }

                is SearchData.PersonModel -> openUserPage(searchData.userLogin)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val (state, effect) = use(homeViewModel)

        observeState(state)
        collectEffects(effect)

        setupUi()
        initSearchField()
        setupListeners()
    }

    override fun observeState(state: StateFlow<HomeScreenContract.State>) =
        repeatOnLifecycleWithScope(state = Lifecycle.State.STARTED) {
            state.collectLatest {
                binding.searchButton.isEnabled =
                    it.canPerformSearch && it.listData !is Resource.Loading

                binding.searchEditText.isEnabled = it.listData !is Resource.Loading

                when (val data = it.listData) {
                    Resource.Loading -> {
                        hideError()
                        enableLoading()
                    }
                    is Resource.Success -> {
                        homeListAdapter.submitList(data.data)
                        hideLoading()
                        hideError()
                    }

                    is Resource.Error -> {
                        showError(data.error.message.orEmpty())
                    }

                    else -> Unit
                }
            }
        }

    override fun collectEffects(effect: SharedFlow<HomeScreenContract.Effect>) =
        repeatOnLifecycleWithScope(state = Lifecycle.State.STARTED) {
            effect.collectLatest {
                when (it) {
                    HomeScreenContract.Effect.DismissInput -> hideSoftKeyBoard()
                }
            }
        }

    /**
     * Sets initial params to views
     */
    private fun setupUi() = with(binding) {
        searchEditText.setText(homeViewModel.searchQuery)

        contentList.setHasFixedSize(true)
        contentList.adapter = homeListAdapter
        contentList.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun initSearchField() = with(binding) {
        searchEditText.doAfterTextChanged { editable ->
            homeViewModel.onEvent(
                HomeScreenContract.Event.ChangeSearchQuery(
                    editable.toString()
                )
            )
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                homeViewModel.onEvent(HomeScreenContract.Event.Search)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    /**
     * Sets listeners for interactive items
     */
    private fun setupListeners() = with(binding) {
        searchButton.setOnClickListener {
            homeViewModel.onEvent(HomeScreenContract.Event.Search)
        }

        retryButton.setOnClickListener {
            homeViewModel.onEvent(HomeScreenContract.Event.Search)
        }
    }

    /**
     * Shows loading indicator
     */
    private fun enableLoading() = with(binding) {
        contentList.isVisible = false

        progressIndicator.isVisible = true
    }

    /**
     * Hides loading indicator
     */
    private fun hideLoading() = with(binding) {
        progressIndicator.isVisible = false

        contentList.isVisible = true
    }

    /**
     * Shows error content
     */
    private fun showError(message: String) = with(binding) {
        progressIndicator.isVisible = false
        errorMessage.text = message

        contentList.isVisible = false
        errorGroup.isVisible = true
    }

    /**
     * Hides error content
     */
    private fun hideError() = with(binding) {
        errorMessage.text = ""

        errorGroup.isVisible = false
        contentList.isVisible = true
    }

    /**
     * Creates intent to open user page in browser
     */
    private fun openUserPage(userName: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("https://github.com/$userName")
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}