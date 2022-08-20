package com.example.timemanagement.models

import com.google.firebase.Timestamp

data class Count(
    var resumeTime:Timestamp = Timestamp.now(),
    var pauseTime:Timestamp = Timestamp.now(),
    var countTimeInSeconds:Long = 0,
    var isInit: Boolean = false
    )
