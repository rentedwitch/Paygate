package com.example.paygate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // ✅ If already signed in, go directly to HomePage
        if (auth.currentUser != null) {
            val intent = Intent(this, HomePageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }

        setContentView(R.layout.activity_main)

        val phoneNumberInput = findViewById<EditText>(R.id.editPhone)
        val otpInput = findViewById<EditText>(R.id.editOtp)
        val sendOtpButton = findViewById<Button>(R.id.btnSendOtp)
        val verifyOtpButton = findViewById<Button>(R.id.btnVerifyOtp)

        otpInput.isEnabled = false
        verifyOtpButton.isEnabled = false

        sendOtpButton.setOnClickListener {
            val phoneNumber = phoneNumberInput.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sendOtp(phoneNumber, otpInput, verifyOtpButton)
            } else {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
            }
        }

        verifyOtpButton.setOnClickListener {
            val otp = otpInput.text.toString().trim()
            if (otp.isNotEmpty()) {
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendOtp(phoneNumber: String, otpInput: EditText, verifyOtpButton: Button) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@MainActivity, "Verification Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(id, token)
                    verificationId = id
                    otpInput.isEnabled = true
                    verifyOtpButton.isEnabled = true
                    Toast.makeText(this@MainActivity, "OTP sent successfully ✅", Toast.LENGTH_SHORT).show()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "OTP verified successfully 🎉", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomePageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "OTP verification failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
