package com.akash.notes.adapter

import android.icu.text.Transliterator.Position
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.akash.notes.R
import com.akash.notes.ToDoClickInterface

class ToDoListAdapter(var list: ArrayList<TodoList>,
                      var toDoClickInterface: ToDoClickInterface
):BaseAdapter() {
    var isEnabled : Boolean = true
    fun isEnabledTextView(isEnabled : Boolean){
        this.isEnabled = isEnabled
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0L
    }

    override fun getView(position: Int, converview: View?, parent: ViewGroup?): View {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.todo_notes,parent,false)
        var ettitle = view.findViewById<EditText>(R.id.etTitle)
        var checkBox = view.findViewById<CheckBox>(R.id.checkbox)
        ettitle.setText(list[position].task)
        if (isEnabled == false){
            ettitle.isEnabled = false
        }else{
            ettitle.isEnabled = true
        }
        ettitle.doOnTextChanged { text, start, before, count ->
            if (text.toString().length>0){
                text.let {
                    toDoClickInterface.onTextChanged(position,(text?:"").toString())



                }
            }
        }
        checkBox.setOnCheckedChangeListener { compoundButton, isChecked ->
            toDoClickInterface.onCheckboxClick(list[position])

        }
        return view
    }
}