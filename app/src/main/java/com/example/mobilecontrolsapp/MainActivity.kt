package com.example.mobilecontrolsapp

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothStatusTextView: TextView
    private lateinit var flashlightTextView: TextView
    private lateinit var bluetoothButton: Button
    private lateinit var flashlightButton: Button

    private var isFlashlightOn = false

    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            toggleBluetooth()
        } else {
            Toast.makeText(this, "Bluetooth izni verilmedi", Toast.LENGTH_SHORT).show()
        }
    }
    private val bluetoothDisableLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
        } else {

        }
        updateBluetoothStatus()
    }
    private val bluetoothEnableLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

        } else {

        }
        updateBluetoothStatus()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothStatusTextView = findViewById(R.id.textView)
        flashlightTextView = findViewById(R.id.textView2)
        bluetoothButton = findViewById(R.id.button)
        flashlightButton = findViewById(R.id.button2)

        updateBluetoothStatus()
        updateFlashlight()
        bluetoothButton.setOnClickListener {
            handleBluetoothPermission()
        }

        flashlightButton.setOnClickListener {
            toggleFlashlight()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun handleBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            bluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            toggleBluetooth()
        }
    }
    private fun updateBluetoothStatus() {
        val bluetoothAdapter: BluetoothAdapter? =
            (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        if (bluetoothAdapter == null) {
            bluetoothStatusTextView.text = "Bluetooth: ?"
        } else {
            bluetoothStatusTextView.text = if (bluetoothAdapter.isEnabled) {
                "Bluetooth: Açık"
            } else {
                "Bluetooth: Kapalı"
            }
        }
    }
    private fun toggleBluetooth() {
        val bluetoothAdapter: BluetoothAdapter? =
            (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled) {
                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                bluetoothDisableLauncher.launch(intent)
            } else {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bluetoothEnableLauncher.launch(intent)
            }
        } else {
            Toast.makeText(this, "Bluetooth desteklenmiyor", Toast.LENGTH_SHORT).show()
        }
    }



    private fun updateFlashlight() {
        if (isFlashlightOn) {
            flashlightTextView.text = "El Feneri açık durumda"
        } else {
            flashlightTextView.text = "El Feneri kapalı durumda"
        }
    }

    private fun toggleFlashlight() {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList[0]
            isFlashlightOn = if (isFlashlightOn) {
                cameraManager.setTorchMode(cameraId, false)
                false
            }
            else {
                cameraManager.setTorchMode(cameraId, true)
                true
            }
            updateFlashlight()
        } catch (e: Exception) {
            Toast.makeText(this, "El feneri çalışmıyor!", Toast.LENGTH_SHORT).show()
        }
    }
}
