package com.example.android.hackernews.settings

import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat
import com.example.android.hackernews.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        private const val TAG = "SettingsFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        Log.d(TAG, "Reached settings fragment")
    }
}