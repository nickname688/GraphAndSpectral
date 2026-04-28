package org.graph.spectral.toolUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 分类选项数据类
 */
data class CategoryOption(
    val id: String,
    val name: String,
    val isSelected: Boolean
)

/**
 * 核心分类 BottomSheet
 * 纯粹的展示组件，所有数据和逻辑通过参数传入
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    title: String = "选择核心分类",
    categories: List<CategoryOption>,
    onCategoryClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            HorizontalDivider()
            
            // 分类列表 - 完全由外部数据驱动
            categories.forEach { category ->
                MenuItem(
                    text = category.name,
                    isSelected = category.isSelected,
                    onClick = { onCategoryClick(category.id) }
                )
            }
        }
    }
}
