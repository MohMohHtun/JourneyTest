package com.moh.journeytest.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moh.journeytest.R
import com.moh.journeytest.databinding.ActivityCommentBinding
import com.moh.journeytest.databinding.ActivityMainBinding
import com.moh.journeytest.model.Comment
import com.moh.journeytest.network.NetworkUtils
import com.moh.journeytest.viewmodel.CommentViewModel
import com.moh.journeytest.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private val viewModel: CommentViewModel by viewModels()
    private lateinit var adapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        handleLoading()
//        // Observe the LiveData and submit data to the adapter
//        viewModel.comments.observe(this) { comments ->
//            adapter.submitList(comments)
//        }
        // Collect the StateFlow using `lifecycleScope` and `repeatOnLifecycle`
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredComments.collect { comments ->
                    Log.d("CommentActivity", "submitList  : $comments")
                    adapter.submitList(comments)

                }
            }
        }
        adapter.notifyDataSetChanged()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                viewModel.setSearchQuery(searchText ?: "")
                return true
            }
        })
        viewModel.fetchComments(this.intent.getIntExtra("postId",0))
    }

    private fun handleLoading(){
        // Observe the loading state
        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                if(!NetworkUtils.isInternetAvailable(this)){
                    binding.loadingText.text = "No Internet Connection"
                }
                binding.commentRv.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.loadingText.visibility = View.GONE
                binding.commentRv.visibility = View.VISIBLE
            }
        })
    }
    private fun setupRecyclerView() {
        // Initialize the adapter and handle item click events
        adapter = CommentAdapter()
        binding.commentRv.apply {
            layoutManager = LinearLayoutManager(this@CommentActivity)
            adapter = this@CommentActivity.adapter
        }
        adapter.notifyDataSetChanged()
    }
}