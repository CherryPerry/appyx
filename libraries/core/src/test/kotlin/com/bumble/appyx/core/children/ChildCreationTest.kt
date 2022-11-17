package com.bumble.appyx.core.children

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.build
import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class ChildCreationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // region Keep mode

    @Test
    fun `parent node with keep mode creates initial child`() {
        val parent = Parent(keepMode = ChildEntry.KeepMode.KEEP)

        assertEquals(1, parent.children.value.size)
        assertNotNull(parent.child("initial"))
    }

    @Test
    fun `parent node with keep mode creates second child on add on screen child`() {
        val parent = Parent(keepMode = ChildEntry.KeepMode.KEEP)
        parent.testNavModel.add("second", TestNavModel.State.ON_SCREEN)

        assertEquals(2, parent.children.value.values.size)
        assertNotNull(parent.child("initial"))
        assertNotNull(parent.child("second"))
    }

    @Test
    fun `parent node with keep mode creates second child on add off screen child`() {
        val parent = Parent(keepMode = ChildEntry.KeepMode.KEEP)
        parent.testNavModel.add("second", TestNavModel.State.OFF_SCREEN)

        assertEquals(2, parent.children.value.values.size)
        assertNotNull(parent.child("initial"))
        assertNotNull(parent.child("second"))
    }

    @Test
    fun `parent node with keep mode removes second child on remove`() {
        val parent = Parent(keepMode = ChildEntry.KeepMode.KEEP)
        parent.testNavModel.add("second", TestNavModel.State.ON_SCREEN)
        parent.testNavModel.remove("second")

        assertEquals(1, parent.children.value.values.size)
        assertNotNull(parent.child("initial"))
    }

    @Test
    fun `parent node with keep mode keeps not on screen child`() {
        val parent = Parent(keepMode = ChildEntry.KeepMode.KEEP)
        parent.testNavModel.suspend("initial")

        assertEquals(1, parent.children.value.values.size)
        assertNotNull(parent.child("initial"))
    }

    @Test
    fun `parent node with keep mode reuses same node when becomes on screen`() {
        val parent = Parent(keepMode = ChildEntry.KeepMode.KEEP)
        parent.testNavModel.suspend("initial")
        val node = parent.child("initial")
        parent.testNavModel.unsuspend("initial")

        assertEquals(1, parent.children.value.values.size)
        assertEquals(node, parent.child("initial"))
    }

    // endregion

    // region Suspend mode

    @Test
    fun `parent node with suspend mode creates initial child`() {
        val parent = Parent(keepMode = ChildEntry.KeepMode.SUSPEND)

        assertEquals(1, parent.children.value.size)
        assertNotNull(parent.child("initial"))
    }

    @Test
    fun `parent node with suspend mode creates second child on add on screen child`() {
        val parent = Parent(keepMode = ChildEntry.KeepMode.SUSPEND)
        parent.testNavModel.add("second", TestNavModel.State.ON_SCREEN)

        assertEquals(2, parent.children.value.values.size)
        assertNotNull(parent.child("initial"))
        assertNotNull(parent.child("second"))
    }

    @Test
    fun `parent node with suspend mode does not create second child on add off screen child`() {
        val parent = Parent(keepMode = ChildEntry.KeepMode.SUSPEND)
        parent.testNavModel.add("second", TestNavModel.State.OFF_SCREEN)

        assertEquals(2, parent.children.value.values.size)
        assertNotNull(parent.child("initial"))
        assertNull(parent.child("second"))
    }

    @Test
    fun `parent node with suspend mode removes second child on remove`() {
        val parent = Parent(keepMode = ChildEntry.KeepMode.SUSPEND)
        parent.testNavModel.add("second", TestNavModel.State.ON_SCREEN)
        parent.testNavModel.remove("second")

        assertEquals(1, parent.children.value.values.size)
        assertNotNull(parent.child("initial"))
    }

    @Test
    fun `parent node with suspend mode suspends not on screen child`() {
        val parent = Parent(keepMode = ChildEntry.KeepMode.SUSPEND)
        parent.testNavModel.suspend("initial")

        assertEquals(1, parent.children.value.values.size)
        assertNull(parent.child("initial"))
    }

    @Test
    fun `parent node with suspend mode restores child when becomes on screen`() {
        val parent = Parent(keepMode = ChildEntry.KeepMode.SUSPEND)
        parent.testNavModel.suspend("initial")
        parent.testNavModel.unsuspend("initial")

        assertEquals(1, parent.children.value.values.size)
        assertEquals(true, parent.child("initial")?.hasRestoredState)
    }

    // endregion

    // region Setup

    private class TestNavModel : BaseNavModel<String, TestNavModel.State>(
        screenResolver = OnScreenStateResolver { it == State.ON_SCREEN },
        finalState = State.DESTROYED,
        savedStateMap = null,
    ) {
        enum class State { ON_SCREEN, OFF_SCREEN, DESTROYED }

        override val initialElements: NavElements<String, State> = listOf(
            NavElement(
                key = NavKey("initial"),
                fromState = State.ON_SCREEN,
                targetState = State.ON_SCREEN,
                operation = Operation.Noop(),
            )
        )

        fun add(navTarget: String, state: State) {
            updateState {
                it + NavElement(
                    key = NavKey(navTarget),
                    fromState = state,
                    targetState = state,
                    operation = Operation.Noop(),
                )
            }
        }

        fun remove(navTarget: String) {
            updateState {
                it.filterNot { it.key.navTarget == navTarget }
            }
        }

        fun suspend(navTarget: String) {
            updateState { list ->
                list.map {
                    if (it.key.navTarget == navTarget) {
                        it.transitionTo(State.OFF_SCREEN, Operation.Noop())
                    } else {
                        it
                    }
                }
            }
        }

        fun unsuspend(navTarget: String) {
            updateState { list ->
                list.map {
                    if (it.key.navTarget == navTarget) {
                        it.transitionTo(State.ON_SCREEN, Operation.Noop())
                    } else {
                        it
                    }
                }
            }
        }

    }

    private class Parent(
        keepMode: ChildEntry.KeepMode = ChildEntry.KeepMode.KEEP,
        buildContext: BuildContext = BuildContext.root(null),
        val testNavModel: TestNavModel = TestNavModel(),
    ) : ParentNode<String>(
        buildContext = buildContext,
        navModel = testNavModel,
        childKeepMode = keepMode,
    ) {
        init {
            build()
            manageTransitionsInTest()
        }

        override fun resolve(navTarget: String, buildContext: BuildContext): Node =
            Child(buildContext)

        fun key(navTarget: String): NavKey<String>? =
            children.value.keys.find { it.navTarget == navTarget }

        fun child(navTarget: String): Child? =
            children.value.values.find { it.key.navTarget == navTarget }?.nodeOrNull as Child?

    }

    private class Child(buildContext: BuildContext) : Node(buildContext) {
        val hasRestoredState: Boolean =
            buildContext.savedStateMap?.contains("test") == true

        override fun onSaveInstanceState(state: MutableSavedStateMap) {
            super.onSaveInstanceState(state)
            state["test"] = true
        }
    }

    // endregion

}
