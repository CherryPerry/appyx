package com.bumble.appyx.core.modality

import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.utils.customisations.NodeCustomisation
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl
import java.util.UUID

data class BuildContext(
    val ancestryInfo: AncestryInfo,
    val savedStateMap: SavedStateMap?,
    val customisations: NodeCustomisationDirectory,
    // Temporary nullable while migrating to the new approach.
    val integrationPoint: IntegrationPoint?,
) {

    @Deprecated("Provide IntegrationPoint via constructor")
    constructor(
        ancestryInfo: AncestryInfo,
        savedStateMap: SavedStateMap?,
        customisations: NodeCustomisationDirectory,
    ) : this(ancestryInfo, savedStateMap, customisations, null)

    companion object {
        private const val IDENTIFIER_KEY = "build.context.identifier"

        @Deprecated("Provide IntegrationPoint via constructor")
        fun root(
            savedStateMap: SavedStateMap?,
            customisations: NodeCustomisationDirectory = NodeCustomisationDirectoryImpl()
        ): BuildContext =
            BuildContext(
                ancestryInfo = AncestryInfo.Root,
                savedStateMap = savedStateMap,
                customisations = customisations,
            )

        fun root(
            savedStateMap: SavedStateMap?,
            integrationPoint: IntegrationPoint,
            customisations: NodeCustomisationDirectory = NodeCustomisationDirectoryImpl(),
        ): BuildContext =
            BuildContext(
                ancestryInfo = AncestryInfo.Root,
                savedStateMap = savedStateMap,
                customisations = customisations,
                integrationPoint = integrationPoint,
            )
    }

    val identifier: String by lazy {
        if (savedStateMap == null) {
            UUID.randomUUID().toString()
        } else {
            savedStateMap[IDENTIFIER_KEY] as String?
                ?: error("onSaveInstanceState() was not called")
        }
    }

    fun <T : NodeCustomisation> getOrDefault(defaultCustomisation: T): T =
        customisations.getRecursivelyOrDefault(defaultCustomisation)

    fun onSaveInstanceState(state: MutableSavedStateMap) {
        state[IDENTIFIER_KEY] = identifier
    }
}
