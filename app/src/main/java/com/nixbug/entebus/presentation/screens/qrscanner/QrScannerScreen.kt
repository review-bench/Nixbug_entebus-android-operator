package com.nixbug.entebus.presentation.screens.qrscanner

import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.nixbug.entebus.ui.theme.PrimaryColor
import com.nixbug.entebus.utils.QrCodeAnalyzer
import java.util.concurrent.Executors

/* -------------------- MAIN SCREEN ---------------------- */
@Composable
fun QrScannerScreen(
    navController: NavHostController,
    viewModel: QrScannerViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scannedText by viewModel.scannedResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val cameraProviderState = remember {
        mutableStateOf<ProcessCameraProvider?>(null)
    }

    val analyzer = remember {
        QrCodeAnalyzer(
            onQrScanned = viewModel::onQrScanned,
            onError = { throwable ->
                viewModel.onScanError(
                    throwable.message ?: "Failed to scan QR"
                )
            }
        )
    }

    val analysisExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    /* -------- Cleanup Camera & Analyzer -------- */
    DisposableEffect(Unit) {
        onDispose {
            cameraProviderState.value?.unbindAll()
            analyzer.clear()
            analysisExecutor.shutdown()
            viewModel::clearResult
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        /* -------- Camera Preview -------- */
        AndroidView(
            factory = { ctx ->

                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        cameraProviderState.value = cameraProvider

                        val preview = Preview.Builder().build().apply {
                            surfaceProvider = previewView.surfaceProvider
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .apply {
                                setAnalyzer(analysisExecutor, analyzer)
                            }

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )

                    } catch (e: Exception) {
                        e.printStackTrace()

                        Toast.makeText(
                            ctx,
                            "Unable to open camera. Please try again.",
                            Toast.LENGTH_LONG
                        ).show()

                        navController.popBackStack()
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        ScannerOverlay()
        ScannerTopBar { navController.popBackStack() }

        /* -------- Result Dialog -------- */
        scannedText?.let {
            QrResultOverlayDialog(
                result = it,
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
    }

    /* -------- Error Handling -------- */
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }
}


/* -------------------- OVERLAY -------------------------- */
@Composable
fun ScannerOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
        ) {

            val width = size.width
            val height = size.height

            val scanSize = width * 0.65f
            val left = (width - scanSize) / 2
            val top = (height - scanSize) / 2
            val right = left + scanSize
            val bottom = top + scanSize

            val cornerLength = scanSize * 0.18f
            val strokeWidth = 10f

            /* -------- Dim background -------- */
            drawRect(Color.Black.copy(alpha = 0.75f))

            /* -------- Cut-out -------- */
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(scanSize, scanSize),
                blendMode = BlendMode.Clear
            )

            /* -------- Animated Scan Line -------- */
            val scanY = top + (scanSize * scanLineOffset)

            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        PrimaryColor.copy(alpha = 0.8f),
                        Color.Transparent
                    )
                ),
                start = Offset(left + 20f, scanY),
                end = Offset(right - 20f, scanY),
                strokeWidth = 6f
            )

            /* -------- Corners -------- */
            fun drawCorner(
                start: Offset,
                horizontal: Offset,
                vertical: Offset
            ) {
                drawLine(
                    color = PrimaryColor,
                    start = start,
                    end = horizontal,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = PrimaryColor,
                    start = start,
                    end = vertical,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            drawCorner(
                Offset(left, top),
                Offset(left + cornerLength, top),
                Offset(left, top + cornerLength)
            )

            drawCorner(
                Offset(right, top),
                Offset(right - cornerLength, top),
                Offset(right, top + cornerLength)
            )

            drawCorner(
                Offset(right, bottom),
                Offset(right - cornerLength, bottom),
                Offset(right, bottom - cornerLength)
            )

            drawCorner(
                Offset(left, bottom),
                Offset(left + cornerLength, bottom),
                Offset(left, bottom - cornerLength)
            )
        }

        /* -------- Bottom Text -------- */
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scan any QR code",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Align QR within the frame",
                color = Color.White.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/* -------------------- TOP BAR -------------------------- */
@Composable
fun ScannerTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

/* -------------------- RESULT DIALOG -------------------- */
@Composable
fun QrResultOverlayDialog(
    result: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ticket Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = result,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue")
                }
            }
        }
    }
}
