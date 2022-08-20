package com.example.timemanagement.models

import com.google.firebase.Timestamp


data class Task(
    val userId: String? = "",
    var title: String? = "",
    var description: String? = "",
    val tags : List<String> = listOf(),
    val status: Boolean = false,
    val timestamp: Timestamp? = Timestamp.now(),
    val documentId: String? = "",
    val count:Count = Count(),

    )
