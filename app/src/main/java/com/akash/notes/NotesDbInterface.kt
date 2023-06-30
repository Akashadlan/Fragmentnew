package com.akash.notes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface  NotesDbInterface {
    @Insert
    fun InsertNotes(notesModel: NotesModel)

    @Query("Select * From notesModel")
    fun getNotes() : List<NotesModel>

    @Delete
    fun DeleteNotes(notesModel: NotesModel)

    @Update
    fun updateNoted(notesModel: NotesModel)

    @Query("Select * From notesModel Where id = :id")
    fun getNotesById(id: Int) : NotesModel

}
