package com.dicoding.githubuser.ui

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.remote.response.DetailResponse
import com.dicoding.githubuser.data.remote.retrofit.ApiConfig
import com.dicoding.githubuser.database.Note
import com.dicoding.githubuser.repository.NoteRepository
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class DetailViewModel(private val application: Application) : ViewModel() {

    private val _detail = MutableLiveData<DetailResponse>()
    val detail: LiveData<DetailResponse> = _detail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _existingNote = MutableLiveData<Note?>()
    val existingNote: LiveData<Note?> = _existingNote

    private val mNoteRepository: NoteRepository = NoteRepository(application)

    companion object{
        private const val TAG = "DetailViewModel"
    }

    fun findDetail(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetail(username)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _detail.value = response.body()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun insert(note: Note) {
        mNoteRepository.insert(note)
    }

    fun handleNoteAction(owner: LifecycleOwner, username: String, avatarUrl: String?) {
        _existingNote.postValue(null)

        var actionCompleted = false

        mNoteRepository.getNoteByUsername(username).observe(owner) { note ->
            if (!actionCompleted) {
                if (note == null) {
                    val newNote = Note().apply {
                        this.username = username
                        this.avatarUrl = avatarUrl
                    }
                    insert(newNote)
                    showToast(R.string.added)
                } else {
                    mNoteRepository.deleteByUsername(username)
                    showToast(R.string.removed)
                }

                actionCompleted = true
            }
        }
    }

    private fun showToast(message: Int) {
        Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
    }

}
