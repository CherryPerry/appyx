package com.bumble.appyx.core.node

import androidx.activity.result.contract.ActivityResultContracts
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import com.bumble.appyx.testing.unit.common.helper.nodeTestHelper
import com.bumble.appyx.testing.unit.common.util.TestIntegrationPoint
import com.bumble.appyx.testing.unit.common.util.TestUpNavigationHandler
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

// TODO: Make it work
class ActivityResultNodeTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testIntegrationPoint = TestIntegrationPoint(TestUpNavigationHandler())

    @Test
    fun `launch provides data`() = runTest {
        TestNode().nodeTestHelper().moveToStateAndCheck(Lifecycle.State.STARTED) {
            it.launchContract()

            testIntegrationPoint.activityResultRegistry.dispatchResult(
                ActivityResultContracts.RequestPermission::class, true,
            )

            assertEquals(true, it.contractResult)
        }
    }

    private inner class TestNode : Node(
        buildContext = BuildContext.root(
            savedStateMap = null,
            integrationPoint = testIntegrationPoint,
        ),
    ) {
        var contractResult: Boolean? = null
        private val contractLauncher = registerActivityForResult(
            ActivityResultContracts.RequestPermission(),
            { result -> contractResult = result }
        )

        fun launchContract() {
            contractLauncher.launch("")
        }
    }
}