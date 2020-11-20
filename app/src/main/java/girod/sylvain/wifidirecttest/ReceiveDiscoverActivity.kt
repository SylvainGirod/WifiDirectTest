package girod.sylvain.wifidirecttest

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat

class ReceiveDiscoverActivity : BaseActivity() {

    val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }

    var channel: WifiP2pManager.Channel? = null
    var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive_discover)

        channel = manager?.initialize(this, mainLooper, null)
        channel?.also { channel ->
            receiver = WifiDirectBroadcastReceiver(manager, channel, this)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("BBLOG", "grant les permissions wesh")
            return
        }

        findViewById<Button>(R.id.receiveButton).setOnClickListener {
            manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d("BBLOG", "discover success")
                }

                override fun onFailure(reason: Int) {
                    Log.d("BBLOG", "discover fail with reason $reason")
                }
            })
        }
        findViewById<Button>(R.id.quitGroupButtonReceive).setOnClickListener {
            removeGroup()
        }
    }

    override fun getConnectionInfo(info: WifiP2pInfo) {
        // no-op
    }

    override fun choosePeer(devices: List<WifiP2pDevice>) {
        // no-op
    }

    override fun updateDeviceName(name: String) {
        findViewById<TextView>(R.id.deviceNameReceive).text = name
    }

    private fun removeGroup() {
        manager?.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                manager?.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.d("BBLOG", "stopPeerDiscovery success")
                    }

                    override fun onFailure(reason: Int) {
                        Log.d("BBLOG", "stopPeerDiscovery fail with reason $reason")
                    }
                })
                Log.d("BBLOG", "removeGroup success")
            }

            override fun onFailure(reason: Int) {
                Log.d("BBLOG", "removeGroup fail for reason $reason")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        receiver?.also {
            registerReceiver(it, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        receiver?.also(::unregisterReceiver)
    }
}