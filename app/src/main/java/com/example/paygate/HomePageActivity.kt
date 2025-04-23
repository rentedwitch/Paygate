package com.example.paygate

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomePageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val REQUEST_CONTACT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Check if user is logged in, if not navigate to login screen
        if (auth.currentUser == null) {
            navigateToLoginScreen()
        }

        // Fetch the user's name from Firestore
        fetchUserName()

        // QR Scanner Button
        findViewById<Button>(R.id.btnQrScanner).setOnClickListener {
            startActivity(Intent(this, QrScannerActivity::class.java))
        }

        // My QR Button
        findViewById<Button>(R.id.btnMyQR).setOnClickListener {
            // Start MyQrActivity when the button is clicked
            startActivity(Intent(this, MyQrActivity::class.java))
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

        // Add Money Button
        findViewById<Button>(R.id.btnAddMoney).setOnClickListener {
            startActivity(Intent(this, AddMoneyActivity::class.java))
        }

        // Contacts Button
        findViewById<Button>(R.id.btnContacts).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(intent, REQUEST_CONTACT)
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

    // Fetch user's name from Firestore
    private fun fetchUserName() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name")
                        val greetingTextView = findViewById<TextView>(R.id.greetingText)
                        greetingTextView.text = "Hi, $userName"  // Set the greeting message
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching user info: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    // Handle contact selection result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CONTACT && resultCode == RESULT_OK) {
            data?.data?.let { contactUri ->
                val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                contentResolver.query(contactUri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val number = cursor.getString(numberIndex)

                        // Now pass it to UpiMobileTransferActivity
                        val upiIntent = Intent(this, UpiMobileTransferActivity::class.java)
                        upiIntent.putExtra("selectedMobile", number)
                        startActivity(upiIntent)
                    }
                }
            }
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
