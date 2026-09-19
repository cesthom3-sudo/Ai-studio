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
import com.thomas.aistudio.data.model.ImageEditRequest
import com.thomas.aistudio.ui.common.PrimaryButton
import com.thomas.aistudio.ui.common.SectionTitle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val quickEdits = listOf(
    "Remove the background.",
    "Make this a professional product photograph.",
    "Change the background to a modern city.",
    "Improve the lighting.",
    "Remove the object in the background.",
    "Make this look cinematic."
)

@Composable
fun ImageEditorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var originalUri by remember { mutableStateOf<Uri?>(null) }
    var instruction by remember { mutableStateOf("") }
    var resultUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        originalUri = uri
        resultUrl = null
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SectionTitle("Image Editor")

        Column(Modifier.padding(horizontal = 20.dp)) {
            OutlinedButton(onClick = { pickImage.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Text(if (originalUri == null) "Select image from device" else "Change image")
            }

            Spacer(Modifier.height(12.dp))

            originalUri?.let { uri ->
                Text("Original", style = MaterialTheme.typography.labelLarge)
                AsyncImage(model = uri, contentDescription = "Original", modifier = Modifier.fillMaxWidth().height(220.dp))
                Spacer(Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = instruction,
                onValueChange = { instruction = it },
                label = { Text("Describe the edit") },
                placeholder = { Text("e.g. Remove the background") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            quickEdits.forEach { suggestion ->
                AssistChip(onClick = { instruction = suggestion }, label = { Text(suggestion) }, modifier = Modifier.padding(vertical = 2.dp))
            }

            Spacer(Modifier.height(16.dp))

            errorText?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            PrimaryButton(
                text = if (isLoading) "Editing..." else "Apply Edit",
                enabled = originalUri != null && instruction.isNotBlank() && !isLoading,
                onClick = {
                    val uri = originalUri ?: return@PrimaryButton
                    errorText = null
                    isLoading = true
                    scope.launch {
                        try {
                            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                                ?: throw IllegalStateException("Could not read image")
                            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)

                            val baseUrl = AppSettings.backendUrlFlow(context).first()
                            val api = ApiClient.create(baseUrl)
                            val job = api.editImage(ImageEditRequest(imageBase64 = base64, instruction = instruction))

                            var status = api.getJob(job.jobId)
                            while (status.status != "completed" && status.status != "failed") {
                                kotlinx.coroutines.delay(2000)
                                status = api.getJob(job.jobId)
                            }
                            if (status.status == "completed") resultUrl = status.resultUrl
                            else errorText = status.error ?: "Edit failed"
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
                Text("Edited result", style = MaterialTheme.typography.labelLarge)
                AsyncImage(model = url, contentDescription = "Edited", modifier = Modifier.fillMaxWidth().height(220.dp))
            }
        }
    }
}
