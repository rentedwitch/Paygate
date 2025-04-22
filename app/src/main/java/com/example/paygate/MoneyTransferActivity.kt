package com.example.paygate

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MoneyTransferActivity : AppCompatActivity() {

    private lateinit var receiverNameTextView: TextView
    private lateinit var amountEditText: EditText
    private lateinit var confirmAmountEditText: EditText
    private lateinit var sendButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var receiverUid: String? = null
    private var receiverName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money_transfer)

        receiverNameTextView = findViewById(R.id.receiverName)
        amountEditText = findViewById(R.id.amountEditText)
        confirmAmountEditText = findViewById(R.id.confirmAmountEditText)
        sendButton = findViewById(R.id.sendButton)

        // Get data from intent
        receiverUid = intent.getStringExtra("receiverUid")
        receiverName = intent.getStringExtra("receiverName")

        receiverNameTextView.text = "Transfer to: $receiverName"

        sendButton.setOnClickListener {
            val amountStr = amountEditText.text.toString()
            val confirmAmountStr = confirmAmountEditText.text.toString()

            val amount = amountStr.toDoubleOrNull()
            val confirmAmount = confirmAmountStr.toDoubleOrNull()

            if (amount == null || confirmAmount == null || amount <= 0) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amount != confirmAmount) {
                Toast.makeText(this, "Amounts do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val senderUid = auth.currentUser?.uid ?: return@setOnClickListener

            if (receiverUid != null) {
                performTransaction(senderUid, receiverUid!!, amount)
            } else {
                Toast.makeText(this, "Receiver UID missing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performTransaction(senderUid: String, receiverUid: String, amount: Double) {
        val senderRef = db.collection("users").document(senderUid)
        val receiverRef = db.collection("users").document(receiverUid)

        db.runTransaction { transaction ->
            val senderSnapshot = transaction.get(senderRef)
            val receiverSnapshot = transaction.get(receiverRef)

            val senderBalance = senderSnapshot.getDouble("bankBalance") ?: 0.0
            val receiverBalance = receiverSnapshot.getDouble("bankBalance") ?: 0.0

            if (senderBalance >= amount) {
                transaction.update(senderRef, "bankBalance", senderBalance - amount)
                transaction.update(receiverRef, "bankBalance", receiverBalance + amount)
            } else {
                throw Exception("Insufficient balance")
            }
        }.addOnSuccessListener {
            Toast.makeText(this, "Money sent successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Transaction failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
