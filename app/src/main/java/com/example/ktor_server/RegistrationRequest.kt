package com.example.ktor_server

import com.google.gson.annotations.SerializedName


data class RegistrationRequest(
    @SerializedName("userName")
    val userName:String,
    @SerializedName("password")
    val password: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("uniqueKey")
    val uniqueKey: String,
    @SerializedName("secretKey")
    val secretKey: String,
    @SerializedName("icon")
    val icon: Icon,
    @SerializedName("charge")
    val charge: Double)
