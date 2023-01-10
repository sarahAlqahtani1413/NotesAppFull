package com.example.notesappfull

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull

class DatabaseHelper(context: Context): SQLiteOpenHelper(context,"notes.db", null, 1) {
    private val sqLiteDatabase: SQLiteDatabase = writableDatabase

    override fun onCreate(db: SQLiteDatabase?) {
        if(db != null){
            db.execSQL("create table notes (noteId INTEGER PRIMARY KEY AUTOINCREMENT, note TEXT)")
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun saveData(note: String){
        val contentValues = ContentValues()
        contentValues.put("note", note)
        sqLiteDatabase.insert("notes", null, contentValues)
    }

    fun getAllNotes(): ArrayList<Note>? {
        var list: ArrayList<Note> = ArrayList()
        try {
            val query = "SELECT * FROM notes"
            val cursor: Cursor = sqLiteDatabase.rawQuery(query, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val id = cursor.getIntOrNull(cursor.getColumnIndex("noteId"))
                val name = cursor.getStringOrNull(cursor.getColumnIndex("note"))
                val note = Note(id,name)
                list.add(note)
                cursor.moveToNext()
            }
            cursor.close()
        } catch (e: Exception) {
            list = ArrayList()
        }
        return list
    }

    fun updateNote(note: Note): Boolean {
        val values = ContentValues()
        values.put("note", note.name)
        val result: Int =
            sqLiteDatabase.update("notes", values, "noteId=" + note.noteId, null)
        return result == 1
    }


    fun deleteNote(note: Note): Boolean {
        val args = arrayOf(note.noteId.toString())
        val result: Int = sqLiteDatabase.delete("notes", "noteId=?", args)
        return result > 0
    }


}