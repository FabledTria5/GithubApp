package ru.dev.fabled.core

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Defines contract for screen ViewModel
 *
 * @property state Screen state that defines UI behavior
 * @property effect UI effects like toasts, snackBars...
 */
interface ScreenContract<STATE, EVENT, EFFECT> {
    val state: StateFlow<STATE>
    val effect: SharedFlow<EFFECT>

    /**
     * Takes events from views
     */
    fun onEvent(event: EVENT)
}

/**
 * Returns members of [StateDispatchEffect] associated with given viewModel
 *
 * @param viewModel ViewModel that implements ScreenContract
 */
inline fun <reified STATE, EVENT, EFFECT> use(
    viewModel: ScreenContract<STATE, EVENT, EFFECT>
): StateDispatchEffect<StateFlow<STATE>, EFFECT> {
    return StateDispatchEffect(state = viewModel.state, effectFlow = viewModel.effect)
}

data class StateDispatchEffect<STATE, EFFECT>(
    val state: STATE,
    val effectFlow: SharedFlow<EFFECT>
)