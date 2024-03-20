package com.dicoding.githubuser.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.remote.response.DetailResponse
import com.dicoding.githubuser.databinding.ActivityDetailBinding
import com.dicoding.githubuser.helper.ViewModelFactory
import com.dicoding.githubuser.repository.NoteRepository
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView

class DetailActivity : AppCompatActivity() {

    private lateinit var mNoteRepository: NoteRepository
    private var _activityDetailBinding: ActivityDetailBinding? = null
    private val binding get() = _activityDetailBinding

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2,
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar4)
        toolbar.title = ""

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val detailViewModel = obtainViewModel(this@DetailActivity)

        val username = intent.getStringExtra("username")

        detailViewModel.findDetail(username ?: "")

        detailViewModel.detail.observe(this) { detailResponse ->
            updateUI(detailResponse)
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        if (username != null) {
            sectionsPagerAdapter.username = username
        }
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f

        mNoteRepository = NoteRepository(application)
        mNoteRepository.getNoteByUsername(username!!).observe(this) { note ->
            Log.d("DetailActivity", "note: $note")
            if (note != null) {
                binding?.btnSubmit?.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                binding?.btnSubmit?.setImageResource(R.drawable.baseline_favorite_border_24)
            }
        }

        binding?.btnSubmit?.setOnClickListener {
            val noteUsername = detailViewModel.detail.value?.login
            val noteImage = detailViewModel.detail.value?.avatarUrl
            if (noteUsername != null) {
                detailViewModel.handleNoteAction(this@DetailActivity, noteUsername, noteImage)
            }


        }

    }

    private fun updateUI(detailResponse: DetailResponse) {
        binding?.tvLogin?.text = detailResponse.login
        binding?.tvName?.text = detailResponse.name
        binding?.tvFollowers?.text = detailResponse.followers.toString()
        binding?.tvFollowing?.text = detailResponse.following.toString()
        val imageView = binding?.imageView
        if (imageView is CircleImageView) {
            Glide.with(this)
                .load(detailResponse.avatarUrl)
                .into(imageView)
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityDetailBinding = null
    }

    private fun obtainViewModel(activity: AppCompatActivity): DetailViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(DetailViewModel::class.java)
    }

}