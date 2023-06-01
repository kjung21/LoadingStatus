package com.kryptopass.loadingstatus

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourceMetadata (
    val fileName: String,
    val fileTitle: String,
    val repoUrl: String,
    val repoZip: String,
    val status: String): Parcelable
