package girod.sylvain.wifidirecttest

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat

class RequestDiscoverActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_request_discover)

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

        val peers = mutableListOf<WifiP2pDevice>()

        val peerListener = WifiP2pManager.PeerListListener { peerList ->
            Log.d("BBLOG", "peers: ${peerList.deviceList}")
        }

        findViewById<Button>(R.id.requestButton).setOnClickListener {
            manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d("BBLOG", "discover success")
                }

                override fun onFailure(reason: Int) {
                    Log.d("BBLOG", "discover fail with reason $reason")
                }
            })
        }
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