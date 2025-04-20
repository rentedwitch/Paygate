import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

class QrScannerActivity : AppCompatActivity() {

    private lateinit var barcodeView: CompoundBarcodeView

    private val CAMERA_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)

        barcodeView = findViewById(R.id.barcode_scanner)

        // Request Camera Permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            // Start scanning if permission is already granted
            startScanning()
        }

        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    barcodeView.pause()
                    // Handle the scanned QR code content (result.text)
                    Toast.makeText(this@QrScannerActivity, "QR Code: ${result.text}", Toast.LENGTH_LONG).show()
                    // You can add your logic here to process the QR content
                }
            }

            override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) {}
        })
    }

    private fun startScanning() {
        barcodeView.resume()
    }

    override fun onResume() {
        super.onResume()
        // Resume scanning if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startScanning()
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start scanning
                startScanning()
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
