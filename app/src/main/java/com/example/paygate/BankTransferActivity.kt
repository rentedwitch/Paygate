package com.example.paygate

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BankTransferActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_transfer)

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance()
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Get references to EditText fields
        val editAccountName = findViewById<EditText>(R.id.editAccountName)
        val editAmount = findViewById<EditText>(R.id.editAmount)

        // When user clicks the Send button
        val btnSendBankTransfer = findViewById<Button>(R.id.btnSendBankTransfer)
        btnSendBankTransfer.setOnClickListener {
            // Get the values entered in the fields
            val bankingName = editAccountName.text.toString().trim()
            val amount = editAmount.text.toString().trim()

            // Check if both fields are not empty
            if (bankingName.isNotEmpty() && amount.isNotEmpty()) {
                // Convert amount to double
                val amountToSend = amount.toDoubleOrNull()

                if (amountToSend != null && amountToSend > 0) {
                    // Fetch the current balance from Firestore and update
                    fetchCurrentBalanceAndUpdate(bankingName, amountToSend)
                } else {
                    // Show a warning if the amount is invalid
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show a warning if fields are empty
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fetch current balance from Firestore and update after deduction
    private fun fetchCurrentBalanceAndUpdate(bankingName: String, amountToSend: Double) {
        val userDocRef = db.collection("users").document(currentUserId)

        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Get the current balance from the document
                val currentBalance = document.getDouble("bankBalance") ?: 0.0

                if (currentBalance >= amountToSend) {
                    // Subtract the amount from the current balance
                    val updatedBalance = currentBalance - amountToSend

                    // Update the balance in Firestore
                    userDocRef.update("bankBalance", updatedBalance)
                        .addOnSuccessListener {
                            // Successfully updated the balance
                            val message = "Transferred ₹$amountToSend to $bankingName"
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
