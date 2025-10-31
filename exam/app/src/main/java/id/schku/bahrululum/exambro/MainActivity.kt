package id.schku.bahrululum.exambro

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {

    private val cameraPermissionCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnScan = findViewById<Button>(R.id.btn_scan)
        val btnManual = findViewById<Button>(R.id.btn_manual)
        val txtInfo = findViewById<TextView>(R.id.txt_info)

        txtInfo.text = "Aplikasi Exambro SMK NU Bahrul Ulum\nJl. Raya Pelemwatu No.09 Menganti Gresik"

        btnScan.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionCode)
            } else {
                startScan()
            }
        }

        btnManual.setOnClickListener {
            val exampleUrl = "https://example.com"
            startActivity(Intent(this, WebViewActivity::class.java).apply {
                putExtra("url", exampleUrl)
            })
        }
    }

    private fun startScan() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Arahkan kamera ke kode QR yang berisi tautan ujian.")
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan()
            } else {
                Toast.makeText(this, "Izin kamera diperlukan untuk memindai kode QR.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Pemindaian dibatalkan.", Toast.LENGTH_SHORT).show()
            } else {
                val scannedUrl = result.contents
                if (scannedUrl.startsWith("http://") || scannedUrl.startsWith("https://")) {
                    startActivity(Intent(this, WebViewActivity::class.java).apply {
                        putExtra("url", scannedUrl)
                    })
                } else {
                    Toast.makeText(this, "Kode yang dipindai bukan tautan yang valid.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
