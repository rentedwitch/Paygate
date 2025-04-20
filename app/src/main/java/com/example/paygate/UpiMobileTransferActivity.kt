package com.example.paygate

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UpiMobileTransferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upi_mobile_transfer)

        val editMobile = findViewById<EditText>(R.id.editMobile)
        val editAmount = findViewById<EditText>(R.id.editAmount)
        val btnSend = findViewById<Button>(R.id.btnSendUpi)

        btnSend.setOnClickListener {
            val mobile = editMobile.text.toString()
            val amount = editAmount.text.toString()

            if (mobile.isNotEmpty() && amount.isNotEmpty()) {
                // TODO: Implement actual UPI transfer logic here
                Toast.makeText(this, "Sending ₹$amount to $mobile via UPI", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter valid mobile number and amount", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
