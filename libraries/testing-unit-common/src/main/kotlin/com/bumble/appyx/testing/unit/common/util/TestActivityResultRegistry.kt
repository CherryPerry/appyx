package com.bumble.appyx.testing.unit.common.util

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.LifecycleOwner
import com.bumble.appyx.core.integrationpoint.ActivityResultRegistry
import kotlin.reflect.KClass

/**
 * [ActivityResultRegistry] to use in tests.
 *
 * Launch the contract and use [dispatchResult] to provide the result.
 *
 * Does not support multiple launches of the same contract.
 * The result will be delivered to all of them, instead of a particular one.
 * Unfortunately, it is impossible to distinct them based on `requestCode`.
 */
class TestActivityResultRegistry : ActivityResultRegistry {
    private val waitingForDispatch = ArrayList<WaitingForDispatch>()
    private val platformDelegate = object : androidx.activity.result.ActivityResultRegistry() {
        override fun <I : Any?, O : Any?> onLaunch(
            requestCode: Int,
            contract: ActivityResultContract<I, O>,
            input: I,
            options: ActivityOptionsCompat?
        ) {
            waitingForDispatch += WaitingForDispatch(requestCode, contract::class)
        }
    }

    override fun <I, O> register(
        key: String,
        lifecycleOwner: LifecycleOwner,
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>
    ): ActivityResultLauncher<I> =
        platformDelegate.register(key, lifecycleOwner, contract, callback)

    fun <I : Any?, O : Any?> dispatchResult(
        contractClass: KClass<out ActivityResultContract<I, O>>,
        result: O,
    ) {
        waitingForDispatch
            .filter { it.contractClass == contractClass }
            .also { if (it.isEmpty()) error("No launches with the provided contract.") }
            .forEach {
                waitingForDispatch -= it
                platformDelegate.dispatchResult(it.requestCode, result)
            }
    }

    class WaitingForDispatch(
        val requestCode: Int,
        val contractClass: KClass<*>,
    )
}