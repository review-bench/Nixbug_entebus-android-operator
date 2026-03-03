package com.nixbug.entebus.utils.printer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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

    @SuppressLint("MissingPermission")
    suspend fun connectToPrinter(deviceAddress: String): Boolean = withContext(Dispatchers.IO) {
        val adapter = bluetoothAdapter ?: return@withContext false
        closeConnection()

        try {
            val device = adapter.getRemoteDevice(deviceAddress)
            socket = device.createRfcommSocketToServiceRecord(PRINTER_UUID)
            adapter.cancelDiscovery()
            socket?.connect()
            _isConnected.value = true
            true
        } catch (e: Exception) {
            e.printStackTrace()
            closeConnection()
            false
        }
    }

    fun closeConnection() {
        synchronized(this) {
            try {
                if (socket?.isConnected == true) {
                    socket?.outputStream?.flush()
                }
                socket?.close()
            } catch (e: Exception) {
                println("BT_LOG: Error closing socket: ${e.message}")
            } finally {
                socket = null
                _isConnected.value = false
            }
        }
    }

    fun isConnected(): Boolean {
        val isSocketActive = socket?.isConnected ?: false
        if (!isSocketActive) return false

        return try {
            socket?.outputStream?.write(byteArrayOf(0x00))
            true
        } catch (e: IOException) {
            _isConnected.value = false
            false
        }
    }

    object Commands {
        val INIT = byteArrayOf(0x1B, 0x40)
        val ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)
        val ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
        val BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)
        val BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)
        val LINE_SPACE_DEFAULT = byteArrayOf(0x1B, 0x32)
        val LINE_SPACE_TIGHT = byteArrayOf(0x1B, 0x33, 0x14)
        val FONT_A = byteArrayOf(0x1B, 0x4D, 0x00)
        val FONT_B = byteArrayOf(0x1B, 0x4D, 0x01)
        val SIZE_NORMAL = byteArrayOf(0x1D, 0x21, 0x00)
        val SIZE_MEDIUM_TALL = byteArrayOf(0x1D, 0x21, 0x01)
        val SIZE_BIG = byteArrayOf(0x1D, 0x21, 0x11)
        val CHAR_TABLE_PC858 = byteArrayOf(0x1B, 0x74, 0x13)
    }

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
