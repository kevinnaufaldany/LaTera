package com.pam.latera.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.pam.latera.data.ChatMessage

class FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()
    private val messagesCollection = db.collection("messages")

    fun saveMessage(message: ChatMessage) {
        messagesCollection.add(message)
    }
}
