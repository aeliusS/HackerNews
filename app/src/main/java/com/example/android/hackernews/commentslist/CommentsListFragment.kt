package com.example.android.hackernews.commentslist

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
import com.example.android.hackernews.R
import com.example.android.hackernews.commentslist.adapter.ExpandableComment
import com.example.android.hackernews.data.ApiStatus
import com.example.android.hackernews.databinding.FragmentCommentsBinding
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.OnItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommentsListFragment : Fragment() {
    private lateinit var binding: FragmentCommentsBinding
    private val viewModel: CommentsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel

        setupMenuOptions()
        getHeaderAndComments()

        val adapter = GroupieAdapter().apply {
            setOnItemClickListener(onCommentClickListener)
        }
        binding.commentsListRecyclerView.adapter = adapter

        viewModel.apiStatus.observe(viewLifecycleOwner) { handleApiStatus(it) }
    }

    private fun setupMenuOptions() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.comments_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.refresh_comments -> {
                        getHeaderAndComments()
                        true
                    }
                    R.id.open_link -> {
                        viewModel.headerItem.value?.let { newsItem ->
                            newsItem.url?.let {
                                openWebPage(it)
                            }
                        }
                        true
                    }
                    else -> false
                }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun getHeaderAndComments() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateHeaderAndComments()
            Log.d(TAG, "finished updating header item and comments")
        }
    }

    private val onCommentClickListener = OnItemClickListener { item, _ ->
        if (item is ExpandableComment) {
            Log.d(TAG, "Clicked on comment: ${item.newsItem.id}")
            viewModel.toggleIsExpanded(newsItem = item.newsItem)
        }
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
                        getHeaderAndComments()
                    }
                    .show()
                viewModel.finishedDisplayingApiErrorMessage()
            }
            else -> {}
        }
    }

    companion object {
        private const val TAG = "CommentsListFragment"
    }
}