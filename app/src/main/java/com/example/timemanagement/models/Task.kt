package com.example.timemanagement.models

import com.google.firebase.Timestamp


data class Task(
    val userId: String? = "",
    var title: String? = "",
    var description: String? = "",
    val timestamp: Timestamp? = Timestamp.now(),

    val documentId: String? = "",

    )
