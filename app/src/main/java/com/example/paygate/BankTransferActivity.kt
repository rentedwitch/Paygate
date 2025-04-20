package com.example.paygate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BankTransferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_transfer)

        // 🔔 Placeholder functionality for now
        Toast.makeText(this, "Bank Transfer Page Coming Soon!", Toast.LENGTH_SHORT).show()
    }
}
