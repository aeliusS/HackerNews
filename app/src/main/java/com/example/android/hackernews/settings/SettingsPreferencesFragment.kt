package com.example.android.hackernews.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.android.hackernews.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsPreferencesFragment : PreferenceFragmentCompat() {
    companion object {
        private const val TAG = "SettingsFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        Log.d(TAG, "Reached settings fragment")
        findPreference<SwitchPreferenceCompat>("notifications")
            ?.onPreferenceChangeListener = updateNotifications

    }

    private val updateNotifications = Preference.OnPreferenceChangeListener {_, newValue ->
        Log.d(TAG, "notification new value is $newValue")
        true
    }

    // TODO: Implement dynamic theming
    // https://medium.com/@nihitb06.dev/implementing-dynamic-theming-in-android-a9ed4f5010a8
}