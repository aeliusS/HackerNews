package com.example.android.hackernews.newslist

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.android.hackernews.R
import com.example.android.hackernews.databinding.FragmentNewsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsListFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding
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
        setupMenuOptions()

        binding.viewModel = viewModel

        val adapter = NewsListAdapter(NewsClickListener { newsItem ->
            this.findNavController().navigate(
                NewsListFragmentDirections.actionNewsListFragmentToCommentsListFragment(newsItem)
            )
        })
        binding.newsListRecyclerView.adapter = adapter

        /*
        // set lift on scroll view id
        requireActivity()
            .findViewById<AppBarLayout>(R.id.appBarLayout)
            .liftOnScrollTargetViewId = binding.newsListRecyclerView.id
         */

        // TODO: remove after testing
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

            // TODO: finish menu
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.search -> true
                    R.id.filter_news -> true
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

    companion object {
        private const val TAG = "NewsListFragment"
    }
}