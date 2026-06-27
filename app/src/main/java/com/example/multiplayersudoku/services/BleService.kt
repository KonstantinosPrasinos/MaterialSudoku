package com.example.multiplayersudoku.services

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleService @Inject constructor(@param:ApplicationContext private val context: Context) {
    companion object {
        private const val TAG = "BleService"

        // Replace this with your generated 128-bit App UUID
        private val LOBBY_SERVICE_UUID = UUID.fromString("f36c8d49-1a53-4626-bcdc-f6d4aefcab8c")
        private val CHARACTERISTIC_UUID = UUID.fromString("c2df3457-f40c-4844-b76b-695b9b5dadff");
    }

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
    private var gattServer: BluetoothGattServer? = null
    private var lobbyPayloadBytes: ByteArray = byteArrayOf()

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var scanCleanupJob: Job? = null
    private var scanCallback: ScanCallback? = null
    private var onLobbyDiscoveredListener: ((String, String) -> Unit)? = null
    private var onLobbyLostListener: ((String, String) -> Unit)? = null

    private val lastSeen = mutableMapOf<String, Long>()
    private val discoveredLobbies = mutableMapOf<String, String>()
    private val connectingDevices = mutableSetOf<String>()

    val hasBle: Boolean by lazy {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    @SuppressLint("MissingPermission")
    fun startAdvertisingLobby(lobbyId: String) {
        if (!hasBle || advertiser == null) {
            Log.e(TAG, "Cannot advertise: BLE hardware or advertiser is unavailable.")
            return
        }

        lobbyPayloadBytes = lobbyId.toByteArray()

        // Start the server that listens for requests and handles responses
        gattServer = bluetoothManager.openGattServer(context, gattServerCallback).apply {
            val service =
                BluetoothGattService(LOBBY_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)

            val characteristic = BluetoothGattCharacteristic(
                CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ
            )

            service.addCharacteristic(characteristic)
            addService(service)
        }


        // Configure the ble connection (the hardware)
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(true)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build()

        val advertiseData = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(LOBBY_SERVICE_UUID))
            .setIncludeDeviceName(false)
            .setIncludeTxPowerLevel(false)
            .build()

        // Start advertising
        try {
            advertiser.startAdvertising(settings, advertiseData, advertiseCallback)
        } catch (e: SecurityException) {
            Log.e(TAG, "Cannot start advertising: Missing permissions.", e)
        }
    }

    @SuppressLint("MissingPermission")
    fun stopAdvertisingLobby() {
        if (advertiser != null) {
            try {
                advertiser.stopAdvertising(advertiseCallback)
                Log.d(TAG, "BLE Advertising stopped successfully.")
            } catch (e: SecurityException) {
                Log.e(TAG, "Failed to stop advertising: ${e.message}")
            }
        }

        gattServer?.apply {
            clearServices()
            close()
        }
        gattServer = null
    }

    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Device connected to our GATT Server: ${device.address}")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Device disconnected from our GATT Server: ${device.address}")
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)

            if (characteristic?.uuid == CHARACTERISTIC_UUID) {
                Log.d(TAG, "Sending lobby payload data chunk to ${device?.address}")

                gattServer?.sendResponse(
                    device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    offset,
                    lobbyPayloadBytes
                )
            }
        }
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            Log.i(TAG, "LE Advertise Started.")
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
        }
    }

    @SuppressLint("MissingPermission")
    fun startScanningForLobbies(
        onDiscovered: (String, String) -> Unit,
        onLost: (String, String) -> Unit
    ) {
        if (!hasBle || bluetoothAdapter == null) {
            Log.e(TAG, "Cannot scan: BLE hardware or adapter is unavailable.")
            return
        }

        val scanner = bluetoothAdapter.bluetoothLeScanner
        if (scanner == null) {
            Log.e(TAG, "BluetoothLeScanner is null.")
            return
        }

        stopScanningForLobbies()

        onLobbyDiscoveredListener = onDiscovered
        onLobbyLostListener = onLost

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                val device = result.device
                val address = device.address

                synchronized(lastSeen) {
                    lastSeen[address] = System.currentTimeMillis()
                }

                val existingRoom = synchronized(discoveredLobbies) { discoveredLobbies[address] }
                if (existingRoom != null) {
                    onLobbyDiscoveredListener?.invoke(existingRoom, address)
                } else {
                    connectAndReadRoomCode(device)
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.e(TAG, "Scan failed with error code: $errorCode")
            }
        }

        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(LOBBY_SERVICE_UUID))
            .build()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        try {
            scanner.startScan(listOf(filter), settings, scanCallback)
            Log.d(TAG, "BLE Scanning started successfully.")
        } catch (e: SecurityException) {
            Log.e(TAG, "Cannot start scan: Missing permissions.", e)
            return
        }

        scanCleanupJob = serviceScope.launch {
            while (true) {
                delay(2000)
                val now = System.currentTimeMillis()
                val toRemove = mutableListOf<String>()

                synchronized(lastSeen) {
                    val iterator = lastSeen.entries.iterator()
                    while (iterator.hasNext()) {
                        val entry = iterator.next()
                        if (now - entry.value > 8000) {
                            toRemove.add(entry.key)
                            iterator.remove()
                        }
                    }
                }

                for (address in toRemove) {
                    val roomCode = synchronized(discoveredLobbies) {
                        discoveredLobbies.remove(address)
                    }
                    if (roomCode != null) {
                        Log.d(TAG, "Lobby $roomCode on $address timed out (no scan results).")
                        onLobbyLostListener?.invoke(roomCode, address)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScanningForLobbies() {
        scanCleanupJob?.cancel()
        scanCleanupJob = null

        if (bluetoothAdapter != null && scanCallback != null) {
            val scanner = bluetoothAdapter.bluetoothLeScanner
            if (scanner != null) {
                try {
                    scanner.stopScan(scanCallback)
                    Log.d(TAG, "BLE Scanning stopped.")
                } catch (e: SecurityException) {
                    Log.e(TAG, "Failed to stop scan: ${e.message}")
                }
            }
        }
        scanCallback = null
        onLobbyDiscoveredListener = null
        onLobbyLostListener = null

        synchronized(lastSeen) {
            lastSeen.clear()
        }
        synchronized(discoveredLobbies) {
            discoveredLobbies.clear()
        }
        connectingDevices.clear()
    }

    @SuppressLint("MissingPermission")
    private fun connectAndReadRoomCode(device: BluetoothDevice) {
        val address = device.address
        synchronized(connectingDevices) {
            if (connectingDevices.contains(address) || discoveredLobbies.containsKey(address)) {
                return
            }
            connectingDevices.add(address)
        }

        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, "Connected to GATT server of device $address. Discovering services...")
                    g.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d(TAG, "Disconnected from GATT server of device $address")
                    synchronized(connectingDevices) {
                        connectingDevices.remove(address)
                    }
                    g.close()
                }
            }

            override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val service = g.getService(LOBBY_SERVICE_UUID)
                    val characteristic = service?.getCharacteristic(CHARACTERISTIC_UUID)
                    if (characteristic != null) {
                        Log.d(TAG, "Characteristic found. Reading...")
                        g.readCharacteristic(characteristic)
                    } else {
                        Log.e(TAG, "Lobby service or characteristic not found on $address")
                        synchronized(connectingDevices) {
                            connectingDevices.remove(address)
                        }
                        g.disconnect()
                        g.close()
                    }
                } else {
                    Log.e(TAG, "Service discovery failed on $address: $status")
                    synchronized(connectingDevices) {
                        connectingDevices.remove(address)
                    }
                    g.disconnect()
                    g.close()
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onCharacteristicRead(
                g: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val data = characteristic.value
                    if (data != null) {
                        val roomCode = String(data)
                        Log.d(TAG, "Successfully read room code: $roomCode from $address")

                        synchronized(discoveredLobbies) {
                            discoveredLobbies[address] = roomCode
                        }

                        onLobbyDiscoveredListener?.invoke(roomCode, address)
                    }
                } else {
                    Log.e(TAG, "Failed to read characteristic from $address: $status")
                }

                synchronized(connectingDevices) {
                    connectingDevices.remove(address)
                }
                g.disconnect()
                g.close()
            }

            override fun onCharacteristicRead(
                g: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray,
                status: Int
            ) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val roomCode = String(value)
                    Log.d(TAG, "Successfully read room code (API 33+): $roomCode from $address")

                    synchronized(discoveredLobbies) {
                        discoveredLobbies[address] = roomCode
                    }

                    onLobbyDiscoveredListener?.invoke(roomCode, address)
                } else {
                    Log.e(TAG, "Failed to read characteristic (API 33+) from $address: $status")
                }

                synchronized(connectingDevices) {
                    connectingDevices.remove(address)
                }
                g.disconnect()
                g.close()
            }
        }

        Log.d(TAG, "Connecting to GATT on $address")
        device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }
}
