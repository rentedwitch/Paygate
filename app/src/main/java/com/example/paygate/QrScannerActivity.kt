package com.example.paygate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

class QrScannerActivity : AppCompatActivity() {

    private lateinit var barcodeView: CompoundBarcodeView
    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    private val db = FirebaseFirestore.getInstance()
    private var hasScanned = false // prevent multiple scans

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)

        barcodeView = findViewById(R.id.barcode_scanner)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startScanning()
        }

        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                if (hasScanned) return  // prevent multiple triggers
                result?.let {
                    hasScanned = true
                    barcodeView.pause()

                    val scannedUid = result.text
                    Log.d("QrScanner", "Scanned UID: $scannedUid")

                    db.collection("users").document(scannedUid).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val name = document.getString("name")
                                val bankBalance = document.getDouble("bankBalance")

                                if (name != null && bankBalance != null) {
                                    val intent = Intent(this@QrScannerActivity, MoneyTransferActivity::class.java)
                                    intent.putExtra("receiverName", name)
                                    intent.putExtra("receiverUid", scannedUid)
                                    intent.putExtra("receiverBankBalance", bankBalance)
                                    startActivity(intent)
                                    finish() // close scanner so user doesn't come back to it
                                } else {
                                    hasScanned = false
                                    Toast.makeText(this@QrScannerActivity, "Invalid user data", Toast.LENGTH_SHORT).show()
                                    barcodeView.resume()
                                }
                            } else {
                                hasScanned = false
                                Toast.makeText(this@QrScannerActivity, "User not found", Toast.LENGTH_SHORT).show()
                                barcodeView.resume()
                            }
                        }
                        .addOnFailureListener { e ->
                            hasScanned = false
                            Log.e("QrScanner", "Error fetching user data: ${e.message}")
                            Toast.makeText(this@QrScannerActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            barcodeView.resume()
                        }
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {}
        })
    }

    private fun startScanning() {
        barcodeView.resume()
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            barcodeView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
