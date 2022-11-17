package com.bumble.appyx.navmodel.spotlight.operation

import com.bumble.appyx.navmodel.spotlight.Spotlight.State.ACTIVE
import com.bumble.appyx.navmodel.spotlight.Spotlight.State.INACTIVE_AFTER
import com.bumble.appyx.navmodel.spotlight.Spotlight.State.INACTIVE_BEFORE
import com.bumble.appyx.navmodel.spotlight.operation.NavTarget.NavTarget1
import com.bumble.appyx.navmodel.spotlight.spotlightElement
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class PreviousTest {

    @Test
    fun `Given first element is in transition When previous called Then operation is not applicable`() {
        val firstElement = spotlightElement<NavTarget>(
            element = NavTarget1,
            fromState = INACTIVE_AFTER,
            targetState = INACTIVE_BEFORE,
        )
        val lastElement = spotlightElement<NavTarget>(
            element = NavTarget1,
            fromState = ACTIVE,
            targetState = INACTIVE_BEFORE,
        )
        val elements = listOf(firstElement, lastElement)
        val operation = Previous<NavTarget>()

        val applicable = operation.isApplicable(elements)

        assertFalse(applicable)
    }

    @Test
    fun `Given first element is not in transition When previous called Then operation is applicable`() {
        val firstElement = spotlightElement<NavTarget>(
            element = NavTarget1,
            fromState = ACTIVE,
            targetState = ACTIVE,
        )
        val lastElement = spotlightElement<NavTarget>(
            element = NavTarget1,
            fromState = INACTIVE_BEFORE,
            targetState = INACTIVE_BEFORE,
        )
        val elements = listOf(firstElement, lastElement)
        val operation = Previous<NavTarget>()

        val applicable = operation.isApplicable(elements)

        assertTrue(applicable)
    }

}
