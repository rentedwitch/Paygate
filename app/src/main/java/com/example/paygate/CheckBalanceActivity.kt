package com.example.paygate

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CheckBalanceActivity : AppCompatActivity() {

    private lateinit var txtBalance: TextView
    private lateinit var btnRefresh: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_balance)

        txtBalance = findViewById(R.id.txtBalance)
        btnRefresh = findViewById(R.id.btnRefreshBalance)

        btnRefresh.setOnClickListener {
            // Simulate balance fetch
            val fakeBalance = "₹12,340.75"
            txtBalance.text = "Available Balance: $fakeBalance"

            Toast.makeText(this, "Balance Updated", Toast.LENGTH_SHORT).show()
        }
    }
}
