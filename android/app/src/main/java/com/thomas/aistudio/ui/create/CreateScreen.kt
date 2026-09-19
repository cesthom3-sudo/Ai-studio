package com.thomas.aistudio.ui.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.thomas.aistudio.ui.common.SectionTitle

private data class CreateOption(val icon: ImageVector, val title: String, val subtitle: String, val route: String)

private val options = listOf(
    CreateOption(Icons.Filled.Image, "Image Generator", "Text prompt to image", "create/image-generate"),
    CreateOption(Icons.Filled.Edit, "Image Editor", "Edit photos with natural language", "create/image-edit"),
    CreateOption(Icons.Filled.Slideshow, "Image to Video", "Animate a still image", "create/image-to-video"),
    CreateOption(Icons.Filled.Movie, "Text to Video", "Describe a video, we generate it", "create/text-to-video"),
    CreateOption(Icons.Filled.Campaign, "Ad Studio", "Full automatic advertisement pipeline", "create/ad-studio")
)

@Composable
fun CreateScreen(navController: NavHostController) {
    Column(Modifier.fillMaxSize().padding(top = 24.dp)) {
        SectionTitle("Create")
        LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
            items(options) { option ->
                ListItem(
                    headlineContent = { Text(option.title, fontWeight = FontWeight.Medium) },
                    supportingContent = { Text(option.subtitle) },
                    leadingContent = { Icon(option.icon, contentDescription = option.title) },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { navController.navigate(option.route) }
                )
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}
