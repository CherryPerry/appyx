package com.bumble.appyx.core.integrationpoint

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultRegistry
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityBoundary
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.core.integrationpoint.permissionrequester.PermissionRequestBoundary
import com.bumble.appyx.core.integrationpoint.permissionrequester.PermissionRequester

open class ActivityIntegrationPoint(
    private val activity: ComponentActivity,
    savedInstanceState: Bundle?,
) : IntegrationPoint(savedInstanceState = savedInstanceState) {
    private val activityBoundary = ActivityBoundary(activity, requestCodeRegistry)
    private val permissionRequestBoundary = PermissionRequestBoundary(activity, requestCodeRegistry)

    @Deprecated("Use AndroidX API")
    override val activityStarter: ActivityStarter
        get() = activityBoundary

    @Deprecated("Use AndroidX API")
    override val permissionRequester: PermissionRequester
        get() = permissionRequestBoundary

    override val activityResultCaller: ActivityResultCaller
        get() = activity

    override val activityResultRegistry: ActivityResultRegistry
        get() = activity.activityResultRegistry

    @Deprecated("Is required only for old ActivityStarter API")
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activityBoundary.onActivityResult(requestCode, resultCode, data)
    }

    @Deprecated("Is required only for old PermissionRequester API")
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionRequestBoundary.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun handleUpNavigation() {
        if (!activity.onNavigateUp()) {
            activity.onBackPressed()
        }
    }

    override fun onRootFinished() {
        if (!activity.onNavigateUp()) {
            activity.finish()
        }
    }

    companion object {
        fun getIntegrationPoint(context: Context): IntegrationPoint {
            val activity = context.findActivity<ComponentActivity>()
            checkNotNull(activity) {
                "Could not find an activity from the context: $context"
            }

            val integrationPointProvider = activity as? IntegrationPointProvider ?: error(
                "Activity ${activity::class.qualifiedName} does not implement IntegrationPointProvider"
            )

            return integrationPointProvider.appyxIntegrationPoint
        }

        @Suppress("UNCHECKED_CAST")
        private fun <T : Activity> Context.findActivity(): T? {
            return if (this is Activity) {
                this as T?
            } else {
                val contextWrapper = this as ContextWrapper?
                val baseContext = contextWrapper?.baseContext
                requireNotNull(baseContext)
                baseContext.findActivity()
            }
        }
    }
}
