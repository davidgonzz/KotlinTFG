package com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


@Composable
fun BarcodeScannerScreen(
    onCodeScanned: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { ContextCompat.getMainExecutor(context) } // ✅ compatible
    val scanner = remember { BarcodeScanning.getClient() }
    val activity = remember(context) {
        context as? Activity
    }
    LaunchedEffect(activity) {
        activity?.let {
            val permisoConcedido = ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (!permisoConcedido) {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.CAMERA),
                    1234
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val previewUseCase = Preview.Builder().build().apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val analysisUseCase = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .apply {
                            setAnalyzer(executor) { imageProxy ->
                                processImageProxy(scanner, imageProxy, onCodeScanned)
                            }
                        }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            previewUseCase,
                            analysisUseCase
                        )
                    } catch (e: Exception) {
                        Log.e("BarcodeScanner", "Error al vincular cámara", e)
                    }
                }, executor)

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = { onCancel() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A5F0B))
        ) {
            Text("Cancelar escaneo", color = Color.White)
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
fun processImageProxy(
    scanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onCodeScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull()?.rawValue?.let { code ->
                    onCodeScanned(code)
                }
            }
            .addOnFailureListener { e ->
                Log.e("BarcodeScanner", "Error en escaneo: ${e.message}")
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
