package com.akash.notes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.akash.notes.adapter.TodoList

@Database(entities = [NotesModel::class,TodoList::class], version = 1)
abstract class NotesDb : RoomDatabase(){
    abstract fun notesdbinterface() : NotesDbInterface
    companion object{
        var notesDb : NotesDb? = null
        @Synchronized
        fun getDatabase(context:Context):NotesDb{
            if (notesDb == null){
                notesDb = Room.databaseBuilder(context,NotesDb::class.java,
                    context.resources.getString(R.string.app_name)).build()
            }
            return notesDb!!
        }
    }
}