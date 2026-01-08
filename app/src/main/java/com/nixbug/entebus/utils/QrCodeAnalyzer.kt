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

/**
 * [QrCodeAnalyzer] is a CameraX [ImageAnalysis.Analyzer] implementation responsible
 * for detecting and decoding QR codes from camera frames using ML Kit.
 *
 * ### Responsibilities
 * - Processes camera frames provided by CameraX
 * - Detects QR codes using ML Kit's Barcode Scanner
 * - Ensures only a single QR code is processed per scan session
 *
 * ### Thread Safety
 * This analyzer may be invoked on a background executor. An [AtomicBoolean]
 * (`isScanned`) is used to ensure thread-safe control over scan state and
 * prevent duplicate QR scans from consecutive frames.
 *
 * ### Lifecycle
 * 1. **Initialization** – Analyzer is created and attached to CameraX
 * 2. **Usage** – Frames are analyzed until a QR code is detected
 * 3. **Completion** – Once a QR is detected, further frames are ignored
 * 4. **Cleanup** – [clear] must be called when the scanner screen is disposed
 *    to reset state and release ML Kit resources
 *
 * @param onQrScanned Callback invoked when a QR code is successfully detected.
 * The decoded QR value is provided as a [String].
 *
 * @param onError Callback invoked when QR processing fails due to an exception
 * during image analysis or ML Kit processing.
 */
class QrCodeAnalyzer(
    private val onQrScanned: (String) -> Unit,
    private val onError: (Throwable) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    /**
     * Atomic flag used to prevent multiple QR scans from being processed.
     *
     * Once a QR code is detected, this flag is set to `true` to ignore
     * subsequent frames until the analyzer is reset via [clear].
     */
    private val isScanned = AtomicBoolean(false)

    /**
     * Analyzes camera frames and attempts to detect a QR code.
     *
     * If a QR code is detected, [onQrScanned] is invoked and further
     * frame analysis is paused until reset.
     *
     * @param imageProxy Camera frame provided by CameraX. Must be closed
     * after processing to avoid blocking the camera pipeline.
     */
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        // Atomically check & set to avoid duplicate scans
        if (!isScanned.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image ?: run {
            isScanned.set(false)
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

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

    /**
     * Clears the analyzer state and releases ML Kit resources.
     *
     * This method **must be called** when the scanner screen is disposed
     * or when a new scan session is started. It resets the scan state
     * and closes the underlying barcode scanner to prevent leaks.
     */
    fun clear() {
        isScanned.set(false)
        scanner.close()
    }
}
