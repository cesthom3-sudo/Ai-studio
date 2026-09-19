package com.thomas.aistudio.ui.create

import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.thomas.aistudio.data.api.AppSettings
import com.thomas.aistudio.data.api.ApiClient
import com.thomas.aistudio.data.model.VideoFromImageRequest
import com.thomas.aistudio.ui.common.PrimaryButton
import com.thomas.aistudio.ui.common.SectionTitle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun ImageToVideoScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var uri by remember { mutableStateOf<Uri?>(null) }
    var motionPrompt by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(5) }
    var aspect by remember { mutableStateOf("9:16") }
    var status by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var resultUrl by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri = it }

    Column(Modifier.fillMaxSize().padding(top = 24.dp).verticalScroll(rememberScrollState())) {
        SectionTitle("Image to Video")
        Column(Modifier.padding(horizontal = 20.dp)) {
            OutlinedButton(onClick = { pickImage.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Text(if (uri == null) "Upload image" else "Change image")
            }
            uri?.let {
                Spacer(Modifier.height(12.dp))
                AsyncImage(model = it, contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = motionPrompt,
                onValueChange = { motionPrompt = it },
                label = { Text("Motion prompt") },
                placeholder = { Text("e.g. Cinematic product reveal with slow camera movement") },
                minLines = 2,
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
            if (status.isNotBlank()) { Text(status); Spacer(Modifier.height(8.dp)) }

            PrimaryButton(
                text = if (isLoading) "Working..." else "Generate",
                enabled = uri != null && motionPrompt.isNotBlank() && !isLoading,
                onClick = {
                    val imageUri = uri ?: return@PrimaryButton
                    errorText = null
                    isLoading = true
                    scope.launch {
                        try {
                            status = "Preparing..."
                            val bytes = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                                ?: throw IllegalStateException("Could not read image")
                            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)

                            val baseUrl = AppSettings.backendUrlFlow(context).first()
                            val api = ApiClient.create(baseUrl)
                            status = "Generating..."
                            val job = api.videoFromImage(
                                VideoFromImageRequest(base64, motionPrompt, duration, aspect)
                            )
                            var jobStatus = api.getJob(job.jobId)
                            while (jobStatus.status != "completed" && jobStatus.status != "failed") {
                                status = "Processing..."
                                kotlinx.coroutines.delay(2000)
                                jobStatus = api.getJob(job.jobId)
                            }
                            if (jobStatus.status == "completed") {
                                status = "Complete."
                                resultUrl = jobStatus.resultUrl
                            } else {
                                errorText = jobStatus.error ?: "Generation failed"
                            }
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
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { }) { Text("Preview") }
                    OutlinedButton(onClick = { }) { Text("Download") }
                    OutlinedButton(onClick = { }) { Text("Share") }
                }
            }
        }
    }
}
