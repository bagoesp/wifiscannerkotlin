package com.bugs.wifiscannerkotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val LOG_TAG = "IkiLogBos"
        const val MY_REQUEST_CODE = 123
    }

    private lateinit var wifiManager: WifiManager

    private lateinit var buttonState: Button
    private lateinit var buttonScan: Button
    lateinit var listWifi: ListView

    private lateinit var wifiReceiver: WifiBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // view
        listWifi = findViewById(R.id.lv_wifi_devices)
        buttonState = findViewById(R.id.button_state)
        buttonScan = findViewById(R.id.button_scan)

        buttonState.setOnClickListener(this)
        buttonScan.setOnClickListener(this)


        // inisialisasikan object wifiManager
        wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // instansiasi wifi broadcast receiver
        wifiReceiver = WifiBroadcastReceiver(wifiManager, listWifi)
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart() register receiver")

        // register receivernya
        val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiReceiver, filter)
    }

    // method untuk menangani onClick
    override fun onClick(v: View?) {
        if (v != null){
            when (v.id) {
                R.id.button_state -> {
                    // show toast wifi state
                    showWifiState()
                }

                R.id.button_scan -> {
                    // scan wifi network
                    askAndStartScanWifi()
                }

            }
        }
    }

    // function untuk menampilkan state wifi device
    private fun showWifiState(){
        val state = wifiManager.wifiState
        var statusInfo = "Unkown"

        when (state) {
            WifiManager.WIFI_STATE_DISABLING ->
                statusInfo = "Disabling"

            WifiManager.WIFI_STATE_DISABLED ->
                statusInfo = "Disabled"

            WifiManager.WIFI_STATE_ENABLING ->
                statusInfo = "Enabling"

            WifiManager.WIFI_STATE_ENABLED ->
                statusInfo = "Enabled"

            WifiManager.WIFI_STATE_UNKNOWN ->
                statusInfo = "Unkown"

            else -> statusInfo = "Unknown"
        }
        Toast.makeText(this, "Wifi Status: $statusInfo", Toast.LENGTH_SHORT).show()
    }

    // function untuk ask permission dan scanning wifi
    private fun askAndStartScanWifi() {
        // ask permission for android 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)

            // check permission nya dulu gaes
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Request Permission")

                // Request permissions nya.
                ActivityCompat.requestPermissions(this,
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
                    android.Manifest.permission.ACCESS_NETWORK_STATE
                ), MY_REQUEST_CODE)
                return
            }
            Log.d(LOG_TAG, "Permission uwis diijinno bosque")
        }
        doStartScanWifi()
    }

    // Function nggo nyecan wifi cuy
    private fun doStartScanWifi() {
        Log.d(LOG_TAG, "doStartScanWifi()")
        wifiManager.startScan()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(LOG_TAG, "onRequestPermissionResult")

        when (requestCode) {
            MY_REQUEST_CODE ->
                // jika permission ditolak, array akan kosong
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission telah diijinkan
                    Log.d(LOG_TAG, "Permission diijinkan: " + permissions[0])

                    // mulai scan wifi
                    doStartScanWifi()
                } else {
                    // permission ditolak
                    Log.d(LOG_TAG, "Permission ditolak: " + permissions[0])
                }
        }
    }

    override fun onStop() {
        unregisterReceiver(wifiReceiver)
        super.onStop()
    }
}