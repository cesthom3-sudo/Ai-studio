package com.thomas.aistudio.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thomas.aistudio.data.api.AppSettings
import com.thomas.aistudio.data.api.ApiClient
import com.thomas.aistudio.ui.common.PrimaryButton
import com.thomas.aistudio.ui.common.SectionTitle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var backendUrl by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("Not checked") }
    var isChecking by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        backendUrl = AppSettings.backendUrlFlow(context).first()
    }

    Column(Modifier.fillMaxSize().padding(top = 24.dp).verticalScroll(rememberScrollState())) {
        SectionTitle("Settings")
        Column(Modifier.padding(horizontal = 20.dp)) {
            Text("Backend", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = backendUrl,
                onValueChange = { backendUrl = it },
                label = { Text("Backend URL") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = {
                    scope.launch { AppSettings.setBackendUrl(context, backendUrl) }
                }) { Text("Save") }

                OutlinedButton(
                    enabled = !isChecking,
                    onClick = {
                        isChecking = true
                        scope.launch {
                            statusText = try {
                                val api = ApiClient.create(backendUrl)
                                val status = api.getProviderStatus()
                                buildString {
                                    append(if (status.demoMode) "DEMO MODE — " else "Connected — ")
                                    append("text:${status.text} image:${status.image} video:${status.video} audio:${status.audio} tts:${status.tts}")
                                }
                            } catch (e: Exception) {
                                "Could not reach backend"
                            } finally {
                                isChecking = false
                            }
                        }
                    }
                ) { Text(if (isChecking) "Checking..." else "Test connection") }
            }
            Spacer(Modifier.height(8.dp))
            Text(statusText, style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(24.dp))
            Text("About", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Text("Thomas AI Studio", modifier = Modifier.padding(top = 8.dp))
            Text("Created by Thomas T. Chingwaru", style = MaterialTheme.typography.bodySmall)
            Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(24.dp))
            Text("Privacy", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Text(
                "Media you generate or upload is sent to your configured backend and AI providers for processing. No API keys are stored on this device.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
