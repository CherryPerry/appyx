package com.bumble.appyx.core.integrationpoint

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.LifecycleOwner

/**
 * Interface over [androidx.activity.result.ActivityResultRegistry] to make it easier to unit test it.
 */
interface ActivityResultRegistry {
    fun <I, O> register(
        key: String,
        lifecycleOwner: LifecycleOwner,
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>,
    ): ActivityResultLauncher<I>

    companion object {
        fun from(registry: androidx.activity.result.ActivityResultRegistry): ActivityResultRegistry =
            object : ActivityResultRegistry {
                override fun <I, O> register(
                    key: String,
                    lifecycleOwner: LifecycleOwner,
                    contract: ActivityResultContract<I, O>,
                    callback: ActivityResultCallback<O>
                ): ActivityResultLauncher<I> =
                    registry.register(key, lifecycleOwner, contract, callback)
            }
    }
}