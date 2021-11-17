package com.bugs.wifiscannerkotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

class WifiBroadcastReceiver(
    private val wifiManager: WifiManager,
    private val wifiListView: ListView) : BroadcastReceiver() {

    companion object {
        const val LOG_TAG = "IkiLogBroadcast"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        // broadcast reveived
        Log.d(LOG_TAG, "onReceive()")
        Toast.makeText(context, "Broadcast ditompo bosque!", Toast.LENGTH_SHORT).show()

        if (intent != null) {
            Log.d(LOG_TAG, "onReceive() intent ok not null")
            val action = intent.action
            val ok = WifiManager.SCAN_RESULTS_AVAILABLE_ACTION == action

            if (ok) {
                // scan ok
                Log.d(LOG_TAG, "onReceive() Intent.action sama, OK!!")
                Toast.makeText(context, "Scan Rampung Bosque!", Toast.LENGTH_SHORT).show()
                val list: List<ScanResult> = wifiManager.scanResults
                showNetworks(list, context)
            } else {
                // scan not ok
                Log.d(LOG_TAG, "onReceive() Intent.action tidak sama bosque!, sekip")
            }
        }
        else Log.d(LOG_TAG, "onReceive duh intent null bosque")
    }

    private fun showNetworks(list: List<ScanResult>, context: Context?){
        // do something with the list of wifi
        if (context != null) {
            Log.d(LOG_TAG, "onReceive() -> showNetworks()")
            var deviceList: ArrayList<String> = arrayListOf()
            for (wifi in list) {
                deviceList.add("${wifi.SSID} - ${wifi.level} dB")
            }
            val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceList)
            wifiListView.adapter = arrayAdapter
        }
    }
}