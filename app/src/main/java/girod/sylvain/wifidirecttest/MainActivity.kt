package girod.sylvain.wifidirecttest

import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.request).setOnClickListener {
            val intent = Intent(this, RequestDiscoverActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.receive).setOnClickListener {
            val intent = Intent(this, ReceiveDiscoverActivity::class.java)
            startActivity(intent)
        }
    }

    override fun choosePeer(devices: List<WifiP2pDevice>) {
        // no-op
    }

    override fun getConnectionInfo(info: WifiP2pInfo) {
        // no-op
    }

    override fun updateDeviceName(name: String) {
        findViewById<TextView>(R.id.deviceNameMain).text = name
    }
}