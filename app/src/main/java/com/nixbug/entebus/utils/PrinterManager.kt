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
        /** Resets the printer to default settings */
        val INIT = byteArrayOf(0x1B, 0x40)

        /** Moves the print head to the next line */
        val FEED_LINE = byteArrayOf(0x0A)

        // --- Alignment ---
        val ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)
        val ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
        val ALIGN_RIGHT = byteArrayOf(0x1B, 0x61, 0x02)

        // --- Text Weight (Pure Bold) ---
        /** Bold without changing font size */
        val BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)
        val BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)

        // --- Line Spacing (Vertical Space) ---
        /** Sets line spacing to default (approx 3.75mm or 30 dots) */
        val LINE_SPACE_DEFAULT = byteArrayOf(0x1B, 0x32)

        /** Sets line spacing to a very tight 2.5mm (20 dots). Change 0x14 (20) to adjust. */
        val LINE_SPACE_TIGHT = byteArrayOf(0x1B, 0x33, 0x14)

        // --- Font Selection (Size) ---
        /** Standard Font A (12x24 dots) - Usually 32 chars/line */
        val FONT_A = byteArrayOf(0x1B, 0x4D, 0x00)

        /** Small Font B (9x17 dots) - Allows more text per line (approx 42 chars) */
        val FONT_B = byteArrayOf(0x1B, 0x4D, 0x01)

        // --- Scaling (Magnification) ---
        /** Normal text scaling (1x1) */
        val SIZE_NORMAL = byteArrayOf(0x1D, 0x21, 0x00)

        /** Large text (2x2) */
        val SIZE_BIG = byteArrayOf(0x1D, 0x21, 0x11)

        // --- Character Sets ---
        val CHAR_TABLE_PC858 = byteArrayOf(0x1B, 0x74, 0x13)
    }

    /** * Core function to send the data.
     * Recommended: Call this from the TicketBuilder build() output.
     */
    fun sendData(data: ByteArray) {
        if (socket == null || !socket!!.isConnected) {
            _isConnected.value = false
            return
        }
        try {
            socket?.outputStream?.let {
                it.write(data)
                it.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            _isConnected.value = false
        }
    }

}