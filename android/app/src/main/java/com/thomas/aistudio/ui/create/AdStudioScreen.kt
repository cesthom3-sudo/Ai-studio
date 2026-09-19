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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.thomas.aistudio.data.api.AppSettings
import com.thomas.aistudio.data.api.ApiClient
import com.thomas.aistudio.data.model.AdCreateRequest
import com.thomas.aistudio.data.model.StoryboardScene
import com.thomas.aistudio.ui.common.PrimaryButton
import com.thomas.aistudio.ui.common.SectionTitle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun AdStudioScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var productImage by remember { mutableStateOf<Uri?>(null) }
    var productName by remember { mutableStateOf("") }
    var objective by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("") }
    var tone by remember { mutableStateOf("Energetic") }
    var cta by remember { mutableStateOf("Shop now") }
    var aspect by remember { mutableStateOf("9:16") }
    var duration by remember { mutableStateOf(15) }
    var freeform by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var storyboard by remember { mutableStateOf<List<StoryboardScene>?>(null) }
    var resultUrl by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { productImage = it }

    Column(Modifier.fillMaxSize().padding(top = 24.dp).verticalScroll(rememberScrollState())) {
        SectionTitle("Ad Studio")
        Column(Modifier.padding(horizontal = 20.dp)) {
            OutlinedButton(onClick = { pickImage.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Text(if (productImage == null) "Upload product image" else "Change product image")
            }
            productImage?.let {
                Spacer(Modifier.height(12.dp))
                AsyncImage(model = it, contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp))
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(freeform, { freeform = it }, label = { Text("Or just describe it") }, placeholder = { Text("Create an advert for this product.") }, minLines = 2, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(12.dp))
            Text("Or fill in details", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(productName, { productName = it }, label = { Text("Product / brand name") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            OutlinedTextField(objective, { objective = it }, label = { Text("Advertising objective") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            OutlinedTextField(audience, { audience = it }, label = { Text("Target audience") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            OutlinedTextField(tone, { tone = it }, label = { Text("Tone") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            OutlinedTextField(cta, { cta = it }, label = { Text("Call to action") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))

            Spacer(Modifier.height(12.dp))
            Text("Format", style = MaterialTheme.typography.labelLarge)
            Row {
                listOf("9:16" to "TikTok/Reels/Shorts", "1:1" to "Square", "16:9" to "YouTube").forEach { (ratio, _) ->
                    FilterChip(selected = aspect == ratio, onClick = { aspect = ratio }, label = { Text(ratio) }, modifier = Modifier.padding(end = 8.dp))
                }
            }
            Row {
                listOf(15, 30, 60).forEach { d ->
                    FilterChip(selected = duration == d, onClick = { duration = d }, label = { Text("${d}s") }, modifier = Modifier.padding(end = 8.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
            errorText?.let { Text(it, color = MaterialTheme.colorScheme.error); Spacer(Modifier.height(8.dp)) }

            PrimaryButton(
                text = if (isLoading) "Generating advertisement..." else "Generate Advertisement",
                enabled = !isLoading && (productImage != null || freeform.isNotBlank()),
                onClick = {
                    errorText = null
                    storyboard = null
                    resultUrl = null
                    isLoading = true
                    scope.launch {
                        try {
                            val base64 = productImage?.let { uri ->
                                context.contentResolver.openInputStream(uri)?.use { s -> Base64.encodeToString(s.readBytes(), Base64.NO_WRAP) }
                            }
                            val baseUrl = AppSettings.backendUrlFlow(context).first()
                            val api = ApiClient.create(baseUrl)
                            val job = api.createAd(
                                AdCreateRequest(
                                    productImageBase64 = base64,
                                    productName = productName,
                                    objective = objective,
                                    audience = audience,
                                    tone = tone,
                                    durationSeconds = duration,
                                    aspectRatio = aspect,
                                    callToAction = cta,
                                    freeformPrompt = freeform.ifBlank { null }
                                )
                            )
                            var status = api.getJob(job.jobId)
                            while (status.status != "completed" && status.status != "failed") {
                                if (status.storyboard != null) storyboard = status.storyboard
                                kotlinx.coroutines.delay(2000)
                                status = api.getJob(job.jobId)
                            }
                            if (status.status == "completed") {
                                storyboard = status.storyboard
                                resultUrl = status.resultUrl
                            } else {
                                errorText = status.error ?: "Advertisement generation failed"
                            }
                        } catch (e: Exception) {
                            errorText = e.message ?: "Something went wrong"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            )

            storyboard?.let { scenes ->
                Spacer(Modifier.height(20.dp))
                Text("Storyboard", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                scenes.forEach { scene ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Scene ${scene.index}  ${scene.startSeconds}-${scene.endSeconds}s", fontWeight = FontWeight.Medium)
                            Text(scene.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            resultUrl?.let { url ->
                Spacer(Modifier.height(16.dp))
                Text("Final advertisement: $url", style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { }) { Text("Preview") }
                    OutlinedButton(onClick = { }) { Text("Export") }
                    OutlinedButton(onClick = { }) { Text("Share") }
                }
            }
        }
    }
}
