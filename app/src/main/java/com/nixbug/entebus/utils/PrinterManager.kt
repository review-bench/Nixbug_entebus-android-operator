package com.nixbug.entebus.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrinterManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    private val PRINTER_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var socket: BluetoothSocket? = null


    private val _isConnected = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean> = _isConnected.asStateFlow()

    /**
     * Attempts to connect to a Bluetooth printer.
     * This is a suspending function that runs on the IO dispatcher.
     */
    @SuppressLint("MissingPermission")
    suspend fun connectToPrinter(deviceAddress: String): Boolean = withContext(Dispatchers.IO) {
        val adapter = bluetoothAdapter ?: return@withContext false

        // Always close existing connection before starting a new one
        closeConnection()

        try {
            val device = adapter.getRemoteDevice(deviceAddress)

            // Create RFCOMM socket
            socket = device.createRfcommSocketToServiceRecord(PRINTER_UUID)

            // Cancel discovery as it slows down connection
            adapter.cancelDiscovery()

            // Connect (blocking call)
            socket?.connect()

            _isConnected.value = true
            true
        } catch (e: Exception) {
            e.printStackTrace()
            closeConnection()
            false
        }
    }

    /**
     * Sends raw bytes to the printer.
     */
    fun sendData(data: ByteArray) {
        if (socket == null || !socket!!.isConnected) {
            _isConnected.value = false
            return
        }

        try {
            val outputStream = socket?.outputStream
            outputStream?.write(data)
            outputStream?.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            _isConnected.value = false
        }
    }

    /**
     * Properly releases the socket resources.
     */
    fun closeConnection() {
        synchronized(this) {
            try {
                // Check if socket exists and is connected before trying to flush
                if (socket?.isConnected == true) {
                    socket?.outputStream?.flush()
                }
                socket?.close()
            } catch (e: Exception) {
                // Log the error but don't crash
                println("BT_LOG: Error closing socket: ${e.message}")
            } finally {
                socket = null
                _isConnected.value = false
            }
        }
    }

    /**
     * Checks if the socket is currently connected.
     */
    fun isConnected(): Boolean {
        val isSocketActive = socket?.isConnected ?: false
        if (!isSocketActive) return false

        // Attempt a tiny "no-op" write to see if the link is actually alive
        return try {
            // Sending an empty INIT command or just checking the output stream
            socket?.outputStream?.write(byteArrayOf(0x00))
            true
        } catch (e: IOException) {
            _isConnected.value = false
            false
        }
    }

    // --- ESC/POS COMMANDS ---

    object Commands {
        val INIT = byteArrayOf(0x1B, 0x40)

        // Line Spacing
        val LINE_SPACING_20 = byteArrayOf(0x1B, 0x33, 0x14)
        val LINE_SPACING_30 = byteArrayOf(0x1B, 0x33, 0x1E)
        val LINE_SPACING_DEFAULT = byteArrayOf(0x1B, 0x32)

        // Alignment
        val ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)
        val ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
        val ALIGN_RIGHT = byteArrayOf(0x1B, 0x61, 0x02)

        // Text Styles
        val BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)
        val BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)

        // Font Sizes
        val SIZE_NORMAL = byteArrayOf(0x1B, 0x21, 0x00)
        val SIZE_LARGE = byteArrayOf(0x1B, 0x21, 0x30)

        val FEED_LINE = byteArrayOf(0x0A)
    }

    /**
     * Helper to print text with common formatting.
     */
    fun printStyledText(
        text: String,
        alignment: ByteArray = Commands.ALIGN_LEFT,
        isBold: Boolean = false,
        size: ByteArray = Commands.SIZE_NORMAL
    ) {
        sendData(Commands.INIT)
        sendData(alignment)
        sendData(size)
        if (isBold) sendData(Commands.BOLD_ON) else sendData(Commands.BOLD_OFF)
        sendData((text + "\n").toByteArray())
    }
}