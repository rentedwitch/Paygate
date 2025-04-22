package com.example.paygate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.FirebaseException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var phoneEditText: EditText
    private lateinit var otpEditText: EditText
    private lateinit var sendOtpButton: Button
    private lateinit var verifyOtpButton: Button

    private lateinit var verificationId: String  // Store the verification ID to verify the OTP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Linking the EditTexts and Buttons to their respective views
        phoneEditText = findViewById(R.id.editPhone)
        otpEditText = findViewById(R.id.editOtp)
        sendOtpButton = findViewById(R.id.btnSendOtp)
        verifyOtpButton = findViewById(R.id.btnVerifyOtp)

        // Check if the user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // If the user is already signed in, navigate to the HomePageActivity
            navigateToHomePage()
        }

        // Send OTP button click handler
        sendOtpButton.setOnClickListener {
            val phoneNumber = phoneEditText.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sendOtp(phoneNumber)
            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }

        // Verify OTP button click handler
        verifyOtpButton.setOnClickListener {
            val otp = otpEditText.text.toString().trim()
            if (otp.isNotEmpty()) {
                verifyOtp(otp)
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to send OTP to the user's phone number
    private fun sendOtp(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to which OTP is sent
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)                 // Activity for callback
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Automatically sign in the user if verification is successful
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Handle failure cases like invalid phone number, etc.
                    Toast.makeText(this@MainActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, token)
                    // Save the verification ID to use it when verifying the OTP
                    this@MainActivity.verificationId = verificationId
                    Toast.makeText(this@MainActivity, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Function to verify OTP entered by the user
    private fun verifyOtp(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithCredential(credential)
    }

    // Function to sign in with the verified phone number
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in successful, navigate to the HomePageActivity
                    val user = task.result?.user
                    if (user != null) {
                        checkIfUserExists(user)
                    }
                } else {
                    val e = task.exception
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Authentication failed: ${e?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    // Check if the user is already registered in Firestore, if not, create a new document
    private fun checkIfUserExists(user: FirebaseUser) {
        val userRef = firestore.collection("users").document(user.uid)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // User exists, navigate to the home page
                navigateToHomePage()
            } else {
                // User does not exist, create a new document with bank balance
                val userData = mapOf(
                    "uid" to user.uid,
                    "bankBalance" to 5000,
                    "name" to user.displayName // Assuming the user has a name
                )
                userRef.set(userData).addOnSuccessListener {
                    // User created, navigate to the home page
                    navigateToHomePage()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error saving user data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Function to navigate to HomePageActivity after successful login
    private fun navigateToHomePage() {
        val intent = Intent(this, HomePageActivity::class.java)
        startActivity(intent)
        finish()
    }
}
