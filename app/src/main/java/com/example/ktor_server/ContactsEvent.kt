package com.example.ktor_server

import com.google.gson.annotations.SerializedName

data class ContactsEvent(
    @SerializedName("type")
    val type:String = "CONTACTS_EVENT",
    @SerializedName("payload")
    val payload: List<Contact>,
)
