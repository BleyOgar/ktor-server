package com.example.ktor_server

import com.google.gson.annotations.SerializedName


data class Icon(
    @SerializedName("setId")
    val setId: String,
    @SerializedName("iconId")
    val iconId: String)
