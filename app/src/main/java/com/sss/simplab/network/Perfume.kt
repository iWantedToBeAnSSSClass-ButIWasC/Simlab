package com.sss.simplab.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Perfume (
    val name: String,
    val content: String,
    val url: String
) : Parcelable