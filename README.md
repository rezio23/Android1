# Homework1Apr Android App

An Android notes app built with Kotlin, Firebase Authentication, and Cloud Firestore.

## Features

- User registration and login with Firebase Auth
- Add personal notes
- Load notes for the current signed-in user
- Delete notes with a long press
- Sign out support

## Tech Stack

- Kotlin
- Android SDK (minSdk 24, targetSdk 34)
- Firebase Authentication
- Firebase Firestore
- Kotlin Coroutines

## Project Structure

- `app/src/main/java/com/example/homework1apr/`
  - `LoginActivity.kt`
  - `RegisterActivity.kt`
  - `MainActivity.kt`
  - `FirebaseRepository.kt`
  - `Note.kt`

## Setup

1. Open the project in Android Studio.
2. Create a Firebase project and enable:
   - Email/Password in Firebase Authentication
   - Cloud Firestore database
3. Download `google-services.json` from Firebase Console.
4. Place the file at `app/google-services.json`.
5. Sync Gradle and run the app.

## Run

- Run from Android Studio on an emulator or Android device.
- Launcher activity: `LoginActivity`.
