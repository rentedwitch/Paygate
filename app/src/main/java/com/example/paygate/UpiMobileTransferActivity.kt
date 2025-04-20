package com.example.paygate

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpiMobileTransferActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upi_mobile_transfer)

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance()
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Initialize views
        val editMobile = findViewById<EditText>(R.id.editMobile)
        val editAmount = findViewById<EditText>(R.id.editAmount)
        val btnSend = findViewById<Button>(R.id.btnSendUpi)

        // Get the phone number passed from either HomePageActivity or contact picker
        val contactNumber = intent.getStringExtra("contactNumber")
        val selectedMobile = intent.getStringExtra("selectedMobile")

        // Set the phone number into the mobile EditText if available
        contactNumber?.let {
            editMobile.setText(it)
        }

        selectedMobile?.let {
            editMobile.setText(it.replace("\\s".toRegex(), "")) // Remove any spaces
        }

        // Send UPI button click listener
        btnSend.setOnClickListener {
            val mobile = editMobile.text.toString().trim()  // Declare 'mobile' here
            val amount = editAmount.text.toString().trim()

            // Validate the mobile number and amount
            if (mobile.isEmpty() || !isValidMobile(mobile)) {
                Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show()
            } else if (amount.isEmpty() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            } else {
                // Fetch the current balance from Firestore
                fetchCurrentBalanceAndUpdate(mobile, amount)  // Pass 'mobile' and 'amount' to this method
            }
        }
    }

    // Function to validate mobile number format
    private fun isValidMobile(mobile: String): Boolean {
        return Patterns.PHONE.matcher(mobile).matches() && mobile.length == 10
    }

    // Fetch current balance from Firestore and update after deduction
    private fun fetchCurrentBalanceAndUpdate(mobile: String, amount: String) {
        val userDocRef = db.collection("users").document(currentUserId)

        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Get the current balance from the document
                val currentBalance = document.getDouble("bankBalance") ?: 0.0

                // Convert the entered amount to Double
                val amountToSend = amount.toDouble()

                if (currentBalance >= amountToSend) {
                    // Subtract the amount from the current balance
                    val updatedBalance = currentBalance - amountToSend

                    // Update the balance in Firestore
                    userDocRef.update("bankBalance", updatedBalance)
                        .addOnSuccessListener {
                            // Successfully updated the balance
                            Toast.makeText(this, "Successfully sent ₹$amount to $mobile", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // Error updating balance
                            Toast.makeText(this, "Error updating balance: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // If balance is insufficient
                    Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Document doesn't exist
                Toast.makeText(this, "User document not found.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            // Error fetching user data
            Toast.makeText(this, "Error fetching balance: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
