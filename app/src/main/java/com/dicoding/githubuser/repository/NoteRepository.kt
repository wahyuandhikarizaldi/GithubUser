package com.dicoding.githubuser.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.githubuser.database.Note
import com.dicoding.githubuser.database.NoteDao
import com.dicoding.githubuser.database.NoteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NoteRepository(application: Application) {
    private val mNotesDao: NoteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = NoteRoomDatabase.getDatabase(application)
        mNotesDao = db.noteDao()
    }

    fun getAllNotes(): LiveData<List<Note>> = mNotesDao.getAllNotes()

    fun insert(note: Note) {
        executorService.execute { mNotesDao.insert(note) }
    }

    fun deleteByUsername(username: String) {
        executorService.execute { mNotesDao.deleteByUsername(username) }
    }

    fun getNoteByUsername(username: String): LiveData<Note?> {
        return mNotesDao.getNoteByUsername(username)
    }

}