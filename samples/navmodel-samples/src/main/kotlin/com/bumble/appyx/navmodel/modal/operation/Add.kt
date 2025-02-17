package com.bumble.appyx.navmodel.modal.operation

import com.bumble.appyx.navmodel.modal.Modal
import com.bumble.appyx.navmodel.modal.Modal.State.CREATED
import com.bumble.appyx.navmodel.modal.ModalElement
import com.bumble.appyx.navmodel.modal.ModalElements
import com.bumble.appyx.core.navigation.NavKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Add<T : Any>(
    private val element: @RawValue T
) : ModalOperation<T> {

    override fun isApplicable(elements: ModalElements<T>) = true

    override fun invoke(elements: ModalElements<T>): ModalElements<T> {
        return elements + ModalElement(
            key = NavKey(element),
            fromState = CREATED,
            targetState = CREATED,
            operation = this
        )
    }
}

fun <T : Any> Modal<T>.add(element: T) {
    accept(Add(element))
}
