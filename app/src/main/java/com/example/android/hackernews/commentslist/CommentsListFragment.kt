package com.example.android.hackernews.commentslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.hackernews.databinding.FragmentCommentsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsListFragment: Fragment() {
    private lateinit var binding: FragmentCommentsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }
}