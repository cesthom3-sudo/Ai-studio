package com.thomas.aistudio.ui.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.thomas.aistudio.data.api.AppSettings
import com.thomas.aistudio.data.api.ApiClient
import com.thomas.aistudio.data.model.VideoGenerateRequest
import com.thomas.aistudio.ui.common.PrimaryButton
import com.thomas.aistudio.ui.common.SectionTitle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun TextToVideoScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var prompt by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(5) }
    var aspect by remember { mutableStateOf("9:16") }
    var style by remember { mutableStateOf("cinematic") }
    var isLoading by remember { mutableStateOf(false) }
    var resultUrl by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(top = 24.dp).verticalScroll(rememberScrollState())) {
        SectionTitle("Text to Video")
        Column(Modifier.padding(horizontal = 20.dp)) {
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Describe the video") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Row {
                listOf(3, 5, 8, 10).forEach { d ->
                    FilterChip(selected = duration == d, onClick = { duration = d }, label = { Text("${d}s") }, modifier = Modifier.padding(end = 8.dp))
                }
            }
            Row {
                listOf("9:16", "1:1", "16:9").forEach { a ->
                    FilterChip(selected = aspect == a, onClick = { aspect = a }, label = { Text(a) }, modifier = Modifier.padding(end = 8.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            errorText?.let { Text(it, color = MaterialTheme.colorScheme.error); Spacer(Modifier.height(8.dp)) }

            PrimaryButton(
                text = if (isLoading) "Generating..." else "Generate",
                enabled = prompt.isNotBlank() && !isLoading,
                onClick = {
                    errorText = null
                    isLoading = true
                    scope.launch {
                        try {
                            val baseUrl = AppSettings.backendUrlFlow(context).first()
                            val api = ApiClient.create(baseUrl)
                            val job = api.generateVideo(VideoGenerateRequest(prompt, duration, aspect, style))
                            var jobStatus = api.getJob(job.jobId)
                            while (jobStatus.status != "completed" && jobStatus.status != "failed") {
                                kotlinx.coroutines.delay(2000)
                                jobStatus = api.getJob(job.jobId)
                            }
                            if (jobStatus.status == "completed") resultUrl = jobStatus.resultUrl
                            else errorText = jobStatus.error ?: "Generation failed"
                        } catch (e: Exception) {
                            errorText = e.message ?: "Something went wrong"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            )

            resultUrl?.let { url ->
                Spacer(Modifier.height(16.dp))
                Text("Result video: $url", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
