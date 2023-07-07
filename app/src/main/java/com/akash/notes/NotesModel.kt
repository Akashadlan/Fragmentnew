package com.akash.notes

import android.icu.text.CaseMap.Title
import android.media.Image
import androidx.room.ColumnInfo
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotesModel(
        @PrimaryKey(autoGenerate = true)
       var id : Int = 0,
        @ColumnInfo
        var title: String? = null,
        @ColumnInfo
        var description: String? = null,
        @ColumnInfo
        var image: String? = null,
        @ColumnInfo
        var priority : Int ?= 0
)
