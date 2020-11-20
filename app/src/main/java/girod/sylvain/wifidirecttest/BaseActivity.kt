package girod.sylvain.wifidirecttest

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    abstract fun choosePeer(devices: List<WifiP2pDevice>)
    abstract fun updateDeviceName(name: String)
    abstract fun getConnectionInfo(info: WifiP2pInfo)
}