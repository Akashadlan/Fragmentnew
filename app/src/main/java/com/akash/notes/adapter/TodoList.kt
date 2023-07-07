package com.akash.notes.adapter

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.akash.notes.NotesModel

@Entity (foreignKeys = [ForeignKey(
    entity = NotesModel::class,
    parentColumns = ["id"],
    childColumns = ["notesid"],
onDelete = ForeignKey.CASCADE)])
class TodoList{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo()
    var notesid : Int? = 0
    @ColumnInfo()
    var task: String?= null
    @ColumnInfo()
    var iscomleted : Boolean? = null
}