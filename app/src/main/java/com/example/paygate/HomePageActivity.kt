package com.example.paygate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomePageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        auth = FirebaseAuth.getInstance()

        // Check if user is logged in, if not navigate to login screen
        if (auth.currentUser == null) {
            navigateToLoginScreen()
        }

        // QR Scanner Button
        findViewById<Button>(R.id.btnQrScanner).setOnClickListener {
            startActivity(Intent(this, QrScannerActivity::class.java))
        }

        // Bank Transfer Button
        findViewById<Button>(R.id.btnBankTransfer).setOnClickListener {
            startActivity(Intent(this, BankTransferActivity::class.java))
        }

        // UPI Mobile Transfer Button
        findViewById<Button>(R.id.btnUpiMobile).setOnClickListener {
            startActivity(Intent(this, UpiMobileTransferActivity::class.java))
        }

        // Check Balance Button
        findViewById<Button>(R.id.btnCheckBalance).setOnClickListener {
            startActivity(Intent(this, CheckBalanceActivity::class.java))
        }

        // Logout Button
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            // Sign out the user from Firebase
            auth.signOut()

            // Show a toast to notify the user of successful logout
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Navigate back to MainActivity (login screen)
            navigateToLoginScreen()
        }
    }

    // Function to navigate to the login screen
    private fun navigateToLoginScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()  // Finish current activity so the user cannot go back to it
    }
}
