package com.bumble.appyx.core.node

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.AppyxTestScenario
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.testing.unit.common.util.TestIntegrationPoint
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RegisterForActivityResultTest {

    private val testIntegrationPoint = TestIntegrationPoint()

    @get:Rule
    val rule = AppyxTestScenario { buildContext ->
        TestNode(
            buildContext.copy(
                integrationPoint = testIntegrationPoint,
            ),
        )
    }

    @Test
    fun propagates_result() {
        val uri = Uri.parse("https://test.com")
        rule.start()
        rule.activityScenario.moveToState(Lifecycle.State.RESUMED)

        rule.node.launchContract()
        testIntegrationPoint.activityResultRegistry.dispatchResult(
            ActivityResultContracts.OpenDocument::class,
            uri,
        )

        assertEquals(uri, rule.node.contractResult)
    }

    @Test
    fun survives_configuration_change() {
        val uri = Uri.parse("https://test.com")
        rule.start()
        rule.activityScenario.moveToState(Lifecycle.State.RESUMED)

        rule.node.launchContract()
        rule.activityScenario.recreate()
        testIntegrationPoint.activityResultRegistry.dispatchResult(
            ActivityResultContracts.OpenDocument::class,
            uri,
        )

        assertEquals(uri, rule.node.contractResult)
    }

    inner class TestNode(buildContext: BuildContext) : Node(
        buildContext = buildContext,
    ) {
        var contractResult: Uri? = null
        private val contractLauncher = registerActivityForResult(
            ActivityResultContracts.OpenDocument(),
            { contractResult = it }
        )

        fun launchContract() {
            contractLauncher.launch(arrayOf("image/*"))
        }
    }

}