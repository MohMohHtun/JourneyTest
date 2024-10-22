package com.moh.journeytest.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moh.journeytest.databinding.ActivityMainBinding
import com.moh.journeytest.network.NetworkUtils
import com.moh.journeytest.viewmodel.CommentViewModel
import com.moh.journeytest.viewmodel.PostViewModel
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: PostViewModel by viewModels()
    private val commentViewModel: CommentViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        handleLoading()

        // Collect the StateFlow using `lifecycleScope` and `repeatOnLifecycle`
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredPosts.collect { posts ->
                    Log.d("MainActivity", "submitList  posts : $posts")
                    adapter.submitList(posts)
                }
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                viewModel.setSearchQuery(searchText ?: "")
                return true
            }
        })
        viewModel.fetchPosts()
        commentViewModel.fetchAllComments()

    }

    private fun handleLoading() {
        // Observe the posts loading state
        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.loadingText.visibility = View.VISIBLE
                if (!NetworkUtils.isInternetAvailable(this)) {
                    binding.loadingText.text = "No Internet Connection"
                }
                binding.postRv.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.loadingText.visibility = View.GONE
                binding.postRv.visibility = View.VISIBLE
            }
        })

        // Observe the comments loading state
        commentViewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.loadingText.visibility = View.VISIBLE
                if (!NetworkUtils.isInternetAvailable(this)) {
                    binding.loadingText.text = "No Internet Connection"
                }
                binding.postRv.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.loadingText.visibility = View.GONE
                binding.postRv.visibility = View.VISIBLE
            }
        })
    }

    private fun setupRecyclerView() {
        // Initialize the adapter and handle item click events
        adapter = PostAdapter { post ->
            // Item click listener, navigate to DetailActivity
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra(
                "postId",
                post.id
            )  // Pass the clicked post's id to the comment detail view
            startActivity(intent)
        }

        binding.postRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
        adapter.notifyDataSetChanged()
    }
}