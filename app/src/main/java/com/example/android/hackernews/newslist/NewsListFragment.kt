package com.example.android.hackernews.newslist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.android.hackernews.R
import com.example.android.hackernews.data.ApiStatus
import com.example.android.hackernews.data.entities.NewsItem
import com.example.android.hackernews.databinding.FragmentNewsBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsListFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding
    private var refreshIcon: View? = null
    var finished = true
    private val viewModel: NewsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the lifecycle owner to the lifecycle of the view
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel

        setupMenuOptions()
        updateTopStories()


        val adapter = NewsListAdapter(NewsClickListener { newsItem, clickType ->
            when (clickType) {
                ClickType.URL -> {
                    if (!newsItem.url.isNullOrBlank()) openWebPage(newsItem.url)
                    else navigateToComment(newsItem)
                }
                ClickType.BODY -> navigateToComment(newsItem)
            }
        })
        binding.newsListRecyclerView.adapter = adapter

        viewModel.apiStatus.observe(viewLifecycleOwner) { handleApiStatus(it) }

        // TODO: remove both after testing
        viewModel.topStories.observe(viewLifecycleOwner) {
            Log.d(TAG, "Number of top stories: ${it.size}")
        }
    }

    private fun setupMenuOptions() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.news_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.refresh_news -> {
                        val refreshIcon = requireActivity().findViewById<View>(R.id.refresh_news)
                        wrapRotateMenuIcon(refreshIcon) {
                            updateTopStories(true)
                        }
                        true
                    }
                    R.id.settings -> {
                        findNavController().navigate(
                            NewsListFragmentDirections.actionNewsListFragmentToSettingsFragment()
                        )
                        true
                    }
                    else -> false
                }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    inline fun <T> wrapRotateMenuIcon(icon: View?, function: () -> T) {
        if (icon == null) Log.d("wrapRotateMenuIcon", "null icon")
        val animator = ObjectAnimator.ofFloat(icon, View.ROTATION, -360f, 0f)
        var finished = false
        animator.duration = 1000
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!finished) animator.start()
            }
        })
        animator.start()
        try {
            function()
        } catch (e: Exception) {
            Log.e("wrapRotateMenuIcon", "Error animating object: ${e.localizedMessage}")
        } finally {
            finished = true
        }
    }

    private fun updateTopStories(force: Boolean = false) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.refreshTopStories(force)
        }
    }

    private fun navigateToComment(newsItem: NewsItem) {
        this.findNavController().navigate(
            NewsListFragmentDirections.actionNewsListFragmentToCommentsListFragment(newsItem)
        )
    }

    private fun openWebPage(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun handleApiStatus(status: ApiStatus) {
        when (status) {
            ApiStatus.ERROR -> {
                Snackbar.make(binding.root, R.string.api_error_message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.try_again) {
                        updateTopStories()
                    }
                    .show()
                viewModel.finishedDisplayingApiErrorMessage()
                finished = true
            }
            ApiStatus.LOADING -> {
                finished = false
            }
            else -> {
                finished = true
            }
        }
    }

    companion object {
        private const val TAG = "NewsListFragment"
    }
}