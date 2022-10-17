package com.example.android.hackernews.commentslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.hackernews.commentslist.adapter.CommentHeader
import com.example.android.hackernews.databinding.FragmentCommentsBinding
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsListFragment: Fragment() {
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

        // TODO: get child NewsItems from repository for recycler view
        val adapter = GroupieAdapter()
        binding.commentsListRecyclerView.adapter = adapter

        // TODO: update NewsItem header item
    }

    // TODO: create menu for refresh and link

    private fun populateAdapter() {
        TODO()
    }
}