package com.akash.notes

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akash.notes.databinding.FragmentAddNotesBinding

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddNotesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddNotesFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentAddNotesBinding
    lateinit var notesDb: NotesDb
    lateinit var mainActivity: MainActivity
   private var id = -1
    var notes = NotesModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
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
        binding = FragmentAddNotesBinding.inflate(layoutInflater)
        notesDb = NotesDb.getDatabase(mainActivity)

        arguments?.let {
            id = it.getInt("id")
            if (id>-1){
                getEntity()
            }
        }

        binding.btnsave.setOnClickListener {
            if (binding.ettitle.text.toString().isNullOrBlank()) {
                binding.ettitle.error = mainActivity.resources.getString(R.string.Title)
            } else if (binding.etdescription.text.toString().isNullOrBlank()) {
                binding.etdescription.error =
                    mainActivity.resources.getString(R.string.Description)
            }else if(id>-1) {
                var note = NotesModel(id = id, title= binding.ettitle.text.toString(),description = binding.etdescription.text.toString())
                class UpdateNotes : AsyncTask<Void, Void, Void>() {
                    override fun doInBackground(vararg p0: Void?): Void? {
                        notesDb.notesdbinterface().updateNoted(note)

                        return null
                    }
                    override fun onPostExecute(result: Void?) {
                        super.onPostExecute(result)
                        mainActivity.navController.popBackStack()

                    }
                }
                UpdateNotes().execute()}
            else {
                var note = NotesModel(title= binding.ettitle.text.toString(),description=  binding.etdescription.text.toString())
                class InsertNotes : AsyncTask<Void, Void, Void>() {
                    override fun doInBackground(vararg p0: Void?): Void? {
                        notesDb.notesdbinterface().InsertNotes(note)
                        return null
                    }
                    override fun onPostExecute(result: Void?) {
                        super.onPostExecute(result)
                        mainActivity.navController.popBackStack()

                    }
                }
                InsertNotes().execute()
               // mainActivity.notesList.add(note)
        }


        }
        return binding.root
        }

    fun getEntity(){
        class getNotes : AsyncTask<Void,Void,Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                notes = notesDb.notesdbinterface().getNotesById(id)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                binding.ettitle.setText(notes.title)
                binding.etdescription.setText(notes.description)
                binding.btnsave.setText("Update")

            }

        }
       getNotes().execute()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddNotesFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddNotesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
