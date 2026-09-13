package com.udb.travelgo.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.udb.travelgo.model.Destination
import kotlinx.coroutines.tasks.await
import java.util.UUID

object DestinationRepository {

    private const val COLLECTION = "destinations"
    private const val STORAGE_FOLDER = "destination_images"

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    fun logout() = auth.signOut()

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun uploadImage(imageUri: Uri): String {
        val fileName = "${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child("$STORAGE_FOLDER/$fileName")
        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }

    fun listenToDestinations(
        onChange: (List<Destination>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        val uid = currentUserId ?: ""
        return db.collection(COLLECTION)
            .whereEqualTo("userId", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Destination::class.java)?.apply { id = doc.id }
                } ?: emptyList()
                onChange(list)
            }
    }

    suspend fun addDestination(destination: Destination) {
        db.collection(COLLECTION).add(destination.toMap()).await()
    }

    suspend fun updateDestination(destination: Destination) {
        db.collection(COLLECTION).document(destination.id).set(destination.toMap()).await()
    }

    suspend fun deleteDestination(destinationId: String) {
        db.collection(COLLECTION).document(destinationId).delete().await()
    }

    suspend fun getDestination(destinationId: String): Destination? {
        val doc = db.collection(COLLECTION).document(destinationId).get().await()
        return doc.toObject(Destination::class.java)?.apply { id = doc.id }
    }
}
