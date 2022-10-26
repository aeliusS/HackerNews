package com.example.android.hackernews.commentslist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.android.hackernews.R
import com.example.android.hackernews.commentslist.adapter.ExpandableComment
import com.example.android.hackernews.data.ApiStatus
import com.example.android.hackernews.databinding.FragmentCommentsBinding
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
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

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel

        setupMenuOptions()
        getHeaderAndComments()

        val adapter = GroupieAdapter().apply {
            setOnItemLongClickListener(onCommentClickListener)
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
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun getHeaderAndComments() {
        rotateRefreshIcon()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateHeaderAndComments()
            Log.d(TAG, "finished updating header item and comments")
        }
    }

    private val onCommentClickListener = OnItemLongClickListener { item, _ ->
        if (item is ExpandableComment) {
            Log.d(TAG, "Long clicked on comment: ${item.newsItem.id}")
            viewModel.toggleIsExpanded(newsItem = item.newsItem)
            return@OnItemLongClickListener true
        }
        false
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

    private fun rotateRefreshIcon() {
        if (viewModel.apiStatus.value == ApiStatus.LOADING) {
            Toast.makeText(context, R.string.update_in_progress, Toast.LENGTH_SHORT)
                .show()
        } else {
            // TODO: why is the id refresh_news not refresh_comments
            val refreshIcon = requireActivity().findViewById<View?>(R.id.refresh_comments)
                ?: requireActivity().findViewById(R.id.refresh_news) ?: return
            val animator = ObjectAnimator.ofFloat(refreshIcon, View.ROTATION, -360f, 0f)
            animator.duration = 1000
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (viewModel.apiStatus.value == ApiStatus.LOADING) animator.start()
                }
            })
            animator.start()
        }
    }

    companion object {
        private const val TAG = "CommentsListFragment"
    }
}