package com.thomas.aistudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thomas.aistudio.ui.create.AdStudioScreen
import com.thomas.aistudio.ui.create.CreateScreen
import com.thomas.aistudio.ui.create.ImageEditorScreen
import com.thomas.aistudio.ui.create.ImageGeneratorScreen
import com.thomas.aistudio.ui.create.ImageToVideoScreen
import com.thomas.aistudio.ui.create.TextToVideoScreen
import com.thomas.aistudio.ui.home.HomeScreen
import com.thomas.aistudio.ui.projects.ProjectsScreen
import com.thomas.aistudio.ui.settings.SettingsScreen
import com.thomas.aistudio.ui.theme.ThomasAiStudioTheme

sealed class Dest(val route: String, val label: String) {
    object Home : Dest("home", "Home")
    object Create : Dest("create", "Create")
    object Projects : Dest("projects", "Projects")
    object Settings : Dest("settings", "Settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThomasAiStudioTheme {
                StudioApp()
            }
        }
    }
}

@Composable
fun StudioApp() {
    val navController = rememberNavController()
    val bottomItems = listOf(Dest.Home, Dest.Create, Dest.Projects, Dest.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                bottomItems.forEach { dest ->
                    val icon = when (dest) {
                        Dest.Home -> Icons.Filled.Home
                        Dest.Create -> Icons.Filled.AddCircle
                        Dest.Projects -> Icons.Filled.Folder
                        Dest.Settings -> Icons.Filled.Settings
                    }
                    val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = dest.label) },
                        label = { Text(dest.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Dest.Home.route) { HomeScreen(navController) }
            composable(Dest.Create.route) { CreateScreen(navController) }
            composable(Dest.Projects.route) { ProjectsScreen() }
            composable(Dest.Settings.route) { SettingsScreen() }

            composable("create/image-generate") { ImageGeneratorScreen() }
            composable("create/image-edit") { ImageEditorScreen() }
            composable("create/image-to-video") { ImageToVideoScreen() }
            composable("create/text-to-video") { TextToVideoScreen() }
            composable("create/ad-studio") { AdStudioScreen() }
        }
    }
}
