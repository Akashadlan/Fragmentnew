package com.akash.notes

import android.icu.text.Transliterator.Position
import com.akash.notes.adapter.TodoList

interface NotesClickInterface {
    fun onDeleteClick(notesModel: NotesModel)
    fun onEditClick(notesModel: NotesModel)
}

interface ToDoClickInterface{
    fun onCheckboxClick(todoList: TodoList)
    fun onTextChanged(position: Int, text: String)
}