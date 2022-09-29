package com.example.android.hackernews.newslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.hackernews.R
import com.example.android.hackernews.databinding.FragmentNewsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsListFragment: Fragment() {
    private lateinit var binding: FragmentNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)

        binding.topAppBar.setOnMenuItemClickListener {handleMenuItems(it)}
        return binding.root
    }

    // TODO: finish menu
    private fun handleMenuItems(menu: MenuItem): Boolean {
        return when (menu.itemId) {
            R.id.search -> true
            R.id.filter_news -> true
            R.id.settings -> {
                findNavController().navigate(
                    NewsListFragmentDirections.actionNewsListFragmentToSettingsActivity()
                )
                true
            }
            else -> false
        }
    }
}