package com.example.paygate

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.journeyapps.barcodescanner.BarcodeEncoder

class MyQrActivity : AppCompatActivity() {

    private lateinit var qrImageView: ImageView
    private lateinit var userNameTextView: TextView  // Declare the TextView for the user's name
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_qr)

        qrImageView = findViewById(R.id.qrImageView)  // Ensure the ImageView exists in your XML
        userNameTextView = findViewById(R.id.userNameTextView)  // Find the TextView for the name
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Fetch the user's name from Firestore and generate the QR code
        fetchUserNameAndGenerateQr()
    }

    private fun fetchUserNameAndGenerateQr() {
        val userUid = auth.currentUser?.uid
        if (userUid == null) {
            // Handle the case when no user is logged in
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch user's name from Firestore
        firestore.collection("users").document(userUid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("name") ?: "User"  // Default to "User" if no name is found
                    userNameTextView.text = "$userName"  // Set the user's name

                    // After setting the name, generate the QR code
                    generateQrCode(userUid)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch user name: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateQrCode(userUid: String) {
        try {
            // Create the BarcodeEncoder to generate a QR code
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(userUid, com.google.zxing.BarcodeFormat.QR_CODE, 800, 800)

            // Set the QR code as the image source for the ImageView
            qrImageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the error (maybe show a Toast or Snackbar)
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show()
        }
    }
}
