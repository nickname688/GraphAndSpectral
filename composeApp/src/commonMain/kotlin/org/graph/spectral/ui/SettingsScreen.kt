package org.graph.spectral.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.graph.spectral.toolUI.ButtonCard

@Composable
fun SettingsScreen(paddingValues: PaddingValues) {
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                ButtonCard(
                    modifier = Modifier.fillMaxWidth(),
                    name = "计算设置",
                    onNavigateToEdit = { /* 导航到计算设置页面 */ }
                )
            }

            item {
                ButtonCard(
                    modifier = Modifier.fillMaxWidth(),
                    name = "可视化设置",
                    onNavigateToEdit = { /* 导航到可视化设置页面 */ }
                )
            }

            item {
                ButtonCard(
                    modifier = Modifier.fillMaxWidth(),
                    name = "关于",
                    onNavigateToEdit = { /* 导航到关于页面 */ }
                )
            }
        }
    }
}