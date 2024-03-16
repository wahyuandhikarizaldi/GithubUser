package com.dicoding.githubuser.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.data.remote.response.GithubResponse
import com.dicoding.githubuser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)

        val layoutManager = LinearLayoutManager(this)
        binding.rvUser.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUser.addItemDecoration(itemDecoration)

        mainViewModel.github.observe(this) { githubResponse ->
            updateUI(githubResponse)
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->
                val query = searchView.text.toString().trim()
                if (query.isNotEmpty()) {
                    mainViewModel.findUser(query)
                    searchView.hide()
                    searchBar.setText(query)
                    return@setOnEditorActionListener true
                }
                else {
                    Toast.makeText(this@MainActivity, "Mohon masukkan username", Toast.LENGTH_SHORT).show()
                }

                return@setOnEditorActionListener false
            }
        }

    }

    private fun updateUI(githubResponse: GithubResponse) {
        val items = githubResponse.items
        if (items.isEmpty()) {
            binding.tvNotFound.visibility = View.VISIBLE
            binding.rvUser.visibility = View.GONE
        } else {
            binding.tvNotFound.visibility = View.GONE
            binding.rvUser.visibility = View.VISIBLE

            val adapter = Adapter()
            adapter.submitList(items)
            binding.rvUser.adapter = adapter

        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}