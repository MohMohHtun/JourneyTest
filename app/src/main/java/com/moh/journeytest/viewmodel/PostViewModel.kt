package com.moh.journeytest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class PostViewModel @Inject constructor(private val repository: PostsRepository) : ViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isFromLocal = MutableLiveData<Boolean>()
    val isFromLocal: LiveData<Boolean> get() = _isFromLocal

    val filteredPosts: StateFlow<List<Post>> = searchQuery
        .debounce(300)
        .combine(posts) { query, posts ->
            if (query.isEmpty()) posts else posts.filter { it.title?.contains(query, ignoreCase = true)
                ?: true || it.body?.contains(query, ignoreCase = true)
                    ?: true }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun fetchPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _isLoading.postValue(true)
                _isFromLocal.postValue(false)
                // Try to fetch data from API, fallback to local database if API call fails
                val posts = try {
                    repository.refreshPosts()  // Fetch from API and save to DB
                } catch (e: Exception) {
                    repository.getPostsFromDatabase().apply {
                        if(this.isNotEmpty()){
                            _isFromLocal.postValue(true)
                        }
                    }  // Fetch from local DB in case of an error
                }
                _posts.value = posts
                if(posts.isNotEmpty()){
                    _isLoading.postValue(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
