Paygate
Paygate is a collaborative project developed as part of our ITW Lab assignment. The primary objective is to simulate the core functionalities of a digital payment application, similar to Google Pay.

While this project does not aim to replicate the full production-grade experience of existing commercial apps, it serves as a functional prototype showcasing essential features such as:

User registration and authentication

OTP-based login using Firebase (doesn't work apparently as otp authentication needs billing)

QR code scanning for quick transfers

UPI-style money transfer between users

Real-time balance updates via Firebase Firestore (cloud database)

🔧 Tech Stack
Frontend: Android (Kotlin)

Backend: Firebase Authentication & Firestore

Libraries: ZXing (QR Scanner), Material UI Components

⚙️ Features Implemented
Phone number authentication via Firebase OTP (will work if we pay for the paid plan of firebase)

User onboarding with name and default balance setup

QR scanner for seamless user-to-user money transfer

Balance updates for sender and receiver in real-time

Clean and user-friendly interface with gradient backgrounds

📌 Disclaimer
This app is a student project created for academic purposes. It is not intended for real financial transactions and should not be used as a substitute for actual payment platforms.
