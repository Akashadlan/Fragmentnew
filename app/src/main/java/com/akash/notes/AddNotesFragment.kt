package com.akash.notes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.biometrics.BiometricManager.Strings
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import com.akash.notes.adapter.ToDoListAdapter
import com.akash.notes.adapter.TodoList
import com.akash.notes.databinding.FragmentAddNotesBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import android.util.Base64
import java.util.Calendar

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddNotesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddNotesFragment : Fragment(), ToDoClickInterface {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentAddNotesBinding
    lateinit var notesDb: NotesDb
    lateinit var mainActivity: MainActivity
    private var noteID = -1
    var notes = NotesModel()
    lateinit var toDolistAdapter: ToDoListAdapter
    var todoList = ArrayList<TodoList>()
    var uri: Uri? = null


    var requestResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            imagePicker.launch("image/*")
        } else {
            // alert dialog
        }
    }
    var imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            uri = it
            binding.image.setImageURI(it)
        }
    }

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
        binding = FragmentAddNotesBinding.inflate(layoutInflater)

        arguments?.let {
            noteID = it.getInt("id")
            if (noteID > -1) {
                getEntity()
            }
        }
        toDolistAdapter = ToDoListAdapter(todoList, this)
        binding.lvTodo.adapter = toDolistAdapter

        binding.btntodo.setOnClickListener {
            todoList.add(TodoList())
            toDolistAdapter.notifyDataSetChanged()
        }
        binding.image.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    mainActivity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    imagePicker.launch("image/*")
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)->{
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", "com.akash.notes", null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestResult.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }

        binding.btnsave.setOnClickListener {
            if (binding.ettitle.text.toString().isNullOrBlank()) {
                binding.ettitle.error = mainActivity.resources.getString(R.string.Title)
            } else if (binding.etdescription.text.toString().isNullOrBlank()) {
                binding.etdescription.error =
                    mainActivity.resources.getString(R.string.Description)
            } else if (noteID > -1) {
                var note = NotesModel(
                    id = noteID,
                    title = binding.ettitle.text.toString(),
                    description = binding.etdescription.text.toString())
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
                UpdateNotes().execute()

            } else {
                var note = NotesModel(
                    title = binding.ettitle.text.toString(),
                    description = binding.etdescription.text.toString())
                if(uri != null){
                    var bitmap = MediaStore.Images.Media.getBitmap(mainActivity.contentResolver,uri)

                    note.image = encodeToBase64(bitmap)?:""
                }

                class insertNotes : AsyncTask<Void, Void, Void>() {
                    override fun doInBackground(vararg p0: Void?): Void? {
                        notesDb.notesdbinterface().InsertNotes(notes)
                        return null
                    }

                    override fun onPostExecute(result: Void?) {
                        super.onPostExecute(result)
                        mainActivity.navController.popBackStack()

                    }
                }
                insertNotes().execute()
            }
            // mainActivity.notesList.add(note)
        }
        binding.tvdate.setOnClickListener {
            var datePicker = DatePickerDialog(
                mainActivity, { _, year, month, date ->
                    var simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy")
                    var calendar = Calendar.getInstance()
                    calendar.set(year, month, date)
                    var selectedDate = simpleDateFormat.format(calendar.time)
                    binding.tvdate.setText("$selectedDate")
                }, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DATE)
            )
            datePicker.show()
        }
        binding.tvtime.setOnClickListener {
            var timePicker = TimePickerDialog(
                mainActivity, { _, hours, minutes, ->
                    var simpleTimePicker = SimpleDateFormat("hh-mm aa")
                    var calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR, hours)
                    calendar.set(Calendar.MINUTE, minutes)

                    var selectedTime = simpleTimePicker.format(calendar.time)
                    binding.tvtime.setText("$selectedTime")
                }, Calendar.getInstance().get(Calendar.HOUR),
                Calendar.getInstance().get(Calendar.MINUTE), true
            )
            timePicker.show()

        }
        return binding.root
    }

    fun getEntity() {
        class getNotes : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                notes = notesDb.notesdbinterface().getNotesById(noteID)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                binding.ettitle.setText(notes.title)
                binding.etdescription.setText(notes.description)
                binding.btnsave.setText("Update")
                if (notes.image != null) {
                    binding.image.setImageBitmap(decodeBase64(notes.image))
                }
                getTodoList()
                toDolistAdapter.isEnabledTextView(false)
            }
        }
        getNotes().execute()
    }
    fun getTodoList(){
        class toDoEntity : AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                todoList.addAll((notesDb.notesdbinterface().getTodoById(id)))

                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                toDolistAdapter.notifyDataSetChanged()
                toDolistAdapter.isEnabledTextView(false)
            }
        }
        toDoEntity().execute()
    }
     private fun addTodo(notesId: Long) {
         for (items in todoList) {
             items.notesid = noteID.toInt()
             class insertClass : AsyncTask<Void, Void, Void>(){
                 override fun doInBackground(vararg p0: Void?): Void? {
                     notesDb.notesdbinterface().insertTodo(items)
                     return null
                 }
                 override fun onPostExecute(result: Void?) {
                     super.onPostExecute(result)
                     mainActivity.navController.popBackStack()
                 }
                 }
             insertClass().execute()
         }
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
    override fun onCheckboxClick(todoList: TodoList) {
    }

    override fun onTextChanged(position: Int, text: String) {
        todoList[position].task = text ?: ""
    }
    //use this method to convert the selected image to bitmap to save in database
    fun encodeToBase64(image: Bitmap): String? {
        var imageEncoded: String = ""
        var imageConverted = getResizedBitmap(image, 500)
        val baos = ByteArrayOutputStream()
        imageConverted?.let {
            imageConverted.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b: ByteArray = baos.toByteArray()
           // imageEncoded = Base64.encodeToString(b, Base64.DEFAULT)
        }


        return imageEncoded
    }

    //use this method to convert the saved string to bitmap
    fun decodeBase64(input: String?): Bitmap? {
        val decodedByte: ByteArray = Base64.decode(input, 0)
        return BitmapFactory
            .decodeByteArray(decodedByte, 0, decodedByte.size)
    }
    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (width * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }
}
