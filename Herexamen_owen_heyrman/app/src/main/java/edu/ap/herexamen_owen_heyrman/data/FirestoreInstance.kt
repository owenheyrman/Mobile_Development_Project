package edu.ap.herexamen_owen_heyrman.data

import com.google.firebase.firestore.FirebaseFirestore

object FirestoreInstance {
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}