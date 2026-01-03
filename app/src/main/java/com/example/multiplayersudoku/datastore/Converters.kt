package com.example.multiplayersudoku.datastore

import androidx.room.TypeConverter
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore

class Converters {
    @TypeConverter
    fun fromDocumentReferenceToString(ref: DocumentReference?): String? {
        return ref?.toString()
    }

    @TypeConverter
    fun fromStringToDocumentReference(str: String?): DocumentReference? {
        val db = Firebase.firestore

        if (str == null) return null
        return db.collection("users").document(str)
    }
}