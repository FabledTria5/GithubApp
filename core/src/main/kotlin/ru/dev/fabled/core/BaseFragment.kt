package ru.dev.fabled.core

import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Base class for all fragments
 */
abstract class BaseFragment<STATE, EFFECT> : Fragment() {
    /**
     * Collects state provided by viewModel
     */
    abstract fun observeState(state: StateFlow<STATE>)

    /**
     * Collects effects from viewModel. This effects represents special events, when user should
     * be notified by using Toasts, SnackBards and e.g.
     */
    abstract fun collectEffects(effect: SharedFlow<EFFECT>)

}