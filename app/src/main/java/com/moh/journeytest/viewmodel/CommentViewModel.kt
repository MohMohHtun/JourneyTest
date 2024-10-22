package com.moh.journeytest.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moh.journeytest.model.Comment
import com.moh.journeytest.model.Post
import com.moh.journeytest.repository.PostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(private val repository: PostsRepository) : ViewModel() {
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> get() = _comments

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _searchCmtQuery = MutableStateFlow("")
    val searchCmtQuery: StateFlow<String> = _searchCmtQuery

    val filteredComments: StateFlow<List<Comment>> = searchCmtQuery
        .debounce(300)
        .combine(comments) { query, comments ->
            Log.d("CommentActivity", "submitList : query: $query : filteredComments : ${comments}")

            if (query.isEmpty()) comments else comments.filter {
                it.name?.contains(query, ignoreCase = true)
                        ?: true || it.email?.contains(query, ignoreCase = true)
                        ?: true || it.body?.contains(query, ignoreCase = true) ?: true
            }

        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchCmtQuery.value = query
    }

    fun fetchComments(postId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _isLoading.postValue(true)
                val comments = try {
                    repository.getComments(postId)  // Fetch from API and save to DB
                } catch (e: Exception) {
                    repository.getCommentsFromDatabase(postId)  // Fetch from local DB in case of an error
                }
                Log.d("CommentActivity", "postId : ${postId} \ncomments : ${comments}")
                _comments.value = comments
                if (comments.isNotEmpty()) {
                    _isLoading.postValue(false)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchAllComments() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _isLoading.postValue(true)
                // Try to fetch data from API, fallback to local database if API call fails
                val comments = try {
                    repository.getAllComments()  // Fetch from API and save to DB
                } catch (e: Exception) {
                    repository.getAllCommentsFromDatabase()  // Fetch from local DB in case of an error
                }
                Log.d("CommentActivity", "All comments : ${comments}")
                _comments.value = comments
                if (comments.isNotEmpty()) {
                    _isLoading.postValue(false)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
