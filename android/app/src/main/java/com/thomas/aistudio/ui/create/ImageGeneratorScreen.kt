package com.thomas.aistudio.ui.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.thomas.aistudio.data.api.AppSettings
import com.thomas.aistudio.data.api.ApiClient
import com.thomas.aistudio.data.model.ImageGenerateRequest
import com.thomas.aistudio.ui.common.PrimaryButton
import com.thomas.aistudio.ui.common.SectionTitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val presets = listOf(
    "Cinematic",
    "Photorealistic",
    "Product photography",
    "Advertisement",
    "Poster",
    "Illustration",
    "3D",
    "Minimalist",
    "Futuristic"
)

private val aspectRatios = listOf(
    "1:1",
    "9:16",
    "16:9",
    "4:5"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageGeneratorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var prompt by remember { mutableStateOf("") }

    // Explicit defaults instead of presets.first() / aspectRatios.first()
    var selectedPreset by remember { mutableStateOf("Cinematic") }
    var selectedAspect by remember { mutableStateOf("1:1") }

    var variations by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var resultUrl by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SectionTitle("Image Generator")

        Column(
            Modifier.padding(horizontal = 20.dp)
        ) {

            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Describe the image") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Style preset",
                style = MaterialTheme.typography.labelLarge
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(presets) { preset ->
                    FilterChip(
                        selected = preset == selectedPreset,
                        onClick = {
                            selectedPreset = preset
                        },
                        label = {
                            Text(preset)
                        }
                    )
                }
            }

            Text(
                "Aspect ratio",
                style = MaterialTheme.typography.labelLarge
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(aspectRatios) { ratio ->
                    FilterChip(
                        selected = ratio == selectedAspect,
                        onClick = {
                            selectedAspect = ratio
                        },
                        label = {
                            Text(ratio)
                        }
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Variations: $variations",
                    modifier = Modifier.weight(1f)
                )

                IconButtonRow(
                    current = variations,
                    onChange = {
                        variations = it
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            errorText?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.height(8.dp))
            }

            PrimaryButton(
                text = if (isLoading) {
                    "Generating..."
                } else {
                    "Generate"
                },
                enabled = prompt.isNotBlank() && !isLoading,
                onClick = {
                    errorText = null
                    resultUrl = null
                    isLoading = true

                    scope.launch {
                        try {
                            val base = first(
                                AppSettings.backendUrlFlow(context)
                            )

                            val api = ApiClient.create(base)

                            val job = api.generateImage(
                                ImageGenerateRequest(
                                    prompt = prompt,
                                    aspectRatio = selectedAspect,
                                    style = selectedPreset.lowercase(),
                                    variations = variations
                                )
                            )

                            var status = api.getJob(job.jobId)

                            while (
                                status.status != "completed" &&
                                status.status != "failed"
                            ) {
                                delay(2000)
                                status = api.getJob(job.jobId)
                            }

                            if (status.status == "completed") {
                                resultUrl = status.resultUrl
                            } else {
                                errorText =
                                    status.error ?: "Generation failed"
                            }

                        } catch (e: Exception) {
                            errorText =
                                e.message ?: "Something went wrong"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            )

            Spacer(Modifier.height(20.dp))

            resultUrl?.let { url ->

                AsyncImage(
                    model = url,
                    contentDescription = "Generated image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .padding(bottom = 12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            // Regenerate can be connected to the same generation
                            // request in a later iteration.
                        }
                    ) {
                        Text("Regenerate")
                    }

                    OutlinedButton(
                        onClick = {
                            // Save to project
                        }
                    ) {
                        Text("Save")
                    }

                    OutlinedButton(
                        onClick = {
                            // Download
                        }
                    ) {
                        Text("Download")
                    }

                    OutlinedButton(
                        onClick = {
                            // Share
                        }
                    ) {
                        Text("Share")
                    }
                }
            }
        }
    }
}

@Composable
private fun IconButtonRow(
    current: Int,
    onChange: (Int) -> Unit
) {
    Row {
        OutlinedButton(
            onClick = {
                if (current > 1) {
                    onChange(current - 1)
                }
            }
        ) {
            Text("-")
        }

        Spacer(Modifier.width(8.dp))

        OutlinedButton(
            onClick = {
                if (current < 4) {
                    onChange(current + 1)
                }
            }
        ) {
            Text("+")
        }
    }
}
