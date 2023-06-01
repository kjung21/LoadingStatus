package com.kryptopass.loadingstatus

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourceMetadata(
    var fileName: String,
    var fileTitle: String,
    var repoUrl: String,
    var repoZip: String,
    var status: String
) : Parcelable
