package girod.sylvain.wifidirecttest

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.*
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket

class RequestDiscoverActivity : BaseActivity() {

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
    private var dialog: AlertDialog? = null
    private var wasDeviceChosen = false

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
        findViewById<Button>(R.id.quitGroupButtonRequest).setOnClickListener {
            removeGroup()
        }
    }

    override fun choosePeer(devices: List<WifiP2pDevice>) {
        if (dialog?.isShowing == true) return
        dialog = AlertDialog.Builder(this)
            .setTitle("Choose device")
            .setItems(
                devices.map { it.deviceName }.toTypedArray()
            ) { dialog, which ->
                Log.v("BBLOG", "device chosen ${devices[which]}")
                wasDeviceChosen = true
                connectToDevice(devices[which])
                dialog.dismiss()
            }.show()
    }

    private fun connectToDevice(device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        manager?.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("BBLOG", "connect success")
            }

            override fun onFailure(reason: Int) {
                Log.d("BBLOG", "connect fail for reason $reason")
            }
        })
    }

    override fun getConnectionInfo(info: WifiP2pInfo) {
        // no-op
        if (info.)
    }

    private fun removeGroup() {
        manager?.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                wasDeviceChosen = false
                Log.d("BBLOG", "removeGroup success")
            }

            override fun onFailure(reason: Int) {
                Log.d("BBLOG", "removeGroup fail for reason $reason")
            }
        })
    }

    override fun updateDeviceName(name: String) {
        findViewById<TextView>(R.id.deviceNameRequest).text = name
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

class ClientAsyncTask(
    private val context: Context,
    private val host: String,
    private val port: Int
) : AsyncTask<Void, Void, String?>() {

    override fun doInBackground(vararg params: Void): String? {
        val socket = Socket()
        val buf = ByteArray(1024)
            var totoRes = ""
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            socket.bind(null)
            socket.connect((InetSocketAddress(host, port)), 500)

            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data is retrieved by the server device.
             */
            val inputStream = socket.getInputStream()
            while (inputStream.read(buf).also { var len = it } != -1) {
                totoRes = buf.decodeToString()
            }

            inputStream.close()
            //catch logic
        } catch (e: IOException) {
            Log.d("BBLOG", "err io client")
            //catch logic
        } finally {
            /**
             * Clean up any open sockets when done
             * transferring or if an exception occurred.
             */
            socket.takeIf { it.isConnected }?.apply {
                close()
            }
            return totoRes
        }
        /**
         * Create a server socket.
         */
    }

    /**
     * Start activity that can handle the JPEG image
     */
    override fun onPostExecute(result: String?) {
        result?.run {
            Log.d("BBLOG", result)
        }
    }
}