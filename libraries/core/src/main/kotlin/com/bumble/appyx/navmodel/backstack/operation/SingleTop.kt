package com.bumble.appyx.navmodel.backstack.operation

import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackElement
import com.bumble.appyx.navmodel.backstack.BackStackElements
import com.bumble.appyx.navmodel.backstack.active
import com.bumble.appyx.navmodel.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.navmodel.backstack.BackStack.State.CREATED
import com.bumble.appyx.navmodel.backstack.activeElement
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Operation:
 *
 * [A, B, C, D] + SingleTop(B) = [A, B]          // of same type and equals, acts as n * Pop
 * [A, B, C, D] + SingleTop(B') = [A, B']        // of same type but not equals, acts as n * Pop + Replace
 * [A, B, C, D] + SingleTop(E) = [A, B, C, D, E] // not found, acts as Push
 */
sealed class SingleTop<T : Any> : BackStackOperation<T> {

    @Parcelize
    class SingleTopReactivateBackStackOperation<T : Any>(
        private val element: @RawValue T,
        private val position: Int
    ) : SingleTop<T>() {

        override fun isApplicable(elements: BackStackElements<T>): Boolean =
            element != elements.activeElement

        override fun invoke(
            elements: BackStackElements<T>
        ): BackStackElements<T> {
            val current = elements.active
            requireNotNull(current)

            val newElements = elements
                .dropLast(elements.size - position - 1)

            return newElements.transitionToIndexed(ACTIVE) { index, _ ->
                index == newElements.lastIndex
            } + current.transitionTo(
                newTargetState = BackStack.State.DESTROYED,
                operation = this
            )
        }

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

        override fun hashCode(): Int = this.javaClass.hashCode()
    }

    @Parcelize
    class SingleTopReplaceBackStackOperation<T : Any>(
        private val element: @RawValue T,
        private val position: Int,
    ) : SingleTop<T>() {

        override fun isApplicable(elements: BackStackElements<T>): Boolean = true

        override fun invoke(
            elements: BackStackElements<T>
        ): BackStackElements<T> {
            val current = elements.active
            requireNotNull(current)

            val newElements = elements.dropLast(elements.size - position)

            return newElements + current.transitionTo(
                newTargetState = BackStack.State.DESTROYED,
                operation = this
            ) + BackStackElement(
                key = NavKey(element),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = this,
            )
        }

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

        override fun hashCode(): Int = this.javaClass.hashCode()
    }

    companion object {

        fun <T : Any> init(
            element: T,
            elements: BackStackElements<T>
        ): BackStackOperation<T> {
            val targetClass = element.javaClass
            val lastIndexOfSameClass =
                elements.indexOfLast { targetClass.isInstance(it.key.navTarget) }

            return if (lastIndexOfSameClass == -1) {
                Push(element)
            } else {
                if (elements[lastIndexOfSameClass].key.navTarget == element) {
                    SingleTopReactivateBackStackOperation(
                        element,
                        lastIndexOfSameClass
                    )
                } else {
                    SingleTopReplaceBackStackOperation(
                        element,
                        lastIndexOfSameClass
                    )
                }
            }
        }
    }
}

fun <T : Any> BackStack<T>.singleTop(element: T) {
    accept(SingleTop.init(element, elements.value))
}
