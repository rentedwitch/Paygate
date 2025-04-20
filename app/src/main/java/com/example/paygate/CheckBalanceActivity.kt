package com.example.paygate

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CheckBalanceActivity : AppCompatActivity() {

    private lateinit var txtBalance: TextView
    private lateinit var btnRefresh: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_balance)

        txtBalance = findViewById(R.id.txtBalance)
        btnRefresh = findViewById(R.id.btnRefreshBalance)

        // On button click, fetch balance from Firestore
        btnRefresh.setOnClickListener {
            fetchBalance()
        }
    }

    private fun fetchBalance() {
        // Get the current user UID from Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            // Get reference to Firestore
            val db = FirebaseFirestore.getInstance()

            // Reference to the user's document in Firestore
            val userDocRef = db.collection("users").document(uid)

            // Fetch the user's document
            userDocRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Retrieve the bankBalance field
                    val bankBalance = documentSnapshot.getDouble("bankBalance")

                    if (bankBalance != null) {
                        // Update the TextView with the balance
                        txtBalance.text = "Available Balance: ₹${bankBalance.toString()}"
                    } else {
                        // If the field does not exist
                        Toast.makeText(this, "Bank Balance not found.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // If the document doesn't exist
                    Toast.makeText(this, "User document not found.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                // If there was an error fetching the document
                Toast.makeText(this, "Error fetching balance: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // If the user is not logged in
            Toast.makeText(this, "User is not logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}
