package com.example.android.hackernews.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.android.hackernews.R
import com.example.android.hackernews.BuildConfig
import com.example.android.hackernews.utils.setupRecurringWork
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsPreferencesFragment : PreferenceFragmentCompat() {
    companion object {
        private const val TAG = "SettingsFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<ListPreference>("update_interval")?.onPreferenceChangeListener =
            updateRefreshInterval

        findPreference<SwitchPreferenceCompat>("notifications")?.apply {
            onPreferenceChangeListener = updateNotifications
            findPreference<EditTextPreference>("keyword_search")?.isVisible = isChecked
        }
    }

    private val updateRefreshInterval = Preference.OnPreferenceChangeListener { _, newValue ->
        Log.d(TAG, "New update interval is $newValue")
        setupRecurringWork(requireContext(), true)
        true
    }

    private val updateNotifications = Preference.OnPreferenceChangeListener { _, newValue ->
        if (newValue == true) {
            requestNotificationPermission()
        }
        findPreference<EditTextPreference>("keyword_search")?.isVisible = newValue == true
        true
    }

    private fun notificationPermissionApproved(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            true
        }
    }

    @SuppressLint("InlinedApi")
    private fun requestNotificationPermission() {
        if (notificationPermissionApproved()) return
        requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        findPreference<SwitchPreferenceCompat>("notifications")?.isChecked = isGranted
        findPreference<EditTextPreference>("keyword_search")?.isVisible = isGranted
        if (!isGranted) {
            Snackbar.make(
                listView,
                getString(R.string.notification_permission_denied),
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.settings) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        }
    }


    // TODO: Implement dynamic theming
    // https://medium.com/@nihitb06.dev/implementing-dynamic-theming-in-android-a9ed4f5010a8
}