package com.moh.journeytest.ui

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
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
import com.moh.journeytest.model.Post
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

        setSupportActionBar(binding.toolbar)
        // Enable the "back" button in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // Change the back icon color
        binding.toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.black))

        // Retrieve the passed Item object
        val post: Post? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("post", Post::class.java)
        } else {
            intent.getParcelableExtra("post")
        }
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
//        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(searchText: String?): Boolean {
//                viewModel.setSearchQuery(searchText ?: "")
//                return true
//            }
//        })
        post?.let {
            supportActionBar?.title = "PostId : ${it.id}"
            binding.userIdTV.text =  "UserId : ${it.userId}"
            binding.titleTV.text = it.title
            binding.bodyTV.text = it.body
            viewModel.fetchComments(it.id)
        }
    }

    private fun handleLoading() {
        // Observe the loading state
        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                if (!NetworkUtils.isInternetAvailable(this)) {
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

    // Handle back button press in the toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle back action when up button is pressed
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // Inflate the menu to add SearchView to Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_comment, menu)

        // Get the search item from the menu
        val searchItem = menu?.findItem(R.id.action_search)

        // Get the SearchView from the search item
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search comments"

        // Listen for query text changes
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                // Filter the list based on the search query
                viewModel.setSearchQuery(searchText ?: "")
                return true
            }
        })

        return true
    }

}