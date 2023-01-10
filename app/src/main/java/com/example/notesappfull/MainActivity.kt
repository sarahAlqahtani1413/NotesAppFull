package com.example.notesappfull

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesappfull.databinding.ActivityMainBinding
import com.example.notesappfull.databinding.DialogUpdateNoteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var listNotes = arrayListOf<Note>()

    private val databaseHelper by lazy { DatabaseHelper(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()

        binding.btnSubmit.setOnClickListener {
            val name = binding.editNoteMessage.text.toString()
            databaseHelper.saveData(name)
            binding.editNoteMessage.text.clear()
            getData()
        }


    }

    private fun getData() {
        listNotes = databaseHelper.getAllNotes()!!
        binding.recNotes.adapter = NotesAdapter(this, listNotes) { note, status ->
            if (status == "Update") {
                showDialog(note)
            } else if (status == "Delete") {
                delete(note)
            }
        }

        for (item in listNotes) {
            Log.d("TAG", "getData: ${item.name}")
            Log.d("TAG", "getData: ${item.noteId}")
        }
    }


    private fun delete(note: Note) {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to delete this note")
            .setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id ->
                    databaseHelper.deleteNote(note)
                    getData()
                    dialog.dismiss()
                })
            .setNegativeButton("No", null)
            .show()
    }

    private fun update(note: Note) {
        databaseHelper.updateNote(note)
    }


    private fun showDialog(oldNote: Note) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = DialogUpdateNoteBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.btnSave.setOnClickListener {
            if (dialogBinding.editText.text.toString().isNotEmpty()) {
                val note = Note(oldNote.noteId, dialogBinding.editText.text.toString())
                update(note)
                getData()
                dialog.dismiss()
            } else {
                dialogBinding.editText.error = "Can not add empty note"
            }
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}