package com.akash.notes

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import com.akash.notes.databinding.ActivityMainBinding
import com.akash.notes.databinding.NotesListFragmentBinding

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotesListFragment : Fragment(), NotesClickInterface {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: NotesListFragmentBinding
    lateinit var mainActivity: MainActivity
    var notesList = ArrayList<NotesModel>()
    lateinit var notesDb: NotesDb
    lateinit var notesListAdapter: NotesListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
        notesDb = NotesDb.getDatabase(mainActivity)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = NotesListFragmentBinding.inflate(layoutInflater)
        notesListAdapter = NotesListAdapter(notesList,this)
        binding.lvlist.adapter= notesListAdapter



        binding.btnadd.setOnClickListener {
            mainActivity.navController.navigate(R.id.action_notesListFragment_to_addNotesFragment)
        }
        getNotes()
        return binding.root
    }

    fun getNotes(){
        notesList.clear()
        notesListAdapter.notifyDataSetChanged()
        class getNotesClass : AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                notesList.addAll(notesDb.notesdbinterface().getNotes())
            return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                notesListAdapter.notifyDataSetChanged()
            }
        }
        getNotesClass().execute()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDeleteClick(notesModel: NotesModel) {
        AlertDialog.Builder(mainActivity)
            .setTitle(mainActivity.resources.getString(R.string.Delete))
            .setMessage(mainActivity.resources.getString(R.string.Delete_msg))
            .setPositiveButton(mainActivity.resources.getString(R.string.yes)){_,_->
                class delete: AsyncTask<Void, Void ,Void>(){
                    override fun doInBackground(vararg p0: Void?): Void? {
                        notesDb.notesdbinterface().DeleteNotes(notesModel)
                        return null
                    }

                    override fun onPostExecute(result: Void?) {
                        super.onPostExecute(result)
                        getNotes()
                    }

                }
                delete().execute()
            }
            .setNegativeButton(mainActivity.resources.getString(R.string.no)){_,_->
            }
            .show()
    }


    override fun onEditClick(notesModel: NotesModel) {
        var bundle = Bundle()
        bundle.putInt("id",notesModel.id)
        mainActivity.navController.navigate(R.id.addNotesFragment,bundle)    }
}