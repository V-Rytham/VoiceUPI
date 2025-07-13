# Voice-Enabled UPI Payment App (VoiceUPI) 🚀🔊💸

> **Empowering effortless and secure digital payments with natural voice commands, tailored for all users — especially senior citizens.**

---

## 🔥 Problem Statement

In a fast-paced digital world, mobile payments are everywhere — but they remain inaccessible or complicated for many:

- **Senior citizens** often struggle with navigating multiple steps and tiny buttons on payment apps.
- Users with **visual impairments** or limited tech experience face hurdles using traditional UPI apps.
- Multitasking or on-the-go users need **hands-free, quick payment solutions**.

---

## 💡 Our Solution

**VoiceUPI** is a **mobile-first Android MVP** that allows users to:

- Make UPI payments by simply **speaking commands** like "Send 500 rupees to Ravi."
- Check account balance through natural voice queries.
- Benefit from a **safety mechanism** where senior users’ high-value transactions trigger an SOS alert to a trusted guardian for approval — combining convenience with security.

---

## 🎯 Key Features

| Feature                          | Description                                                      |
|---------------------------------|------------------------------------------------------------------|
| 🎙️ Voice Command Input          | Activate microphone with a single tap to speak payment or balance queries. Uses AI-powered intent extraction for accuracy. |
| 💸 UPI Deep Linking             | Opens trusted UPI apps (Google Pay, PhonePe) with pre-filled details for seamless payment completion. |
| 🧓🏼 Senior Citizen Safety       | Customizable transaction limit triggers SOS alerts to guardians who can approve or deny transactions remotely. |
| 💰 Mock Balance Display         | Shows real-time-like balance info on voice command (mocked for MVP). |
| ⚙️ User Settings               | Age, trusted contact, and transaction threshold input for personalized safety and experience. |
| 🛠️ Local Storage               | Saves user preferences securely on device without backend dependencies. |
| 🎨 Minimal & Intuitive UI       | Clean interface designed for easy use by all age groups. |

---

## 🚀 Tech Stack & Tools

| Layer                 | Technology / Service                       |
|-----------------------|------------------------------------------|
| Mobile Development    | Android Studio (Java/Kotlin)              |
| Voice Input           | Android Speech-to-Text API / OpenAI Whisper (planned) |
| Intent Extraction     | OpenAI GPT-3.5 / Gemini API (mocked currently) |
| Payment Processing    | UPI Deep Linking via Android Intents     |
| Data Storage          | SharedPreferences / Local Database        |
| Communication         | Mocked SMS/WhatsApp alerts for guardian approval |
| Version Control       | Git & GitHub                             |

---

## 🛠️ How to Run Locally

1. **Clone the repo:**

   ```bash
   git clone https://github.com/V-Rytham/VoiceUPI.git
   cd VoiceUPI
Open in Android Studio:
Import project and sync Gradle.

Build and run:
Run on a physical Android device or emulator with microphone support.

Usage:
Tap the big microphone button, speak commands like:

"Send 200 rupees to Anjali"

"What is my balance?"
For senior users, transactions above the set limit trigger an alert to a trusted contact.

📈 Future Roadmap
- Real backend integration: Connect to real user bank accounts and validate transactions securely.
- Voice biometric authentication: Add speaker recognition for enhanced security.
- Multilingual support: Support commands in multiple Indian languages.
- Real SMS/WhatsApp integration: Use Twilio or similar services for guardian alerts.
- Machine learning models: Fine-tune intent extraction and natural language understanding
- Accessibility improvements: Add screen reader support and customizable UI sizes.

📝 License
This project is licensed under the MIT License - see the LICENSE file for details.

🙏 Acknowledgments
OpenAI for AI APIs

Android Open Source community

UPI developers and Google Pay for deep linking inspiration

Hackathon organizers and mentors

🚀 Let’s make payments easier for everyone — one voice command at a time!
