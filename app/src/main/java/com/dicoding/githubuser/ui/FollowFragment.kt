package com.dicoding.githubuser.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.data.remote.response.FollowersResponseItem
import com.dicoding.githubuser.databinding.FragmentFollowBinding

class FollowFragment : Fragment() {

    private lateinit var binding: FragmentFollowBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = arguments?.getInt(ARG_POSITION, 0)
        val username = arguments?.getString(ARG_USERNAME)

        val followViewModel = ViewModelProvider(this).get(FollowViewModel::class.java)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvUser.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvUser.addItemDecoration(itemDecoration)

        followViewModel.follow.observe(viewLifecycleOwner) { followersList ->
            updateUI(followersList)
        }

        followViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        if (username != null) {
            if (position == 1){
                followViewModel.findFollowers(username)
            } else {
                followViewModel.findFollowing(username)
            }

        }


    }

    private fun updateUI(followersList: List<FollowersResponseItem>) {
        if (followersList.isEmpty()) {
            binding.tvNotFound.visibility = View.VISIBLE
            binding.rvUser.visibility = View.GONE
        } else {
            binding.tvNotFound.visibility = View.GONE
            binding.rvUser.visibility = View.VISIBLE

            val adapter = FollowAdapter()
            adapter.submitList(followersList)
            binding.rvUser.adapter = adapter
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ARG_POSITION = "position"
        const val ARG_USERNAME = "username"
    }
}
