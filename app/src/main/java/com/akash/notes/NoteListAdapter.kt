package com.akash.notes

import android.icu.text.Transliterator.Position
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

/**
 * @Author: 017
 * @Date: 24/06/23
 * @Time: 10:28 am
 */
class NotesListAdapter(var list: ArrayList<NotesModel>,var notesClickInterface: NotesClickInterface) : BaseAdapter(){
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return  list[position]
    }

    override fun getItemId(position: Int): Long {
        return 0L
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.note_list_item, parent,false)
        var delete =  view.findViewById<Button>(R.id.btndelete)
        var add = view.findViewById<Button>(R.id.btnadd)

        delete.setOnClickListener {
            notesClickInterface.onDeleteClick(list[position])
        }
        add.setOnClickListener {
            notesClickInterface.onEditClick(list[position])
        }

        var titleNotes = view.findViewById<TextView>(R.id.tvtitle)
        var descNotes = view.findViewById<TextView>(R.id.tvdescription)

        titleNotes.text = "${list[position].title}"
        descNotes.text = "${list[position].description}"
        return view

    }

}