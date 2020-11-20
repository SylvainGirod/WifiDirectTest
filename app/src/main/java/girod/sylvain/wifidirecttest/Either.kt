package girod.sylvain.wifidirecttest

import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

sealed class Either<out A, out B> {
    class Left<A>(val value: A) : Either<A, Nothing>()
    class Right<B>(val value: B) : Either<Nothing, B>()
}
class MyActionListener(
    val name: String,
    val onSuccess: () -> Unit = {},
    val onFailure: () -> Unit = {}
) : WifiP2pManager.ActionListener {
    override fun onSuccess() {
        Log.d("BBLOG", "$name success")
        onSuccess()
    }
    override fun onFailure(reason: Int) {
        Log.d("BBLOG", "$name fail wih reason $reason")
        onFailure()
    }
}