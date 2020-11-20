package girod.sylvain.wifidirecttest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

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
}