package com.nixbug.entebus.utils

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

class QrCodeAnalyzer(
    private val onQrScanned: (String) -> Unit,
    private val onError: (Throwable) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    private val isScanned = AtomicBoolean(false)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        // Atomically check & set
        if (!isScanned.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image ?: run {
            isScanned.set(false)
            imageProxy.close()
            return
        }

        val inputImage =
            InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull()?.rawValue?.let { qrValue ->
                    onQrScanned(qrValue)
                } ?: run {
                    // No QR detected → allow next frame
                    isScanned.set(false)
                }
            }
            .addOnFailureListener { exception ->
                isScanned.set(false)
                onError(exception)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun clear() {
        scanner.close()
    }
}
