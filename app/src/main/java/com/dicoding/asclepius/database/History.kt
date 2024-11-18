package com.dicoding.asclepius.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity
@Parcelize
data class History(
    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "resultLabel")
    var resultLabel: String? = null,

    @ColumnInfo(name = "confidenceScore")
    var confidenceScore: Float = 0f,

    @ColumnInfo(name = "image")
    var image: String? = null

) : Parcelable