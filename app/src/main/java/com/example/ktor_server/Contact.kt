package com.example.ktor_server

import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("id")
    val id:String,
    @SerializedName("userName")
    val userName:String,
    @SerializedName("type")
    val type:String,
    @SerializedName("isAlive")
    val isAlive:Boolean,
    @SerializedName("icon")
    val icon: Icon,
    @SerializedName("charge")
    val charge: Double
)
