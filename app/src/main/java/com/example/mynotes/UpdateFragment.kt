package com.example.mynotes

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mynotes.databinding.FragmentUpdateBinding
import com.example.mynotes.db.NoteDataBase
import com.example.mynotes.models.NoteData
import com.example.mynotes.utils.hideKeybord
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class UpdateFragment : Fragment() {

    val TAG = "UpdateFragment"

    private var _binding:FragmentUpdateBinding? = null
    private val binding:FragmentUpdateBinding
    get() = _binding!!

    private lateinit var result:String
    private val calendar = Calendar.getInstance()
    private lateinit var navControler:NavController
    private var noteData:NoteData?=null
    private lateinit var formattedDate:String
    private val args:UpdateFragmentArgs by navArgs()
    val noteviewModel by viewModels<NoteViewModels>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater)
        val animation = MaterialContainerTransform().apply {
            drawingViewId = android.R.id.content
            scrimColor = Color.TRANSPARENT
            duration = 300L
        }
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navControler = Navigation.findNavController(view)
        val activity = activity as MainActivity


        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formattedDate = dateFormat.format(Date())
        binding.noteEditedOn.text = "Edited on:${formattedDate.toString()}"

        ViewCompat.setTransitionName(
            binding.noteContentFragmentParent,
            "recyclerView_${args.note?.id}"
        )

        binding.backButton.setOnClickListener {
            requireView().hideKeybord()
            navControler.popBackStack()

        }

        binding.noteSaveButton.setOnClickListener {
            dataFetch()
        }
        try {
            binding.noteEdit.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    binding.markStyle.visibility = View.VISIBLE
                    binding.noteEdit.setStylesBar(binding.markStyle)
                }else{
                    binding.markStyle.visibility = View.GONE
                }
            }
        }catch (e:Throwable){
            Log.d(TAG,e.stackTraceToString())
        }

        setUpNote()
    }

    private fun setUpNote() {
        val note = args.note
        val title = binding.titleEdit
        val content = binding.noteEdit
        val lastEdited = binding.noteEditedOn

        if (note==null){
            binding.noteEditedOn.text = formattedDate
        }
        if(note!=null){
            title.setText(note.title)
            content.renderMD(note.note)
            lastEdited.text = formattedDate
            binding.apply {
                CoroutineScope(Dispatchers.IO).launch{

                }
            }
        }
    }

    private fun dataFetch() {


        val title = binding.titleEdit.text.toString()
        val note = binding.noteEdit.text.toString()
        //val date = binding.noteEdit.text.toString()

        if (title.isEmpty()){
            Toast.makeText(requireContext(),"Please fill the title",Toast.LENGTH_LONG).show()
        }else if (note.isEmpty()){
            Toast.makeText(requireContext(),"Please fill the Note",Toast.LENGTH_LONG).show()
        }else{
            noteData = args.note
            when(noteData){
                null->{
                    noteviewModel.saveDataToRoom(title,note,formattedDate)
                    result = "Note Saved"
                    setFragmentResult("key",
                    bundleOf("bundlekey" to result)
                    )
                    navControler.navigate(UpdateFragmentDirections.actionUpdateFragmentToNoteFragment())
                }
                else ->{
                    //update Note
                    updateNote()
                    navControler.popBackStack()
                }
            }

        }
    }

    private fun updateNote() {
        if(noteData!=null){
            val noteObj = NoteData(binding.titleEdit.text.toString(),binding.noteEdit.text.toString(),formattedDate,
                noteData!!.id)
            noteviewModel.saveNote(noteObj)
        }
    }

    private fun showDatePicker() {
        // Create a DatePickerDialog
        requireView().hideKeybord()
        val datePickerDialog = DatePickerDialog(
            requireContext(), {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Create a new Calendar instance to hold the selected date
                val selectedDate = Calendar.getInstance()
                // Set the selected date using the values received from the DatePicker dialog
                selectedDate.set(year, monthOfYear, dayOfMonth)
                // Create a SimpleDateFormat to format the date as "dd/MM/yyyy"
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                // Format the selected date into a string
                val formattedDate = dateFormat.format(selectedDate.time)
                // Update the TextView to display the selected date with the "Selected Date: " prefix
                //binding.datePicker.setText("$formattedDate")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()
    }
}