package org.graph.spectral.ui.home.common

import org.graph.spectral.ui.home.graph.GraphEditMode

import androidx.compose.runtime.Composable
import org.graph.spectral.models.GraphGenerator
import org.graph.spectral.models.hypergraph.HypergraphGenerator
import org.graph.spectral.toolUI.CategoryOption
import org.graph.spectral.toolUI.CustomBottomSheet

@Composable
fun PresetGraphBottomSheet(
    graphGenerator: GraphGenerator,
    selectedGraph: String,
    onPresetSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    CustomBottomSheet(
        title = "选择经典预设图",
        categories = graphGenerator.presetGraphs.map { preset ->
            CategoryOption(
                id = preset.id,
                name = preset.name,
                isSelected = selectedGraph == preset.name
            )
        },
        onCategoryClick = onPresetSelected,
        onDismiss = onDismiss
    )
}

@Composable
fun PresetHypergraphBottomSheet(
    hypergraphGenerator: HypergraphGenerator,
    selectedHypergraph: String,
    onPresetSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    CustomBottomSheet(
        title = "选择经典预设超图",
        categories = hypergraphGenerator.presetHypergraphs.map { preset ->
            CategoryOption(
                id = preset.id,
                name = preset.name,
                isSelected = selectedHypergraph == preset.name
            )
        },
        onCategoryClick = onPresetSelected,
        onDismiss = onDismiss
    )
}

@Composable
fun EditModeBottomSheet(
    selectedEditMode: GraphEditMode,
    onEditModeSelected: (GraphEditMode) -> Unit,
    onDismiss: () -> Unit
) {
    CustomBottomSheet(
        title = "选择图编辑操作",
        categories = GraphEditMode.entries.map { mode ->
            CategoryOption(
                id = mode.id,
                name = mode.label,
                isSelected = selectedEditMode == mode
            )
        },
        onCategoryClick = { modeId -> onEditModeSelected(GraphEditMode.fromId(modeId)) },
        onDismiss = onDismiss
    )
}
