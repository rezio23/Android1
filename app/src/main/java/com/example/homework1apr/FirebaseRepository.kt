package com.example.homework1apr

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    // Using the Realtime Database reference
    private val db = FirebaseDatabase.getInstance().getReference("notes")

    fun signOut() {
        auth.signOut()
    }

    suspend fun login(email: String, pass: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, pass: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addNote(text: String): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        return try {
            val key = db.push().key ?: return Result.failure(Exception("Could not generate key"))
            val noteData = hashMapOf(
                "userId" to userId,
                "text" to text,
                "timestamp" to System.currentTimeMillis()
            )
            db.child(key).setValue(noteData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotes(): Result<List<Note>> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        return try {
            // Fetch notes belonging to the current user
            val snapshot = db.orderByChild("userId").equalTo(userId).get().await()
            val notes = snapshot.children.mapNotNull { child ->
                val note = child.getValue(Note::class.java)
                note?.id = child.key ?: ""
                note
            }
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNote(id: String): Result<Unit> {
        return try {
            db.child(id).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}