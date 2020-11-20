package girod.sylvain.wifidirecttest

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

class WifiDirectBroadcastReceiver(
    private val manager: WifiP2pManager?,
    private val channel: WifiP2pManager.Channel,
    private val activity: BaseActivity
) : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        requireNotNull(context)
        requireNotNull(intent)
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val device: WifiP2pDevice? = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                device?.let {
                    activity.updateDeviceName(it.deviceName)
                }
                // Log.d("BBLOG", "Current device name ${device?.deviceName}")
            }
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                Log.d("BBLOG", "WIFI_P2P_STATE_CHANGED_ACTION")
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                when (state) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        // Wifi P2P is enabled
                        Log.d("BBLOG", "WIFI_P2P_STATE_ENABLED")
                    }
                    else -> {
                        Log.d("BBLOG", "WIFI_P2P_STATE_DISABLED")
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                Log.d("BBLOG", "WIFI_P2P_PEERS_CHANGED_ACTION")
                manager?.requestPeers(channel) { peers: WifiP2pDeviceList? ->
                    peers?.deviceList?.let { devices ->
                        devices.forEach {
                            Log.d("BBLOG", "Device : $it")
                        }
                        activity.choosePeer(devices.toList())
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                Log.d("BBLOG", "WIFI_P2P_CONNECTION_CHANGED_ACTION")
                manager?.requestConnectionInfo(channel) {
                    Log.d("BBLOG", "Connection Info : $it")
                }
            }
        }
    }
}