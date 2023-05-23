package ru.dev.fabled.repository.repository

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.dev.fabled.core.BaseFragment
import ru.dev.fabled.core.repeatOnLifecycleWithScope
import ru.dev.fabled.core.use
import ru.dev.fabled.domain.model.RepositoryContent
import ru.dev.fabled.domain.model.Resource
import ru.dev.fabled.repository.databinding.FragmentRepositoryBinding
import ru.dev.fabled.repository.repository.adapters.RepositoryContentListAdapter

/**
 * This fragment shows repository content and can show files from it in web view
 */
class RepositoryFragment :
    BaseFragment<RepositoryScreenContract.State, RepositoryScreenContract.Effect>() {

    private var _binding: FragmentRepositoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RepositoryViewModel by viewModel()

    /**
     * Callback to send back button events to view model. When view model indicates, that can not
     * navigate up in the files system, callback will be disabled
     */
    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            viewModel.onEvent(RepositoryScreenContract.Event.NavigateBack)
        }
    }

    /**
     * Adapter for repository content
     */
    private val listAdapter: RepositoryContentListAdapter by lazy {
        RepositoryContentListAdapter { content ->
            when (content) {
                is RepositoryContent.File ->
                    viewModel.onEvent(
                        RepositoryScreenContract.Event.OpenFile(content.fileUrl)
                    )

                is RepositoryContent.Folder ->
                    viewModel.onEvent(RepositoryScreenContract.Event.NavigateToPath(content.path))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepositoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val (state, effect) = use(viewModel)

        observeState(state)
        collectEffects(effect)

        initUi()
        setupListeners()
    }

    override fun observeState(state: StateFlow<RepositoryScreenContract.State>) =
        repeatOnLifecycleWithScope(state = Lifecycle.State.STARTED) {
            state.collectLatest {
                onBackPressedCallback.isEnabled = it.canNavigateUp
                binding.filedWebView.isVisible = it.isWebEnabled

                when (val data = it.currentContent) {
                    is Resource.Error -> {
                        showError(data.error.message.orEmpty())
                        finishLoading()
                    }

                    Resource.Loading -> {
                        hideError()
                        showLoading()
                    }

                    is Resource.Success -> {
                        listAdapter.submitList(data.data) {
                            finishLoading()
                        }
                    }

                    else -> Unit
                }
            }
        }

    override fun collectEffects(effect: SharedFlow<RepositoryScreenContract.Effect>) =
        repeatOnLifecycleWithScope(state = Lifecycle.State.STARTED) {
            effect.collectLatest {
                when (it) {
                    is RepositoryScreenContract.Effect.OpenFile ->
                        binding.filedWebView.loadUrl(it.fileUrl)
                }
            }
        }

    /**
     * Sets initial parameters to views like adapters, decorations etc.
     */
    private fun initUi() = with(binding) {
        repositoryContentList.apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            adapter = listAdapter
        }

        filedWebView.settings.apply {
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        progressBar.max = 100
    }

    private fun setupListeners() = with(binding) {
        retryButton.setOnClickListener { viewModel.onEvent(ru.dev.fabled.repository.repository.RepositoryScreenContract.Event.Retry) }
    }

    /**
     * Creates animation for progress indicator. Max value of this animation is 70%. Animation will
     * end only if there is successful result of loading data
     */
    private fun showLoading() = with(binding) {
        val targetOffset = 70

        ObjectAnimator.ofInt(
            progressBar,
            "progress",
            0,
            targetOffset
        ).apply {
            duration = 2000
            interpolator = LinearInterpolator()

            addListener(
                onStart = {
                    progressBar.alpha = 1f
                }
            )
        }.also { animator ->
            animator.start()
        }
    }

    /**
     * Finishes animation for progress indicator. Calculates current progress and animates it to
     * 100%. After that hides view
     */
    private fun finishLoading() = with(binding) {
        val currentOffset = progressBar.progress

        ObjectAnimator.ofInt(
            progressBar,
            "progress",
            currentOffset,
            100
        ).apply {
            duration = 250
            interpolator = LinearInterpolator()

            addListener(
                onEnd = {
                    progressBar.animate()
                        .alpha(0f)
                        .setDuration(250)
                }
            )
        }.also { animator ->
            animator.start()
        }
    }

    /**
     * Hides list and shows error group
     */
    private fun showError(message: String) = with(binding) {
        repositoryContentList.isVisible = false

        errorMessage.text = message
        errorGroup.isVisible = true
    }

    /**
     * Hides error group and shows list
     */
    private fun hideError() = with(binding) {
        errorGroup.isVisible = false
        errorMessage.text = ""

        repositoryContentList.isVisible = true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}