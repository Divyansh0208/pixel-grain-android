package com.example.cropidentifier

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ai.onnxruntime.*
import java.nio.FloatBuffer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CropApp()
            }
        }
    }
}

@Composable
fun CropApp() {
    val context = LocalContext.current
    var result by remember { mutableStateOf("Pick an image to classify") }
    val labels = listOf("jute", "maize", "rice", "sugarcane", "wheat")

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
            val rgbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            result = classifyImage(context, rgbBitmap, labels)
        } catch (e: Exception) {
            result = "Error: ${e.message}"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = result, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Crop Image")
        }
    }
}

fun classifyImage(context: android.content.Context, bitmap: Bitmap, labels: List<String>): String {
    val env = OrtEnvironment.getEnvironment()
    val session = env.createSession(context.assets.open("best.onnx").readBytes(), OrtSession.SessionOptions())
    val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
    val floatArray = bitmapToFloatArray(resized)
    val inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(floatArray), longArrayOf(1, 3, 224, 224))
    val output = session.run(mapOf("images" to inputTensor))
    val probs = (output[0].value as Array<FloatArray>)[0]
    val maxIdx = probs.indices.maxByOrNull { probs[it] } ?: 0
    val confidence = (probs[maxIdx] * 100).toInt()
    session.close()
    return "${labels[maxIdx]} ($confidence%)"
}

fun bitmapToFloatArray(bitmap: Bitmap): FloatArray {
    val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
    val std = floatArrayOf(0.229f, 0.224f, 0.225f)
    val floats = FloatArray(3 * 224 * 224)
    var idx = 0
    for (c in 0..2) {
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val px = bitmap.getPixel(x, y)
                val v = when (c) {
                    0 -> ((px shr 16) and 0xFF) / 255f
                    1 -> ((px shr 8) and 0xFF) / 255f
                    else -> (px and 0xFF) / 255f
                }
                floats[idx++] = (v - mean[c]) / std[c]
            }
        }
    }
    return floats
}