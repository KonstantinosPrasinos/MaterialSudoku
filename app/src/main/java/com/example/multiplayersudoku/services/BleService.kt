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
import android.util.Log
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
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
}
