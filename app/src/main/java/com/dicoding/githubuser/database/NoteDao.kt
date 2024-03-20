package com.dicoding.githubuser.database;

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(note: Note)

    @Update
    fun update(note: Note)


    @Query("SELECT * from Favorite")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM Favorite WHERE username = :username")
    fun getNoteByUsername(username: String): LiveData<Note?>

    @Query("DELETE FROM Favorite WHERE username = :username")
    fun deleteByUsername(username: String)

}
