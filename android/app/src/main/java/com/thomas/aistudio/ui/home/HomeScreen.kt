package com.thomas.aistudio.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.thomas.aistudio.R
import com.thomas.aistudio.ui.common.EmptyState
import com.thomas.aistudio.ui.common.QuickActionCard
import com.thomas.aistudio.ui.common.SectionTitle

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(Modifier.fillMaxSize().padding(top = 24.dp)) {
        Column(Modifier.padding(horizontal = 20.dp)) {
            Text(
                stringResourceCompat("Thomas AI Studio"),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "AI Creative Studio",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Created by Thomas T. Chingwaru",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(16.dp))
        SectionTitle("Quick actions")

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(quickActions) { action ->
                QuickActionCard(
                    icon = action.icon,
                    label = action.label,
                    onClick = { navController.navigate(action.route) }
                )
            }
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                SectionTitle("Recent projects", modifier = Modifier.padding(horizontal = 0.dp))
            }
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                EmptyState("No projects yet. Start creating above.")
            }
        }
    }
}

private data class QuickAction(val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String, val route: String)

private val quickActions = listOf(
    QuickAction(Icons.Filled.Image, "Generate Image", "create/image-generate"),
    QuickAction(Icons.Filled.Edit, "Edit Image", "create/image-edit"),
    QuickAction(Icons.Filled.Movie, "Generate Video", "create/text-to-video"),
    QuickAction(Icons.Filled.Slideshow, "Image to Video", "create/image-to-video"),
    QuickAction(Icons.Filled.Campaign, "Create Advertisement", "create/ad-studio")
)

// Small helper kept local to avoid extra Context plumbing for a static string.
private fun stringResourceCompat(fallback: String): String = fallback
