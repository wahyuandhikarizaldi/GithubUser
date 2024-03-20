package com.dicoding.githubuser.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.remote.response.GithubResponse
import com.dicoding.githubuser.databinding.ActivityMainBinding
import com.dicoding.githubuser.helper.SettingPreferences
import com.dicoding.githubuser.helper.ViewModelFactory
import com.dicoding.githubuser.helper.dataStore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val pref = SettingPreferences.getInstance(application.dataStore)
        val viewModelFactory = ViewModelFactory.getInstance(application, pref)
        val mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)


        val layoutManager = LinearLayoutManager(this)
        binding.rvUser.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUser.addItemDecoration(itemDecoration)

        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

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

            searchBar.inflateMenu(R.menu.option_menu)
            searchBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu1 -> {
                        val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.menu2 -> {
                        val intent = Intent(this@MainActivity, SwitchActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }

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