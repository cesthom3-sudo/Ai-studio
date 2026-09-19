package com.thomas.aistudio.ui.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.thomas.aistudio.data.api.AppSettings
import com.thomas.aistudio.data.api.ApiClient
import com.thomas.aistudio.data.model.ProjectDto
import com.thomas.aistudio.ui.common.EmptyState
import com.thomas.aistudio.ui.common.SectionTitle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun ProjectsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var projects by remember { mutableStateOf<List<ProjectDto>>(emptyList()) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val baseUrl = AppSettings.backendUrlFlow(context).first()
                val api = ApiClient.create(baseUrl)
                projects = api.listProjects()
            } catch (e: Exception) {
                errorText = "Could not load projects. Check backend connection in Settings."
            } finally {
                isLoading = false
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(top = 24.dp)) {
        SectionTitle("Projects")
        when {
            isLoading -> EmptyState("Loading projects...")
            errorText != null -> EmptyState(errorText!!)
            projects.isEmpty() -> EmptyState("No saved projects yet. Create something first.")
            else -> LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
                items(projects) { project ->
                    ListItem(
                        headlineContent = { Text(project.name) },
                        supportingContent = { Text("${project.type} · ${project.createdAt}") }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
