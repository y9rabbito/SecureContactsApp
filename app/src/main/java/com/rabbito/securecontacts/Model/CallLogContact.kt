package com.rabbito.securecontacts.Model

data class CallLogContact(
    val name: String,
    val number: String,
    val typeCall: Int,
    val duration: String,
    val date: String
)